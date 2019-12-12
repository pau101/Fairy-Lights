package me.paulf.fairylights.client.renderer;

import me.paulf.fairylights.server.block.FLBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

public final class FenceFastenerRepresentative extends TileEntity {
	private FenceFastenerRepresentative() {
		super(TileEntityType.FURNACE);
	}

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
	public BlockState getBlockState() {
		return FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getDefaultState();
	}

	@Override
	public boolean hasWorld() {
		return true;
	}

	@Override
	public TileEntityType<?> getType() {
		// FIXME
		return new TileEntityType<FurnaceTileEntity>(null, null, null) {
			@Override
			public boolean isValidBlock(final Block block) {
				return true;
			}
		};
	}
}
