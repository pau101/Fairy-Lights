package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorBlock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class FastenerBlock extends FastenerDefault {
	private final BlockEntityFastener fastener;

	public FastenerBlock(BlockEntityFastener fastener) {
		this.fastener = fastener;
		bounds = new AxisAlignedBB(fastener.getPos());
		setWorld(fastener.getWorld());
	}

	@Override
	public EnumFacing getFacing() {
		return fastener.getFacing();
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public Vec3d getOffsetPoint() {
		return FairyLights.fastener.getOffset(fastener.getFacing(), 0.125F);
	}

	@Override
	public BlockPos getPos() {
		return fastener.getPos();
	}

	@Override
	public Vec3d getAbsolutePos() {
		return new Vec3d(getPos());
	}

	@Override
	public FastenerAccessorBlock createAccessor() {
		return new FastenerAccessorBlock(this);
	}

	@Override
	public String toString() {
		BlockPos pos = fastener.getPos();
		return pos.getX() + " " + pos.getY() + " " + pos.getZ();
	}
}
