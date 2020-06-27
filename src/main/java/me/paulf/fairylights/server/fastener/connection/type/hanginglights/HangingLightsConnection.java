package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import com.google.common.base.Throwables;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorldLightManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;

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

    private final Set<BlockPos> litBlocks = new HashSet<>();

    private final Set<BlockPos> oldLitBlocks = new HashSet<>();

    private int lightUpdateTime = (int) (Math.random() * LIGHT_UPDATE_WAIT / 2);

    private int lightUpdateIndex;

    public HangingLightsConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
    }

    public HangingLightsConnection(final World world, final Fastener<?> fastenerOrigin, final UUID uuid) {
        super(world, fastenerOrigin, uuid);
        this.pattern = new ArrayList<>();
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.HANGING_LIGHTS;
    }

    @Nullable
    public Jingle getPlayingJingle() {
        return this.jinglePlayer.getJingle();
    }

    public void play(final JingleLibrary library, final Jingle jingle, final int lightOffset) {
        this.jinglePlayer.play(library, jingle, lightOffset);
    }

    @Override
    public boolean interact(final PlayerEntity player, final Vec3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        if (featureType == FEATURE && OreDictUtils.isDye(heldStack)) {
            final int index = feature % this.pattern.size();
            final ColoredLightVariant light = this.pattern.get(index);
            final DyeColor color = DyeColor.getColor(heldStack);
            if (light.getColor() != color) {
                this.pattern.set(index, light.withColor(color));
                this.dataUpdateState = true;
                heldStack.shrink(1);
                this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
                return true;
            }
        }
        if (super.interact(player, hit, featureType, feature, heldStack, hand)) {
            return true;
        }
        this.isOn = !this.isOn;
        final SoundEvent lightSnd;
        final float pitch;
        if (this.isOn) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.orElseThrow(IllegalStateException::new);
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.orElseThrow(IllegalStateException::new);
            pitch = 0.5F;
        }
        this.world.playSound(null, hit.x, hit.y, hit.z, lightSnd, SoundCategory.BLOCKS, 1, pitch);
        this.computeCatenary();
        return true;
    }

    @Override
    public void onUpdateLate() {
        this.jinglePlayer.tick(this.world, this.fastener.getConnectionPoint(), this.features, this.world.isRemote);
        final boolean playing = this.jinglePlayer.isPlaying();
        if (playing || this.wasPlaying) {
            this.updateNeighbors(this.fastener);
            if (this.getDestination().isLoaded(this.world)) {
                this.updateNeighbors(this.getDestination().get(this.world));
            }
        }
        this.wasPlaying = playing;
        final boolean on = !this.isDynamic() && this.isOn;
        for (int i = 0; i < this.features.length; i++) {
            final Light light = this.features[i];
            light.tick(this, this.twinkle, on);
        }
        if (on && this.isOrigin() && this.features.length > 0) {
            this.lightUpdateTime++;
            if (this.lightUpdateTime > LIGHT_UPDATE_WAIT && this.lightUpdateTime % LIGHT_UPDATE_RATE == 0) {
                if (this.lightUpdateIndex >= this.features.length) {
                    this.lightUpdateIndex = 0;
                    this.lightUpdateTime = this.world.rand.nextInt(LIGHT_UPDATE_WAIT / 2);
                } else {
                    this.setLight(new BlockPos(this.features[this.lightUpdateIndex++].getAbsolutePoint(this.fastener)));
                }
            }
        }
    }

    private void updateNeighbors(final Fastener<?> fastener) {
        this.world.updateComparatorOutputLevel(fastener.getPos(), FLBlocks.FASTENER.orElseThrow(IllegalStateException::new));
    }

    @Override
    protected Light[] createFeatures(final int length) {
        return new Light[length];
    }

    @Override
    protected Light createFeature(final int index, final Vec3d point, final Vec3d rotation) {
        final boolean on = !this.isDynamic() && this.isOn;
        final Light light = new Light(index, point, rotation, on);
        if (on && this.isOrigin()) {
            final BlockPos pos = new BlockPos(light.getAbsolutePoint(this.fastener));
            this.litBlocks.add(pos);
            this.setLight(pos);
        }
        if (this.pattern.size() > 0) {
            final ColoredLightVariant lightData = this.pattern.get(index % this.pattern.size());
            light.setVariant(lightData.getVariant());
            light.setColor(LightItem.getColorValue(lightData.getColor()));
        }
        return light;
    }

    @Override
    protected float getFeatureSpacing() {
        if (this.pattern.isEmpty()) {
            return LightVariant.FAIRY.getSpacing();
        }
        float spacing = 0;
        for (final ColoredLightVariant patternLightData : this.pattern) {
            final float lightSpacing = patternLightData.getVariant().getSpacing();
            if (lightSpacing > spacing) {
                spacing = lightSpacing;
            }
        }
        return spacing;
    }

    @Override
    protected void onBeforeUpdateFeatures(final int size) {
        final Iterator<BlockPos> litIter = this.litBlocks.iterator();
        while (litIter.hasNext()) {
            this.oldLitBlocks.add(litIter.next());
            litIter.remove();
        }
    }

    @Override
    protected void onAfterUpdateFeatures(final int size) {
        this.oldLitBlocks.removeAll(this.litBlocks);
        final Iterator<BlockPos> oldIter = this.oldLitBlocks.iterator();
        while (oldIter.hasNext()) {
            this.world.getChunkProvider().getLightManager().checkBlock(oldIter.next());
            oldIter.remove();
        }
    }

    @Override
    public void onRemove() {
        for (final BlockPos pos : this.litBlocks) {
            this.world.getChunkProvider().getLightManager().checkBlock(pos);
        }
    }

    private static final Method SET_LIGHT = ObfuscationReflectionHelper.findMethod(LightEngine.class, "func_215623_a", BlockPos.class, int.class);

    private static final Method ADD_TASK;

    private static final Object POST_PHASE;

    static {
        final Class<?> phaseType;
        try {
            phaseType = Class.forName("net.minecraft.world.server.ServerWorldLightManager$Phase");
        } catch (final ClassNotFoundException e) {
            throw new Error(e);
        }
        ADD_TASK = ObfuscationReflectionHelper.findMethod(ServerWorldLightManager.class, "func_215586_a", int.class, int.class, phaseType, Runnable.class);
        POST_PHASE = phaseType.getEnumConstants()[1];
    }

    private void setLight(final BlockPos pos) {
        if (this.world.isAirBlock(pos) && this.world.getLightFor(LightType.BLOCK, pos) < MAX_LIGHT) {
            final WorldLightManager manager = this.world.getChunkProvider().getLightManager();
            final IWorldLightListener light = manager.getLightEngine(LightType.BLOCK);
            if (light instanceof LightEngine) {
                final LightEngine engine = (LightEngine) light;
                if (manager instanceof ServerWorldLightManager) {
                    try {
                        ADD_TASK.invoke(manager, pos.getX() >> 4, pos.getZ() >> 4, POST_PHASE, Util.namedRunnable(() -> {
                            try {
                                SET_LIGHT.invoke(engine, pos, MAX_LIGHT);
                            } catch (final IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }, () -> "setLight " + pos));
                    } catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        SET_LIGHT.invoke(engine, pos, MAX_LIGHT);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (final InvocationTargetException e) {
                        Throwables.throwIfInstanceOf(e, Error.class);
                        LogManager.getLogger().warn("Exception setting light", e.getCause());
                    }
                }
            }
        }
    }

    public boolean canCurrentlyPlayAJingle() {
        return !this.jinglePlayer.isPlaying();
    }

    public float getJingleProgress() {
        return this.jinglePlayer.getProgress();
    }

    @Override
    public CompoundNBT serialize() {
        final CompoundNBT compound = super.serialize();
        compound.put("jinglePlayer", this.jinglePlayer.serialize());
        compound.putBoolean("isOn", this.isOn);
        return compound;
    }

    @Override
    public void deserialize(final CompoundNBT compound) {
        super.deserialize(compound);
        if (this.jinglePlayer == null) {
            this.jinglePlayer = new JinglePlayer();
        }
        if (!this.jinglePlayer.isPlaying()) {
            this.jinglePlayer.deserialize(compound.getCompound("jinglePlayer"));
        }
        this.isOn = compound.getBoolean("isOn");
    }

    @Override
    public CompoundNBT serializeLogic() {
        final CompoundNBT compound = super.serializeLogic();
        final ListNBT tagList = new ListNBT();
        for (final ColoredLightVariant light : this.pattern) {
            tagList.add(light.serialize());
        }
        compound.put("pattern", tagList);
        compound.putBoolean("twinkle", this.twinkle);
        compound.putBoolean("tight", this.slack == 0);
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        final ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
        this.pattern = new ArrayList<>();
        for (int i = 0; i < patternList.size(); i++) {
            final CompoundNBT lightCompound = patternList.getCompound(i);
            this.pattern.add(ColoredLightVariant.from(lightCompound));
        }
        this.twinkle = compound.getBoolean("twinkle");
        if (compound.getBoolean("tight")) {
            this.slack = 0;
        }
    }
}
