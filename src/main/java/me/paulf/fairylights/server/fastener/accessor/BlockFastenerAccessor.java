package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.*;
import me.paulf.fairylights.server.fastener.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.*;

public final class BlockFastenerAccessor implements FastenerAccessor {
    private BlockPos pos = BlockPos.ZERO;

    public BlockFastenerAccessor() {}

    public BlockFastenerAccessor(final BlockFastener fastener) {
        this(fastener.getPos());
    }

    public BlockFastenerAccessor(final BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Fastener<?> get(final World world) {
        // FIXME
        return world.getTileEntity(this.pos).getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
    }

    @Override
    public boolean isLoaded(final World world) {
        if (world.isBlockLoaded(this.pos)) {
            final TileEntity entity = world.getTileEntity(this.pos);
            return entity != null && !entity.isRemoved();
        }
        return false;
    }

    @Override
    public boolean exists(final World world) {
        return !world.isBlockLoaded(this.pos) || world.getTileEntity(this.pos) != null;
    }

    @Override
    public FastenerType getType() {
        return FastenerType.BLOCK;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BlockFastenerAccessor) {
            return this.pos.equals(((BlockFastenerAccessor) obj).pos);
        }
        return false;
    }

    @Override
    public CompoundNBT serialize() {
        return NBTUtil.writeBlockPos(this.pos);
    }

    @Override
    public void deserialize(final CompoundNBT nbt) {
        this.pos = NBTUtil.readBlockPos(nbt);
    }
}
