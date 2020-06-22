package com.pau101.fairylights.server.fastener.accessor;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerBlock;
import com.pau101.fairylights.server.fastener.FastenerType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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
		if (world.isBlockLoaded(pos, false)) {
			TileEntity entity = world.getTileEntity(pos);
			if (entity != null && entity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) return true;
		}
		return false;
	}

	@Override
	public boolean exists(World world) {
		if (!world.isBlockLoaded(pos, false)) return true;
		TileEntity entity = world.getTileEntity(pos);
		if (entity != null && entity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) return true;
		return false;
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
