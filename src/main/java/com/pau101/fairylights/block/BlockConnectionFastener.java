package com.pau101.fairylights.block;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.item.ItemConnection;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.tileentity.connection.ConnectionPlayer;
import com.pau101.fairylights.util.mc.EnumFacing;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Point3i;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockConnectionFastener extends BlockContainer {
	public static final int[] SIDE_TO_DATA = { 2, 0, 12, 4, 1, 3 };

	private static final ForgeDirection[] DATA_TO_DIRECTION = { ForgeDirection.UP, ForgeDirection.WEST, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.NORTH, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };

	public static final EnumFacing[] DATA_TO_FACING = { EnumFacing.UP, EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP };

	public BlockConnectionFastener() {
		super(Material.circuits);
		setBlockBounds(0.375f, 0, 0.375f, 0.625f, 0.25f, 0.625f);
		setBlockTextureName(FairyLights.MODID + ":fastener");
	}

	public BlockConnectionFastener(Material material) {
		super(material);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
		if (fastener == null) {
			return;
		}
		Iterator<Connection> connectionIterator = fastener.getConnections().iterator();
		float offsetX = world.rand.nextFloat() * 0.8f + 0.1f;
		float offsetY = world.rand.nextFloat() * 0.8f + 0.1f;
		float offsetZ = world.rand.nextFloat() * 0.8f + 0.1f;
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			if (!(connection instanceof ConnectionPlayer)) {
				ItemStack itemStack = new ItemStack(connection.getType().getItem(), 1);
				NBTTagCompound tagCompound = new NBTTagCompound();
				connection.writeDetailsToNBT(tagCompound);
				if (!tagCompound.hasNoTags()) {
					itemStack.setTagCompound(tagCompound);
				}
				EntityItem entityItem = new EntityItem(world, x + offsetX, y + offsetY, z + offsetZ, itemStack);
				float v = 0.05F;
				entityItem.motionX = world.rand.nextGaussian() * v;
				entityItem.motionY = world.rand.nextGaussian() * v + 0.2f;
				entityItem.motionZ = world.rand.nextGaussian() * v;
				world.spawnEntityInWorld(entityItem);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int data) {
		return new TileEntityConnectionFastener();
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public Item getItemDropped(int data, Random random, int fortune) {
		return null;
	}

	public Point3f getOffsetForData(int data, float offset) {
		Point3f offsetPoint = new Point3f(offset, offset, offset);
		switch (data) {
			case 2:
				offsetPoint.y += 0.75f;
			case 0:
				offsetPoint.x += 0.375f;
				offsetPoint.z += 0.375f;
				break;
			case 1:
				offsetPoint.x += 0.75f;
			case 3:
				offsetPoint.z += 0.375f;
				offsetPoint.y += 0.375f;
				break;
			case 12:
				offsetPoint.z += 0.75f;
			case 4:
				offsetPoint.x += 0.375f;
				offsetPoint.y += 0.375f;
		}
		return offsetPoint;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return FairyLights.proxy.getFairyLightsFastenerPickBlock(target, world, x, y, z, this);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fromBlockX, float fromBlockY, float fromBlockZ) {
		ItemStack heldItemStack = player.getHeldItem();
		if (heldItemStack != null && heldItemStack.getItem() instanceof ItemConnection) {
			ItemConnection item = (ItemConnection) heldItemStack.getItem();
			PlayerData data = PlayerData.getPlayerData(player);
			if (data.hasLastClicked()) {
				Point3i lastClicked = data.getLastClicked();
				TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
				TileEntity tileEntityTo = world.getTileEntity(x, y, z);
				if (tileEntity != tileEntityTo && tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
					if (!world.isRemote) {
						TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
						TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
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
					data.setLastClicked(x, y, z);
					TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
					tileEntity.connectWith(player, item.getConnectionType(), heldItemStack.getTagCompound());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		int data = world.getBlockMetadata(x, y, z);
		ForgeDirection direction = DATA_TO_DIRECTION[data];
		Block blockOn = world.getBlock(x - direction.offsetX, y - direction.offsetY, z - direction.offsetZ);
		if (!world.isSideSolid(x - direction.offsetX, y - direction.offsetY, z - direction.offsetZ, direction) && !(blockOn instanceof BlockSlab && direction.offsetY == 0) && !(blockOn instanceof BlockLeaves) && !(blockOn instanceof BlockStairs)) {
			dropBlockAsItem(world, x, y, z, data, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int data = world.getBlockMetadata(x, y, z);
		Point3f offset = getOffsetForData(data, 0);
		setBlockBounds(offset.x, offset.y, offset.z, offset.x + 0.25f, offset.y + 0.25f, offset.z + 0.25f);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int meta) {
		return true;
	}
}
