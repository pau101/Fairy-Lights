package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.BlockFastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class BlockFastenerAccessor implements FastenerAccessor {
	private BlockPos pos = BlockPos.ZERO;

	public BlockFastenerAccessor() {}

	public BlockFastenerAccessor(BlockFastener fastener) {
		this(fastener.getPos());
	}

	public BlockFastenerAccessor(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public Fastener<?> get(World world) {
		// FIXME
		return world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
	}

	@Override
	public boolean isLoaded(World world) {
		if (world.isBlockLoaded(pos)) {
			TileEntity entity = world.getTileEntity(pos);
			return entity != null && !entity.isRemoved();
		}
		return false;
	}

	@Override
	public boolean exists(World world) {
		return !world.isBlockLoaded(pos) || world.getTileEntity(pos) != null;
	}

	@Override
	public FastenerType getType() {
		return FastenerType.BLOCK;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof BlockFastenerAccessor) {
			return pos.equals(((BlockFastenerAccessor) obj).pos);
		}
		return false;
	}

	@Override
	public CompoundNBT serialize() {
		return NBTUtil.writeBlockPos(pos);
	}

	@Override
	public void deserialize(CompoundNBT nbt) {
		pos = NBTUtil.readBlockPos(nbt);
	}
}
