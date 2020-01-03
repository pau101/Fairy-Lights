package com.pau101.fairylights.client.renderer;

import com.pau101.fairylights.server.block.FLBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	public World getWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public BlockPos getPos() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return player == null ? BlockPos.ORIGIN : new BlockPos(player);
	}

	@Override
	public Block getBlockType() {
		return FLBlocks.FASTENER;
	}

	@Override
	public int getBlockMetadata() {
		return 0;
	}
}
