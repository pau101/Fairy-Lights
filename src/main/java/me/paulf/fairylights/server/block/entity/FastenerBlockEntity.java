package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

public final class FastenerBlockEntity extends BlockEntity {
    public FastenerBlockEntity(final BlockPos pos, final BlockState state) {
        super(FLBlockEntities.FASTENER.get(), pos ,state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().inflate(1)).orElseGet(super::getRenderBoundingBox);
    }

    public Vec3 getOffset() {
        return FLBlocks.FASTENER.get().getOffset(this.getFacing(), 0.125F);
    }

    public Direction getFacing() {
        final BlockState state = this.level.getBlockState(this.worldPosition);
        if (state.getBlock() != FLBlocks.FASTENER.get()) {
            return Direction.UP;
        }
        return state.getValue(FastenerBlock.FACING);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void setLevel(final Level world) {
        super.setLevel(world);
        this.getFastener().ifPresent(fastener -> fastener.setWorld(world));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(fastener -> {
            if (!level.isClientSide() && fastener.hasNoConnections()) {
                level.removeBlock(pos, false);
            } else if (!level.isClientSide() && fastener.update()) {
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        });
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, FastenerBlockEntity be) {
        be.getFastener().ifPresent(Fastener::update);
    }

    @Override
    public void setRemoved() {
        this.getFastener().ifPresent(Fastener::remove);
        super.setRemoved();
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }
}
