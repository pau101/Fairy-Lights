package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.server.item.HangingLightsConnectionItem;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JinglePlayer;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.server.string.StringTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorldLightManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class HangingLightsConnection extends HangingFeatureConnection<Light<?>> {
    private static final int MAX_LIGHT = 15;

    private static final int LIGHT_UPDATE_WAIT = 400;

    private static final int LIGHT_UPDATE_RATE = 10;

    private StringType string;

    private List<ItemStack> pattern;

    private JinglePlayer jinglePlayer = new JinglePlayer();

    private boolean wasPlaying = false;

    private boolean isOn = true;

    private final Set<BlockPos> litBlocks = new HashSet<>();

    private final Set<BlockPos> oldLitBlocks = new HashSet<>();

    private int lightUpdateTime = (int) (Math.random() * LIGHT_UPDATE_WAIT / 2);

    private int lightUpdateIndex;

    public HangingLightsConnection(final ConnectionType<? extends HangingLightsConnection> type, final World world, final Fastener<?> fastenerOrigin, final UUID uuid) {
        super(type, world, fastenerOrigin, uuid);
        this.string = StringTypes.BLACK_STRING.get();
        this.pattern = new ArrayList<>();
    }

    public StringType getString() {
        return this.string;
    }

    @Nullable
    public Jingle getPlayingJingle() {
        return this.jinglePlayer.getJingle();
    }

    public void play(final Jingle jingle, final int lightOffset) {
        this.jinglePlayer.play(jingle, lightOffset);
    }

    @Override
    public boolean interact(final PlayerEntity player, final Vector3d hit, final FeatureType featureType, final int feature, final ItemStack heldStack, final Hand hand) {
        if (featureType == FEATURE && heldStack.getItem().isIn(FLCraftingRecipes.LIGHTS)) {
            final int index = feature % this.pattern.size();
            final ItemStack light = this.pattern.get(index);
            if (!ItemStack.areItemStacksEqual(light, heldStack)) {
                final ItemStack placed = heldStack.split(1);
                this.pattern.set(index, placed);
                ItemHandlerHelper.giveItemToPlayer(player, light);
                this.computeCatenary();
                this.world.playSound(null, hit.x, hit.y, hit.z, FLSounds.FEATURE_COLOR_CHANGE.get(), SoundCategory.BLOCKS, 1, 1);
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
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.get();
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.get();
            pitch = 0.5F;
        }
        this.world.playSound(null, hit.x, hit.y, hit.z, lightSnd, SoundCategory.BLOCKS, 1, pitch);
        this.computeCatenary();
        return true;
    }

    @Override
    public void onUpdate() {
        this.jinglePlayer.tick(this.world, this.fastener.getConnectionPoint(), this.features, this.world.isRemote);
        final boolean playing = this.jinglePlayer.isPlaying();
        if (playing || this.wasPlaying) {
            this.updateNeighbors(this.fastener);
            this.getDestination().get(this.world, false).ifPresent(this::updateNeighbors);
        }
        this.wasPlaying = playing;
        final boolean on = !this.isDynamic() && this.isOn;
        for (final Light<?> light : this.features) {
            light.tick(this.world, this.fastener.getConnectionPoint());
        }
        if (on && this.features.length > 0) {
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
        this.world.updateComparatorOutputLevel(fastener.getPos(), FLBlocks.FASTENER.get());
    }

    @Override
    protected Light<?>[] createFeatures(final int length) {
        return new Light<?>[length];
    }

    @Override
    protected boolean canReuse(final Light<?> feature, final int index) {
        return ItemStack.areItemStacksEqual(feature.getItem(), this.getPatternStack(index));
    }

    @Override
    protected Light<?> createFeature(final int index, final Vector3d point, final float yaw, final float pitch) {
        final ItemStack lightData = this.getPatternStack(index);
        return this.createLight(index, point, yaw, pitch, lightData, LightVariant.get(lightData).orElse(SimpleLightVariant.FAIRY_LIGHT));
    }

    private ItemStack getPatternStack(final int index) {
        return this.pattern.isEmpty() ? ItemStack.EMPTY : this.pattern.get(index % this.pattern.size());
    }

    @Override
    protected void updateFeature(final Light<?> light) {
        super.updateFeature(light);
        if (!this.isDynamic() && this.isOn) {
            final BlockPos pos = new BlockPos(light.getAbsolutePoint(this.fastener));
            this.litBlocks.add(pos);
            this.setLight(pos);
        }
    }

    private <T extends LightBehavior> Light<T> createLight(final int index, final Vector3d point, final float yaw, final float pitch, final ItemStack stack, final LightVariant<T> variant) {
        return new Light<>(index, point, yaw, pitch, stack, variant, 0.125F);
    }

    @Override
    protected float getFeatureSpacing() {
        if (this.pattern.isEmpty()) {
            return SimpleLightVariant.FAIRY_LIGHT.getSpacing();
        }
        float spacing = 0;
        for (final ItemStack patternLightData : this.pattern) {
            final float lightSpacing = LightVariant.get(patternLightData).orElse(SimpleLightVariant.FAIRY_LIGHT).getSpacing();
            if (lightSpacing > spacing) {
                spacing = lightSpacing;
            }
        }
        return spacing;
    }

    @Override
    protected void onBeforeUpdateFeatures() {
        final Iterator<BlockPos> litIter = this.litBlocks.iterator();
        while (litIter.hasNext()) {
            this.oldLitBlocks.add(litIter.next());
            litIter.remove();
        }
    }

    @Override
    protected void onAfterUpdateFeatures() {
        final boolean on = !this.isDynamic() && this.isOn;
        for (final Light<?> light : this.features) {
            light.power(on, this.isDynamic() || this.prevCatenary == null);
        }
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
        if (this.world.isBlockPresent(pos) && this.world.isAirBlock(pos) && this.world.getLightFor(LightType.BLOCK, pos) < MAX_LIGHT) {
            final WorldLightManager manager = this.world.getChunkProvider().getLightManager();
            final IWorldLightListener light = manager.getLightEngine(LightType.BLOCK);
            if (light instanceof BlockLightEngine) {
                if (manager instanceof ServerWorldLightManager) {
                    try {
                        ADD_TASK.invoke(manager, pos.getX() >> 4, pos.getZ() >> 4, POST_PHASE, Util.namedRunnable(() -> {
                            ((BlockLightEngine) light).func_215623_a(pos, MAX_LIGHT);
                        }, () -> "setLight " + pos));
                    } catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ((BlockLightEngine) light).func_215623_a(pos, MAX_LIGHT);
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
        HangingLightsConnectionItem.setString(compound, this.string);
        final ListNBT tagList = new ListNBT();
        for (final ItemStack light : this.pattern) {
            tagList.add(light.write(new CompoundNBT()));
        }
        compound.put("pattern", tagList);
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.string = HangingLightsConnectionItem.getString(compound);
        final ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
        this.pattern = new ArrayList<>();
        for (int i = 0; i < patternList.size(); i++) {
            final CompoundNBT lightCompound = patternList.getCompound(i);
            this.pattern.add(ItemStack.read(lightCompound));
        }
    }
}
