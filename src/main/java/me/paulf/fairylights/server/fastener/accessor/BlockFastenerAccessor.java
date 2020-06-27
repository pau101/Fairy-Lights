package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.BlockFastener;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

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
    public LazyOptional<Fastener<?>> get(final World world, final boolean load) {
        if (load || world.isBlockPresent(this.pos)) {
            final TileEntity entity = world.getTileEntity(this.pos);
            if (entity != null) {
                return entity.getCapability(CapabilityHandler.FASTENER_CAP);
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public boolean exists(final World world) {
        if (world.isRemote || !world.isBlockPresent(this.pos)) return true;
        final TileEntity entity = world.getTileEntity(this.pos);
        return entity != null && entity.getCapability(CapabilityHandler.FASTENER_CAP).isPresent();
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
