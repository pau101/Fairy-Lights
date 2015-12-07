package com.pau101.fairylights.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.vectormath.Point3f;

public class BlockConnectionFastenerFence extends BlockConnectionFastener {
	public BlockConnectionFastenerFence(BlockFence fence) {
		super(fence.getMaterial());
		setHardness(fence.getBlockHardness(null, null));
		setResistance(fence.getExplosionResistance(null) * 5 / 3);
		setStepSound(fence.stepSound);
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return Item.getItemFromBlock(FairyLights.fastenerFenceToNormalFenceMap.get(this));
	}

	@Override
	public Point3f getOffsetForData(EnumFacing facing, float offset) {
		return new Point3f(offset + 0.375F, offset + 0.375F, offset + 0.375F);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[0]);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess blockAccess, BlockPos pos) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
	}
}
