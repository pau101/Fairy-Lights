package me.paulf.fairylights.server.entity;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.io.IOException;

public final class FenceFastenerEntity extends HangingEntity implements IEntityAdditionalSpawnData {
    private int surfaceCheckTime;

    public FenceFastenerEntity(final EntityType<? extends FenceFastenerEntity> type, final Level world) {
        super(type, world);
    }

    public FenceFastenerEntity(final Level world) {
        this(FLEntities.FASTENER.get(), world);
    }

    public FenceFastenerEntity(final Level world, final BlockPos pos) {
        this(world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public float getEyeHeight(final Pose pose, final EntityDimensions size) {
        /*
         * Because this entity is inside of a block when
         * EntityLivingBase#canEntityBeSeen performs its
         * raytracing it will always return false during
         * NetHandlerPlayServer#processUseEntity, making
         * the player reach distance be limited at three
         * blocks as opposed to the standard six blocks.
         * EntityLivingBase#canEntityBeSeen will add the
         * value given by getEyeHeight to the y position
         * of the entity to calculate the end point from
         * which to raytrace to. Returning one lets most
         * interactions with a player succeed, typically
         * for breaking the connection or creating a new
         * connection. I hope you enjoy my line lengths.
         */
        return 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBrightness() {
        final BlockPos pos = this.getPos();
        if (this.level.isLoaded(pos)) {
            return this.level.getBrightness(pos);
        }
        return 0;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        return distance < 4096;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean survives() {
        return !this.level.isLoaded(this.pos) || ConnectionItem.isFence(this.level.getBlockState(this.pos));
    }

    @Override
    public void remove(final RemovalReason reason) {
        this.getFastener().ifPresent(Fastener::remove);
        super.remove(reason);
    }

    // Copy from super but remove() moved to after onBroken()
    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.level.isClientSide() && this.isAlive()) {
            this.markHurt();
            this.dropItem(source.getEntity());
            this.remove(RemovalReason.KILLED);
        }
        return true;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void dropItem(@Nullable final Entity breaker) {
        this.getFastener().ifPresent(fastener -> fastener.dropItems(this.level, this.pos));
        if (breaker != null) {
            this.level.levelEvent(2001, this.pos, Block.getId(FLBlocks.FASTENER.get().defaultBlockState()));
        }
    }

    @Override
    public void playPlacementSound() {
        final SoundType sound = FLBlocks.FASTENER.get().getSoundType(FLBlocks.FASTENER.get().defaultBlockState(), this.level, this.getPos(), null);
        this.playSound(sound.getPlaceSound(), (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.BLOCKS;
    }

    @Override
    public void setPos(final double x, final double y, final double z) {
        super.setPos(Mth.floor(x) + 0.5, Mth.floor(y) + 0.5, Mth.floor(z) + 0.5);
    }

    @Override
    public void setDirection(final Direction facing) {}

    @Override
    protected void recalculateBoundingBox() {
        final double posX = this.pos.getX() + 0.5;
        final double posY = this.pos.getY() + 0.5;
        final double posZ = this.pos.getZ() + 0.5;
        this.setPosRaw(posX, posY, posZ);
        final float w = 3 / 16F;
        final float h = 3 / 16F;
        this.setBoundingBox(new AABB(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getFastener().map(fastener -> fastener.getBounds().inflate(1)).orElseGet(super::getBoundingBoxForCulling);
    }

    @Override
    public void tick() {
        this.getFastener().ifPresent(fastener -> {
            if (!this.level.isClientSide() && (fastener.hasNoConnections() || this.checkSurface())) {
                this.dropItem(null);
                this.remove(RemovalReason.DISCARDED);
            } else if (fastener.update() && !this.level.isClientSide()) {
                final UpdateEntityFastenerMessage msg = new UpdateEntityFastenerMessage(this, fastener.serializeNBT());
                ServerProxy.sendToPlayersWatchingEntity(msg, this);
            }
        });
    }

    private boolean checkSurface() {
        if (this.surfaceCheckTime++ == 100) {
            this.surfaceCheckTime = 0;
            return !this.survives();
        }
        return false;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ConnectionItem) {
            if (this.level.isClientSide()) {
                player.swing(hand);
            } else {
                this.getFastener().ifPresent(fastener -> ((ConnectionItem) stack.getItem()).connect(stack, player, this.level, fastener));
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag compound) {
        compound.put("pos", NbtUtils.writeBlockPos(this.pos));
    }

    @Override
    public void readAdditionalSaveData(final CompoundTag compound) {
        this.pos = NbtUtils.readBlockPos(compound.getCompound("pos"));
    }

    @Override
    public void writeSpawnData(final FriendlyByteBuf buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                NbtIo.write(fastener.serializeNBT(), new ByteBufOutputStream(buf));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void readSpawnData(final FriendlyByteBuf buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                fastener.deserializeNBT(NbtIo.read(new ByteBufInputStream(buf), new NbtAccounter(0x200000)));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }

    public static FenceFastenerEntity create(final Level world, final BlockPos fence) {
        final FenceFastenerEntity fastener = new FenceFastenerEntity(world, fence);
        //fastener.forceSpawn = true;
        world.addFreshEntity(fastener);
        fastener.playPlacementSound();
        return fastener;
    }

    @Nullable
    public static FenceFastenerEntity find(final Level world, final BlockPos pos) {
        final HangingEntity entity = findHanging(world, pos);
        if (entity instanceof FenceFastenerEntity) {
            return (FenceFastenerEntity) entity;
        }
        return null;
    }

    @Nullable
    public static HangingEntity findHanging(final Level world, final BlockPos pos) {
        for (final HangingEntity e : world.getEntitiesOfClass(HangingEntity.class, new AABB(pos).inflate(2))) {
            if (e.getPos().equals(pos)) {
                return e;
            }
        }
        return null;
    }
}
