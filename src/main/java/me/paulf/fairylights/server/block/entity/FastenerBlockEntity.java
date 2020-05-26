package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.capability.*;
import me.paulf.fairylights.server.fastener.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;

public final class FastenerBlockEntity extends TileEntity implements ITickableTileEntity {
    public FastenerBlockEntity() {
        super(FLBlockEntities.FASTENER.orElseThrow(IllegalStateException::new));
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().grow(1)).orElseGet(super::getRenderBoundingBox);
    }

    public Vec3d getOffset() {
        return FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getOffset(this.getFacing(), 0.125F);
    }

    public Direction getFacing() {
        final BlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() != FLBlocks.FASTENER.orElseThrow(IllegalStateException::new)) {
            return Direction.UP;
        }
        return state.get(FastenerBlock.FACING);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public void setWorldAndPos(final World world, final BlockPos pos) {
        super.setWorldAndPos(world, pos);
        this.getFastener().ifPresent(fastener -> fastener.setWorld(world));
    }

    @Override
    public void tick() {
        this.getFastener().ifPresent(fastener -> {
            if (!this.world.isRemote && fastener.hasNoConnections()) {
                this.world.removeBlock(this.pos, false);
            } else if (!this.world.isRemote && fastener.update()) {
                this.markDirty();
                final BlockState state = this.world.getBlockState(this.pos);
                this.world.notifyBlockUpdate(this.pos, state, state, 3);
            }
        });
    }

    @Override
    public void remove() {
        this.getFastener().ifPresent(Fastener::remove);
        super.remove();
    }

    @Override
    public void onChunkUnloaded() {
        this.getFastener().ifPresent(Fastener::remove);
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }
}
