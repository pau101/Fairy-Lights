package com.pau101.fairylights.server.fastener.connection.type.hanginglights;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.FeatureType;
import com.pau101.fairylights.server.fastener.connection.type.ConnectionHangingFeature;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.server.jingle.Jingle;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.jingle.JinglePlayer;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.OreDictUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public final class ConnectionHangingLights extends ConnectionHangingFeature<Light> {
	private static final int LIGHT_VALUE = 15;

	private static final int LIGHT_UPDATE_WAIT = 400;

	private static final int LIGHT_UPDATE_RATE = 10;

	private List<ColoredLightVariant> pattern;

	private boolean twinkle;

	private JinglePlayer jinglePlayer = new JinglePlayer();

	private boolean wasPlaying = false;

	private boolean isOn = true;

	private Set<BlockPos> litBlocks = new HashSet<>();

	private Set<BlockPos> oldLitBlocks = new HashSet<>();

	private int lightUpdateTime = (int) (Math.random() * LIGHT_UPDATE_WAIT / 2);

	private int lightUpdateIndex;

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

	@Nullable
	public Jingle getPlayingJingle() {
		return jinglePlayer.getJingle();
	}

	public void play(JingleLibrary library, Jingle jingle, int lightOffset) {
		jinglePlayer.start(library, jingle, lightOffset);
	}

	@Override
	public boolean interact(EntityPlayer player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, EnumHand hand) {
		if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
			int index = feature % pattern.size();
			ColoredLightVariant light = pattern.get(index);
			EnumDyeColor color = EnumDyeColor.byDyeDamage(OreDictUtils.getDyeMetadata(heldStack));
			if (light.getColor() != color) {
				pattern.set(index, light.withColor(color));
				dataUpdateState = true;
				heldStack.stackSize--;
				world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, FLSounds.FEATURE_COLOR_CHANGE, SoundCategory.BLOCKS, 1, 1);
				return true;
			}
		}
		if (super.interact(player, hit, featureType, feature, heldStack, hand)) {
			return true;
		}
		isOn = !isOn;
		SoundEvent lightSnd;
		float pitch;
		if (isOn) {
			lightSnd = FLSounds.FEATURE_LIGHT_TURNON;
			pitch = 0.6F;
		} else {
			lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF;
			pitch = 0.5F;
		}
		world.playSound(null, hit.xCoord, hit.yCoord, hit.zCoord, lightSnd, SoundCategory.BLOCKS, 1, pitch);
		computeCatenary();
		return true;
	}

	@Override
	public void onUpdateLate() {
		boolean playing = jinglePlayer.isPlaying();
		if (playing) {
			jinglePlayer.tick(world, fastener.getConnectionPoint(), features, world.isRemote);
		}
		if (playing || wasPlaying && !playing) {
			updateNeighbors(fastener);
			if (getDestination().isLoaded(world)) {
				updateNeighbors(getDestination().get(world));
			}
		}
		wasPlaying = playing;
		boolean on = !isDynamic() && isOn;
		for (int i = 0; i < features.length; i++) {
			Light light = features[i];
			light.tick(this, twinkle, on);
		}
		if (on && isOrigin() && features.length > 0) {
			lightUpdateTime++;
			if (lightUpdateTime > LIGHT_UPDATE_WAIT && lightUpdateTime % LIGHT_UPDATE_RATE == 0) {
				if (lightUpdateIndex >= features.length) {
					lightUpdateIndex = 0;
					lightUpdateTime = world.rand.nextInt(LIGHT_UPDATE_WAIT / 2);
				} else {
					setLight(new BlockPos(features[lightUpdateIndex++].getAbsolutePoint(fastener)), LIGHT_VALUE);
				}
			}	
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
		boolean on = !isDynamic() && isOn;
		Light light = new Light(index, point, rotation, on);
		if (on && isOrigin()) {
			BlockPos pos = new BlockPos(light.getAbsolutePoint(fastener));
			litBlocks.add(pos);
			setLight(pos, LIGHT_VALUE);
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
		if (pattern.isEmpty()) {
			return LightVariant.FAIRY.getSpacing();
		}
		float spacing = 0;
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
		Iterator<BlockPos> litIter = litBlocks.iterator();
		while (litIter.hasNext()) {
			oldLitBlocks.add(litIter.next());
			litIter.remove();
		}
	}

	@Override
	protected void onAfterUpdateFeatures(int size) {
		oldLitBlocks.removeAll(litBlocks);
		Iterator<BlockPos> oldIter = oldLitBlocks.iterator();
		while (oldIter.hasNext()) {
			world.checkLightFor(EnumSkyBlock.BLOCK, oldIter.next());
			oldIter.remove();
		}
	}

	@Override
	public void onRemove() {
		for (BlockPos pos : litBlocks) {
			world.checkLightFor(EnumSkyBlock.BLOCK, pos);
		}
	}

	private void setLight(BlockPos pos, int value) {
		if (world.isAirBlock(pos) && world.getLightFor(EnumSkyBlock.BLOCK, pos) != value) { 
			world.setLightFor(EnumSkyBlock.BLOCK, pos, value);
			for (EnumFacing dir : EnumFacing.values()) {
				updateLight(pos.offset(dir), value);
			}
		}
	}

	private void updateLight(BlockPos pos, int value) {
		if (world.getLightFor(EnumSkyBlock.BLOCK, pos) != value) {
			world.checkLightFor(EnumSkyBlock.BLOCK, pos);
		}
	}

	public boolean canCurrentlyPlayAJingle() {
		return !jinglePlayer.isPlaying();
	}

	public float getJingleProgress() {
		return jinglePlayer.getProgress();
	}

	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound compound = super.serialize();
		compound.setTag("jinglePlayer", jinglePlayer.serialize());
		compound.setBoolean("isOn", isOn);
		return compound;
	}

	@Override
	public void deserialize(NBTTagCompound compound) {
		super.deserialize(compound);
		if (jinglePlayer == null) {
			jinglePlayer = new JinglePlayer();
		}
		if (!jinglePlayer.isPlaying()) {
			jinglePlayer.deserialize(compound.getCompoundTag("jinglePlayer"));
		}
		isOn = compound.getBoolean("isOn");
	}

	@Override
	public NBTTagCompound serializeLogic() {
		NBTTagCompound compound = super.serializeLogic();
		NBTTagList tagList = new NBTTagList();
		for (ColoredLightVariant light : pattern) {
			tagList.appendTag(light.serialize());
		}
		compound.setTag("pattern", tagList);
		compound.setBoolean("twinkle", twinkle);
		compound.setBoolean("tight", slack == 0);
		return compound;
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {
		super.deserializeLogic(compound);
		NBTTagList patternList = compound.getTagList("pattern", NBT.TAG_COMPOUND);
		pattern = new ArrayList<>();
		for (int i = 0; i < patternList.tagCount(); i++) {
			NBTTagCompound lightCompound = patternList.getCompoundTagAt(i);
			pattern.add(ColoredLightVariant.from(lightCompound));
		}
		twinkle = compound.getBoolean("twinkle");
		if (compound.getBoolean("tight")) {
			slack = 0;
		}
	}
}
