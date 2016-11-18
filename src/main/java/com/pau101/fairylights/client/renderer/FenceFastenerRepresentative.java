package com.pau101.fairylights.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import com.pau101.fairylights.FairyLights;

public final class FenceFastenerRepresentative extends TileEntity {
	private FenceFastenerRepresentative() {}

	public static final FenceFastenerRepresentative INSTANCE = new FenceFastenerRepresentative();

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return Double.MAX_VALUE;
	}

	@Override
	public Block getBlockType() {
		return FairyLights.fastener;
	}

	@Override
	public int getBlockMetadata() {
		return 0;
	}
}
