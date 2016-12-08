package com.pau101.fairylights.server.fastener.accessor;

import javax.annotation.Nullable;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerBlock;
import com.pau101.fairylights.server.fastener.FastenerType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FastenerAccessorBlock implements FastenerAccessor {
	private BlockPos pos = BlockPos.ORIGIN;

	public FastenerAccessorBlock() {}

	public FastenerAccessorBlock(FastenerBlock fastener) {
		this(fastener.getPos());
	}

	public FastenerAccessorBlock(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public Fastener<?> get(World world) {
		return world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null);
	}

	@Override
	public boolean isLoaded(World world) {
		return world.isBlockLoaded(pos, false) && world.getTileEntity(pos) != null;
	}

	@Override
	public boolean exists(World world) {
		return !world.isBlockLoaded(pos, false) || world.getTileEntity(pos) != null;
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
		if (obj instanceof FastenerAccessorBlock) {
			return pos.equals(((FastenerAccessorBlock) obj).pos);
		}
		return false;
	}

	@Override
	public NBTTagCompound serialize() {
		return NBTUtil.createPosTag(pos);
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		pos = NBTUtil.getPosFromTag(nbt);
	}
}
