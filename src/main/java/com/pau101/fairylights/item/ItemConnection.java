package com.pau101.fairylights.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;

public abstract class ItemConnection extends Item {
	public ItemConnection() {
		setCreativeTab(FairyLights.fairyLightsTab);
		setMaxStackSize(16);
	}

	public abstract ConnectionType getConnectionType();

	@Override
	public boolean onItemUse(ItemStack heldItemStack, EntityPlayer user, World world, BlockPos pos, EnumFacing facing, float fromBlockX, float fromBlockY, float fromBlockZ) {
		if (FairyLights.connectionFastener.canPlaceBlockOnSide(world, pos, facing)) {
			pos = pos.offset(facing);
			if (!user.canPlayerEdit(pos, facing, heldItemStack)) {
				return false;
			}
			if (FairyLights.connectionFastener.canPlaceBlockAt(world, pos)) {
				if (!world.isRemote) {
					IBlockState state =  FairyLights.connectionFastener.getDefaultState().withProperty(BlockConnectionFastener.FACING_PROP, facing);
					if (world.setBlockState(pos, state, 3)) {
						FairyLights.connectionFastener.onBlockPlacedBy(world, pos, state, user, heldItemStack);
						world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, FairyLights.connectionFastener.stepSound.getPlaceSound(), (FairyLights.connectionFastener.stepSound.getVolume() + 1) / 2, FairyLights.connectionFastener.stepSound.getFrequency() * 0.8F);
						PlayerData playerData = PlayerData.getPlayerData(user);
						if (playerData.hasLastClicked()) {
							BlockPos lastClicked = playerData.getLastClicked();
							TileEntity tileEntity = world.getTileEntity(lastClicked);
							TileEntity tileEntityTo = world.getTileEntity(pos);
							if (tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
								TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
								TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
								to.removeConnection(user);
								from.connectWith(to, getConnectionType(), heldItemStack.getTagCompound());
								playerData.setUnknownLastClicked();
								heldItemStack.stackSize--;
							}
						} else {
							playerData.setLastClicked(pos);
							TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(pos);
							tileEntity.connectWith(user, getConnectionType(), heldItemStack.getTagCompound());
						}
					}
				}
				return true;
			}
		} else if (FairyLights.fences.contains(world.getBlockState(pos).getBlock())) {
			if (!user.canPlayerEdit(pos, facing, heldItemStack)) {
				return false;
			}
			if (!world.isRemote) {
				world.setBlockState(pos, FairyLights.normalFenceToFastenerFenceMap.get(world.getBlockState(pos).getBlock()).getDefaultState());
				world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getY() + 0.5, FairyLights.connectionFastener.stepSound.getPlaceSound(), (FairyLights.connectionFastener.stepSound.getVolume() + 1) / 2, FairyLights.connectionFastener.stepSound.getFrequency() * 0.8F);
				PlayerData playerData = PlayerData.getPlayerData(user);
				if (playerData.hasLastClicked()) {
					BlockPos lastClicked = playerData.getLastClicked();
					TileEntity tileEntity = world.getTileEntity(lastClicked);
					TileEntity tileEntityTo = world.getTileEntity(pos);
					if (tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
						TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
						TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
						to.removeConnection(user);
						from.connectWith(to, getConnectionType(), heldItemStack.getTagCompound());
						playerData.setUnknownLastClicked();
						heldItemStack.stackSize--;
					}
				} else {
					playerData.setLastClicked(pos);
					TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(pos);
					tileEntity.connectWith(user, getConnectionType(), heldItemStack.getTagCompound());
				}
			}
			return true;
		}
		return false;
	}
}
