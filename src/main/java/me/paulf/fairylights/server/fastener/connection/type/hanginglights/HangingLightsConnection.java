package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.StandardLightVariant;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import me.paulf.fairylights.server.jingle.JinglePlayer;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.entity.player.PlayerEntity;
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

public final class HangingLightsConnection extends HangingFeatureConnection<Light> {
    private static final int MAX_LIGHT = 15;

    private static final int LIGHT_UPDATE_WAIT = 400;

    private static final int LIGHT_UPDATE_RATE = 10;

    private List<ItemStack> pattern;

    private boolean twinkle;

    private JinglePlayer jinglePlayer = new JinglePlayer();

    private boolean wasPlaying = false;

    private boolean isOn = true;

    private final Set<BlockPos> litBlocks = new HashSet<>();

    private final Set<BlockPos> oldLitBlocks = new HashSet<>();

    private int lightUpdateTime = (int) (Math.random() * LIGHT_UPDATE_WAIT / 2);

    private int lightUpdateIndex;

    public HangingLightsConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound, final boolean drop) {
        super(world, fastener, uuid, destination, isOrigin, compound, drop);
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
        if (featureType == FEATURE && heldStack.getItem().isIn(FLCraftingRecipes.LIGHTS )) {
            final int index = feature % this.pattern.size();
            final ItemStack light = this.pattern.get(index);
            if (!ItemStack.areItemStacksEqual(light, heldStack)) {
                final ItemStack placed = heldStack.split(1);
                this.pattern.set(index, placed);
                ItemHandlerHelper.giveItemToPlayer(player, light);
                this.dataUpdateState = true;
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
    public void onUpdateLate() {
        this.jinglePlayer.tick(this.world, this.fastener.getConnectionPoint(), this.features, this.world.isRemote);
        final boolean playing = this.jinglePlayer.isPlaying();
        if (playing || this.wasPlaying) {
            this.updateNeighbors(this.fastener);
            this.getDestination().get(this.world, false).ifPresent(this::updateNeighbors);
        }
        this.wasPlaying = playing;
        final boolean on = !this.isDynamic() && this.isOn;
        for (final Light light : this.features) {
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
        this.world.updateComparatorOutputLevel(fastener.getPos(), FLBlocks.FASTENER.get());
    }

    @Override
    protected Light[] createFeatures(final int length) {
        return new Light[length];
    }

    @Override
    protected Light createFeature(final int index, final Vec3d point, final float yaw, final float pitch) {
        final boolean on = !this.isDynamic() && this.isOn;
        final ItemStack lightData = this.pattern.isEmpty() ? ItemStack.EMPTY : this.pattern.get(index % this.pattern.size());
        final Light light = new Light(index, point, yaw, pitch, lightData, on);
        if (on && this.isOrigin()) {
            final BlockPos pos = new BlockPos(light.getAbsolutePoint(this.fastener));
            this.litBlocks.add(pos);
            this.setLight(pos);
        }
        return light;
    }

    @Override
    protected float getFeatureSpacing() {
        if (this.pattern.isEmpty()) {
            return StandardLightVariant.FAIRY.getSpacing();
        }
        float spacing = 0;
        for (final ItemStack patternLightData : this.pattern) {
            final float lightSpacing = LightVariant.get(patternLightData).orElse(StandardLightVariant.FAIRY).getSpacing();
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

    private void setLight(final BlockPos pos) {
        if (this.world.isAirBlock(pos) && this.world.getLightFor(LightType.BLOCK, pos) < MAX_LIGHT) {
            final IWorldLightListener light = this.world.getChunkProvider().getLightManager().getLightEngine(LightType.BLOCK);
            if (light instanceof LightEngine) {
                final LightEngine<?, ?> engine = (LightEngine<?, ?>) light;
                try {
                    SET_LIGHT.invoke(engine, pos, MAX_LIGHT);
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
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
        for (final ItemStack light : this.pattern) {
            tagList.add(light.write(new CompoundNBT()));
        }
        compound.put("pattern", tagList);
        compound.putBoolean("twinkle", this.twinkle);
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        final ListNBT patternList = compound.getList("pattern", NBT.TAG_COMPOUND);
        this.pattern = new ArrayList<>();
        for (int i = 0; i < patternList.size(); i++) {
            final CompoundNBT lightCompound = patternList.getCompound(i);
            this.pattern.add(ItemStack.read(lightCompound));
        }
        this.twinkle = compound.getBoolean("twinkle");
    }
}
