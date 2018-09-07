package com.pau101.fairylights.server.block;

import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.ServerEventHandler;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorBlock;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockFastener extends BlockContainer {
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

	private static final AxisAlignedBB BOUNDS_DOWN = createAABB(EnumFacing.DOWN);

	private static final AxisAlignedBB BOUNDS_UP = createAABB(EnumFacing.UP);

	private static final AxisAlignedBB BOUNDS_NORTH = createAABB(EnumFacing.NORTH);

	private static final AxisAlignedBB BOUNDS_SOUTH = createAABB(EnumFacing.SOUTH);

	private static final AxisAlignedBB BOUNDS_WEST = createAABB(EnumFacing.WEST);

	private static final AxisAlignedBB BOUNDS_EAST = createAABB(EnumFacing.EAST);

	private static final AxisAlignedBB[] BOUNDS = { BOUNDS_DOWN, BOUNDS_UP, BOUNDS_NORTH, BOUNDS_SOUTH, BOUNDS_WEST, BOUNDS_EAST };

	public BlockFastener() {
		super(Material.CIRCUITS);
		setDefaultState(blockState.getBaseState()
			.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(TRIGGERED, false)
		);
		setResistance(2000);
		Utils.name(this, "fastener");
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, TRIGGERED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() | (state.getValue(TRIGGERED) ? 0b1000 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
			.withProperty(FACING, EnumFacing.byIndex(meta & 0b0111))
			.withProperty(TRIGGERED, (meta & 0b1000) != 0);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS[state.getValue(FACING).ordinal()];
	}

	@Override
	public TileEntity createNewTileEntity(World world, int data) {
		return new BlockEntityFastener();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return Items.AIR;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null).dropItems(world, pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState blockState = world.getBlockState(pos);
		Block blockPlacingOn = blockState.getBlock();
		return world.isSideSolid(pos, side) || blockPlacingOn instanceof BlockSlab && (side.getAxis() != Axis.Y || side == EnumFacing.DOWN && blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) || blockPlacingOn instanceof BlockLeaves || blockPlacingOn instanceof BlockStairs;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (world.isBlockPowered(pos.offset(state.getValue(FACING).getOpposite()))) {
			world.setBlockState(pos, state.withProperty(TRIGGERED, true), 3);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		EnumFacing facing = state.getValue(FACING);
		BlockPos blockOnPos = pos.offset(facing.getOpposite());
		if (canPlaceBlockOnSide(world, blockOnPos, facing)) {
			boolean receivingPower = world.isBlockPowered(pos);
			boolean isPowered = state.getValue(TRIGGERED);
			if (receivingPower && !isPowered) {
				world.scheduleUpdate(pos, this, tickRate(world));
				world.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
			} else if (!receivingPower && isPowered) {
				world.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
			}
		} else {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		Fastener<?> fastener = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null);
		int level = 0;
		for (Entry<UUID, Connection> e : fastener.getConnections().entrySet()) {
			Connection connection = e.getValue();
			if (connection.getType() != ConnectionType.HANGING_LIGHTS) {
				continue;
			}
			if (!connection.getDestination().isLoaded(world)) {
				continue;
			}
			if (!connection.isOrigin()) {
				BlockPos to = connection.getDestination().get(world).getPos();
				fastener = world.getTileEntity(to).getCapability(CapabilityHandler.FASTENER_CAP, null);
				connection = fastener.getConnections().get(e.getKey());
				if (connection == null) {
					continue;
				}
			}
			ConnectionHangingLights logic = (ConnectionHangingLights) connection;
			int lvl = (int) Math.ceil(logic.getJingleProgress() * 15);
			if (lvl > level) {
				level = lvl;
			}
		}
		return level;
	}

	@Override
	public int tickRate(World world) {
		return 2;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote) {
			jingle(world, pos);
		}
	}

	private boolean jingle(World world, BlockPos pos) {
		Fastener<?> fastener = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null);
		for (Entry<UUID, Connection> e : fastener.getConnections().entrySet()) {
			Connection connection = e.getValue();
			if (connection.getType() != ConnectionType.HANGING_LIGHTS) {
				continue;
			}
			if (!connection.getDestination().isLoaded(world)) {
				continue;
			}
			BlockPos to = connection.getDestination().get(world).getPos();
			if (!connection.isDestination(new FastenerAccessorBlock(to))) {
				continue;
			}
			if (!world.getBlockState(to).getValue(TRIGGERED)) {
				continue;
			}
			if (!connection.isOrigin()) {
				fastener = world.getTileEntity(to).getCapability(CapabilityHandler.FASTENER_CAP, null);
				connection = fastener.getConnections().get(e.getKey());
				if (connection == null) {
					continue;
				}
			}
			ConnectionHangingLights logic = (ConnectionHangingLights) connection;
			if (!logic.canCurrentlyPlayAJingle()) {
				continue;
			}
			if (ServerEventHandler.tryJingle(world, connection, logic, FairyLights.randomJingles)) {
				return true;
			}
		}
		return false;
	}

	public Vec3d getOffset(EnumFacing facing, float offset) {
		return getFastenerOffset(facing, offset);
	}

	private static AxisAlignedBB createAABB(EnumFacing facing) {
		Vec3d offset = getFastenerOffset(facing, 0);
		double x = offset.x, y = offset.y, z = offset.z;
		return new AxisAlignedBB(x, y, z, x + 0.25, y + 0.25, z + 0.25);
	}

	public static Vec3d getFastenerOffset(EnumFacing facing, float offset) {
		double x = offset, y = offset, z = offset;
		switch (facing) {
			case DOWN:
				y += 0.75F;
			case UP:
				x += 0.375F;
				z += 0.375F;
				break;
			case WEST:
				x += 0.75F;
			case EAST:
				z += 0.375F;
				y += 0.375F;
				break;
			case NORTH:
				z += 0.75F;
			case SOUTH:
				x += 0.375F;
				y += 0.375F;
		}
		return new Vec3d(x, y, z);
	}
}
