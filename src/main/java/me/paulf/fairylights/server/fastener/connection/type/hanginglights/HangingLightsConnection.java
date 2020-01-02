package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import me.paulf.fairylights.server.item.LightItem;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.jingle.JinglePlayer;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.OreDictUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraft.world.lighting.LightEngine;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class HangingLightsConnection extends HangingFeatureConnection<Light> {
	private static final int MAX_LIGHT = 15;

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

	public HangingLightsConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public HangingLightsConnection(World world, Fastener<?> fastenerOrigin, UUID uuid) {
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
		jinglePlayer.play(library, jingle, lightOffset);
	}

	@Override
	public boolean interact(PlayerEntity player, Vec3d hit, FeatureType featureType, int feature, ItemStack heldStack, Hand hand) {
		if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
			int index = feature % pattern.size();
			ColoredLightVariant light = pattern.get(index);
			DyeColor color = DyeColor.getColor(heldStack);
			if (light.getColor() != color) {
				pattern.set(index, light.withColor(color));
				dataUpdateState = true;
				heldStack.shrink(1);
				world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
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
			lightSnd = FLSounds.FEATURE_LIGHT_TURNON.orElseThrow(IllegalStateException::new);
			pitch = 0.6F;
		} else {
			lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.orElseThrow(IllegalStateException::new);
			pitch = 0.5F;
		}
		world.playSound(null, hit.x, hit.y, hit.z, lightSnd, SoundCategory.BLOCKS, 1, pitch);
		computeCatenary();
		return true;
	}

	@Override
	public void onUpdateLate() {
		jinglePlayer.tick(world, fastener.getConnectionPoint(), features, world.isRemote);
		boolean playing = jinglePlayer.isPlaying();
		if (playing || wasPlaying) {
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
					setLight(new BlockPos(features[lightUpdateIndex++].getAbsolutePoint(fastener)));
				}
			}	
		}
	}

	private void updateNeighbors(Fastener<?> fastener) {
		world.updateComparatorOutputLevel(fastener.getPos(), FLBlocks.FASTENER.orElseThrow(IllegalStateException::new));
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
			setLight(pos);
		}
		if (pattern.size() > 0) {
			ColoredLightVariant lightData = pattern.get(index % pattern.size());
			light.setVariant(lightData.getVariant());
			light.setColor(LightItem.getColorValue(lightData.getColor()));
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
			world.getChunkProvider().getLightManager().checkBlock(oldIter.next());
			oldIter.remove();
		}
	}

	@Override
	public void onRemove() {
		for (BlockPos pos : litBlocks) {
			world.getChunkProvider().getLightManager().checkBlock(pos);
		}
	}

	private static final Method SET_LIGHT = ObfuscationReflectionHelper.findMethod(LightEngine.class, "func_215623_a", BlockPos.class, int.class);

	private void setLight(BlockPos pos) {
		if (world.isAirBlock(pos) && world.getLightFor(LightType.BLOCK, pos) < MAX_LIGHT) {
			IWorldLightListener light = world.getChunkProvider().getLightManager().getLightEngine(LightType.BLOCK);
			if (light instanceof LightEngine) {
				LightEngine engine = (LightEngine) light;
				try {
					SET_LIGHT.invoke(engine, pos, MAX_LIGHT);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
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
	public CompoundNBT serialize() {
		CompoundNBT compound = super.serialize();
		compound.put("jinglePlayer", jinglePlayer.serialize());
		compound.putBoolean("isOn", isOn);
		return compound;
	}

	@Override
	public void deserialize(CompoundNBT compound) {
		super.deserialize(compound);
		if (jinglePlayer == null) {
			jinglePlayer = new JinglePlayer();
		}
		if (!jinglePlayer.isPlaying()) {
			jinglePlayer.deserialize(compound.getCompound("jinglePlayer"));
		}
		isOn = compound.getBoolean("isOn");
	}

	@Override
	public CompoundNBT serializeLogic() {
		CompoundNBT compound = super.serializeLogic();
		ListNBT tagList = new ListNBT();
		for (ColoredLightVariant light : pattern) {
			tagList.add(light.serialize());
		}
		compound.put("pattern", tagList);
		compound.putBoolean("twinkle", twinkle);
		compound.putBoolean("tight", slack == 0);
		return compound;
	}

	@Override
	public void deserializeLogic(CompoundNBT compound) {
		super.deserializeLogic(compound);
		ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
		pattern = new ArrayList<>();
		for (int i = 0; i < patternList.size(); i++) {
			CompoundNBT lightCompound = patternList.getCompound(i);
			pattern.add(ColoredLightVariant.from(lightCompound));
		}
		twinkle = compound.getBoolean("twinkle");
		if (compound.getBoolean("tight")) {
			slack = 0;
		}
	}
}
