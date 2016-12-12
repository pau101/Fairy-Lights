package com.pau101.fairylights.server.fastener.connection.type.hanginglights;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.ClientEventHandler;
import com.pau101.fairylights.client.entity.EntityLightSource;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.type.ConnectionHangingFeature;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.jingle.Jingle;
import com.pau101.fairylights.server.jingle.JinglePlayer;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.DyeOreDictUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public final class ConnectionHangingLights extends ConnectionHangingFeature<Light> {
	private List<ColoredLightVariant> pattern;

	private boolean twinkle;

	private boolean tight;

	private final JinglePlayer jinglePlayer = new JinglePlayer();

	@Nullable
	private List<EntityLightSource> lightSources;

	private boolean updateLightSources;

	private boolean wasPlaying = false;

	public ConnectionHangingLights(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public ConnectionHangingLights(World world, Fastener<?> fastenerOrigin, UUID uuid) {
		super(world, fastenerOrigin, uuid);
		pattern = new ArrayList<>();
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.HANGING_LIGHTS;
	}

	@Override
	public Catenary createCatenary(Vec3d to) {
		return Catenary.from(to, tight);
	}

	@Nullable
	public Jingle getPlayingJingle() {
		return jinglePlayer.getJingle();
	}

	public void play(Jingle jingle, int lightOffset) {
		jinglePlayer.start(jingle, lightOffset);
	}

	@Override
	public boolean interact(EntityPlayer player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, EnumHand hand) {
		if (featureType == FEATURE && DyeOreDictUtils.isDye(heldStack)) {
			int index = feature % pattern.size();
			ColoredLightVariant light = pattern.get(index);
			EnumDyeColor color = EnumDyeColor.byDyeDamage(DyeOreDictUtils.getDyeMetadata(heldStack));
			if (light.getColor() != color) {
				pattern.set(index, light.withColor(color));
				dataUpdateState = true;
				heldStack.stackSize--;
				world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.FEATURE_COLOR_CHANGE, SoundCategory.BLOCKS, 1, 1);
				return true;
			}
		}
		return super.interact(player, hit, featureType, feature, heldStack, hand);
	}

	@Override
	public void onUpdateLate() {
		boolean playing = jinglePlayer.isPlaying();
		if (playing) {
			jinglePlayer.tick(world, getFastener().getConnectionPoint(), features, world.isRemote);
		}
		if (playing || wasPlaying && !playing) {
			updateNeighbors(getFastener());
			if (getDestination().isLoaded(world)) {
				updateNeighbors(getDestination().get(world));
			}
		}
		wasPlaying = playing;
		for (int i = 0; i < features.length; i++) {
			Light light = features[i];
			light.tick(this, twinkle);
		}
	}

	private void updateNeighbors(Fastener<?> fastener) {
		world.updateComparatorOutputLevel(fastener.getPos(), FairyLights.fastener);
	}

	@Override
	protected Light[] createFeatures(int length) {
		return new Light[length];
	}

	@Override
	protected Light createFeature(int index, Vec3d point, Vec3d rotation) {
		Light light = new Light(index, point, rotation);
		if (updateLightSources) {
			EntityLightSource src;
			if (index < lightSources.size()) {
				src = lightSources.get(index);
			} else {
				src = new EntityLightSource(world);
				lightSources.add(src);
				world.loadedEntityList.add(src);
				world.onEntityAdded(src);
			}
			Vec3d pos = light.getAbsolutePoint(getFastener());
			src.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
		}
		if (pattern.size() > 0) {
			ColoredLightVariant lightData = pattern.get(index % pattern.size());
			light.setVariant(lightData.getVariant());
			light.setColor(ItemLight.getColorValue(lightData.getColor()));
		}
		return light;
	}

	@Override
	protected float getFeatureSpacing() {
		float spacing = pattern.isEmpty() ? 16 : 0;
		for (ColoredLightVariant patternLightData : pattern) {
			float lightSpacing = patternLightData.getVariant().getSpacing();
			if (lightSpacing > spacing) {
				spacing = lightSpacing;
			}
		}
		return spacing;
	}

	@Override
	protected void onBeforeUpdateFeatures(int size) {
		updateLightSources = world.isRemote && isOrigin() && ClientEventHandler.isDynamicLights();
		if (updateLightSources && lightSources == null) {
			lightSources = new ArrayList<>(size);
		}
	}

	@Override
	protected void onAfterUpdateFeatures(int index) {
		if (updateLightSources) {
			for (; index < lightSources.size(); index++) {
				Entity e = lightSources.remove(index);
				world.loadedEntityList.remove(e);
				world.onEntityRemoved(e);
			}
		}
	}

	@Override
	public void onRemove() {
		if (world.isRemote && lightSources != null) {
			world.loadedEntityList.removeAll(lightSources);
			for (Entity e : lightSources) {
				world.onEntityRemoved(e);
			}
		}
	}

	public boolean canCurrentlyPlayAJingle() {
		return !jinglePlayer.isPlaying();
	}

	public float getJingleProgress() {
		return jinglePlayer.getProgress();
	}

	@Override
	public NBTTagCompound serializeLogic() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList tagList = new NBTTagList();
		for (ColoredLightVariant light : pattern) {
			tagList.appendTag(light.serialize());
		}
		compound.setTag("pattern", tagList);
		compound.setBoolean("twinkle", twinkle);
		compound.setBoolean("tight", tight);
		return compound;
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {
		NBTTagList patternList = compound.getTagList("pattern", NBT.TAG_COMPOUND);
		pattern = new ArrayList<>();
		for (int i = 0; i < patternList.tagCount(); i++) {
			NBTTagCompound lightCompound = patternList.getCompoundTagAt(i);
			pattern.add(ColoredLightVariant.from(lightCompound));
		}
		twinkle = compound.getBoolean("twinkle");
		tight = compound.getBoolean("tight");
	}
}
