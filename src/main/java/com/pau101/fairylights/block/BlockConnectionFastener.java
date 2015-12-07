package com.pau101.fairylights.block;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.item.ItemConnection;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.tileentity.connection.ConnectionPlayer;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.vectormath.Point3f;

public class BlockConnectionFastener extends BlockContainer {
	public static final PropertyDirection FACING_PROP = PropertyDirection.create("facing");

	public BlockConnectionFastener() {
		super(Material.circuits);
		setDefaultState(blockState.getBaseState().withProperty(FACING_PROP, EnumFacing.NORTH));
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 0.25F, 0.625F);
		setResistance(2000);
	}

	public BlockConnectionFastener(Material material) {
		super(material);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { FACING_PROP });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING_PROP, getFacingFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return MathUtils.modi(-((EnumFacing) state.getValue(FACING_PROP)).ordinal(), 6);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) world.getTileEntity(pos);
		if (fastener == null) {
			return;
		}
		Iterator<Connection> connectionIterator = fastener.getConnections().iterator();
		float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			if (!(connection instanceof ConnectionPlayer)) {
				ItemStack itemStack = new ItemStack(connection.getType().getItem(), 1);
				NBTTagCompound tagCompound = new NBTTagCompound();
				connection.writeDetailsToNBT(tagCompound);
				if (!tagCompound.hasNoTags()) {
					itemStack.setTagCompound(tagCompound);
				}
				EntityItem entityItem = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, itemStack);
				float scale = 0.05F;
				entityItem.motionX = world.rand.nextGaussian() * scale;
				entityItem.motionY = world.rand.nextGaussian() * scale + 0.2F;
				entityItem.motionZ = world.rand.nextGaussian() * scale;
				world.spawnEntityInWorld(entityItem);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int data) {
		return new TileEntityConnectionFastener();
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		setBlockBoundsBasedOnState(world, pos);
		return super.getCollisionBoundingBox(world, pos, state);
	}

	@Override
	public Item getItem(World world, BlockPos pos) {
		return FairyLights.fairyLights;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return null;
	}

	public Point3f getOffsetForData(EnumFacing facing, float offset) {
		Point3f offsetPoint = new Point3f(offset, offset, offset);
		switch (facing) {
			case DOWN:
				offsetPoint.y += 0.75F;
			case UP:
				offsetPoint.x += 0.375F;
				offsetPoint.z += 0.375F;
				break;
			case WEST:
				offsetPoint.x += 0.75F;
			case EAST:
				offsetPoint.z += 0.375F;
				offsetPoint.y += 0.375F;
				break;
			case NORTH:
				offsetPoint.z += 0.75F;
			case SOUTH:
				offsetPoint.x += 0.375F;
				offsetPoint.y += 0.375F;
		}
		return offsetPoint;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		return FairyLights.proxy.getConnectionFastenerPickBlock(target, world, pos, this);
	}

	@Override
	public int getRenderType() {
		return 3;
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
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState blockState = world.getBlockState(pos);
		Block blockPlacingOn = blockState.getBlock();
		return world.isSideSolid(pos, side) || blockPlacingOn instanceof BlockSlab && (side.getAxis() != Axis.Y || side == EnumFacing.DOWN && blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) || blockPlacingOn instanceof BlockLeaves || blockPlacingOn instanceof BlockStairs;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().withProperty(FACING_PROP, facing) : getDefaultState().withProperty(FACING_PROP, EnumFacing.DOWN);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fromBlockX, float fromBlockY, float fromBlockZ) {
		ItemStack heldItemStack = player.getHeldItem();
		if (heldItemStack != null && heldItemStack.getItem() instanceof ItemConnection) {
			ItemConnection item = (ItemConnection) heldItemStack.getItem();
			PlayerData data = PlayerData.getPlayerData(player);
			if (data.hasLastClicked()) {
				BlockPos lastClicked = data.getLastClicked();
				TileEntity tileEntity = world.getTileEntity(lastClicked);
				TileEntity tileEntityTo = world.getTileEntity(pos);
				if (tileEntity != tileEntityTo && tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
					TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
					TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
					if (to.hasConnectionWith(from)) {
						return false;
					}
					if (!world.isRemote) {
						from.connectWith(to, item.getConnectionType(), heldItemStack.getTagCompound());
						to.removeConnection(player);
						data.setUnknownLastClicked();
						if (!player.capabilities.isCreativeMode) {
							heldItemStack.stackSize--;
						}
					}
					return true;
				}
			} else {
				if (!world.isRemote) {
					data.setLastClicked(pos);
					TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(pos);
					tileEntity.connectWith(player, item.getConnectionType(), heldItemStack.getTagCompound());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {
		EnumFacing facing = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		BlockPos blockOnPos = pos.offset(facing.getOpposite());
		if (!canPlaceBlockOnSide(world, blockOnPos, facing)) {
			dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		EnumFacing facing = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		Point3f offset = getOffsetForData(facing, 0);
		setBlockBounds(offset.x, offset.y, offset.z, offset.x + 0.25F, offset.y + 0.25F, offset.z + 0.25F);
	}

	public static EnumFacing getFacingFromMeta(int meta) {
		return EnumFacing.values()[MathUtils.modi(-(meta & 7), 6)];
	}
}
