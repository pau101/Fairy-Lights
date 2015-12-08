package com.pau101.fairylights.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockFairyLightsFastener;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.util.vectormath.Point3i;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFairyLights extends ItemBlock {
	@SideOnly(Side.CLIENT)
	public static IIcon[] icons;

	public ItemFairyLights(Block block) {
		super(block);
		setMaxStackSize(16);
		setTextureName(FairyLights.MODID + ":fairy_lights_fastener");
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer holder, List stringLines, boolean currentlyHeld) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if (tagCompound.getBoolean("twinkle")) {
				stringLines.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
			}
			if (tagCompound.getBoolean("tight")) {
				stringLines.add(StatCollector.translateToLocal("item.fairy_lights.tight"));
			}
			if (tagCompound.hasKey("pattern", 9)) {
				NBTTagList tagList = tagCompound.getTagList("pattern", 10);
				int tagCount = tagList.tagCount();
				if (tagCount > 0) {
					stringLines.add(StatCollector.translateToLocal("item.fairy_lights.pattern"));
				}
				for (int i = 0; i < tagCount; i++) {
					NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
					String localizedLightVariant = StatCollector.translateToLocal(FairyLights.light.getUnlocalizedName() + '.'
						+ LightVariant.getLightVariant(lightCompound.getInteger("light")).getName() + ".name");
					String localizedColor = StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.field_150923_a[lightCompound.getByte("color")]);
					stringLines.add("  " + StatCollector.translateToLocalFormatted("item.fairy_lights.colored_light", localizedColor, localizedLightVariant));
				}
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack heldItemStack, EntityPlayer user, World world, int x, int y, int z, int side, float fromBlockX, float fromBlockY,
		float fromBlockZ) {
		int data = BlockFairyLightsFastener.SIDE_TO_DATA[side];
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		Block blockPlacingOn = world.getBlock(x, y, z);
		if (world.isSideSolid(x, y, z, direction) || blockPlacingOn instanceof BlockSlab && direction.offsetY == 0 || blockPlacingOn instanceof BlockLeaves
			|| blockPlacingOn instanceof BlockStairs) {
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
			if (FairyLights.fairyLightsFastener.canPlaceBlockAt(world, x, y, z)) {
				if (!world.isRemote) {
					world.setBlock(x, y, z, FairyLights.fairyLightsFastener, data, 3);
					world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, FairyLights.fairyLightsFastener.stepSound.func_150496_b(),
						(FairyLights.fairyLightsFastener.stepSound.getVolume() + 1) / 2, FairyLights.fairyLightsFastener.stepSound.getPitch() * 0.8f);
					PlayerData playerData = PlayerData.getPlayerData(user);
					if (playerData.hasLastClicked()) {
						Point3i lastClicked = playerData.getLastClicked();
						TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
						TileEntity tileEntityTo = world.getTileEntity(x, y, z);
						if (tileEntity instanceof TileEntityFairyLightsFastener && tileEntityTo instanceof TileEntityFairyLightsFastener) {
							TileEntityFairyLightsFastener to = (TileEntityFairyLightsFastener) tileEntity;
							TileEntityFairyLightsFastener from = (TileEntityFairyLightsFastener) tileEntityTo;
							to.removeConnection(user);
							from.connectWith(to, heldItemStack.getTagCompound());
							playerData.setUnknownLastClicked();
							heldItemStack.stackSize--;
							return true;
						}
					} else {
						playerData.setLastClicked(x, y, z);
						TileEntityFairyLightsFastener tileEntity = (TileEntityFairyLightsFastener) world.getTileEntity(x, y, z);
						tileEntity.connectWith(user, heldItemStack.getTagCompound());
					}
				}
				return true;
			}
		} else if (world.getBlock(x, y, z) == Blocks.fence) {
			if (!world.isRemote) {
				world.setBlock(x, y, z, FairyLights.fairyLightsFence);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, FairyLights.fairyLightsFastener.stepSound.func_150496_b(),
					(FairyLights.fairyLightsFastener.stepSound.getVolume() + 1) / 2, FairyLights.fairyLightsFastener.stepSound.getPitch() * 0.8f);
				PlayerData playerData = PlayerData.getPlayerData(user);
				if (playerData.hasLastClicked()) {
					Point3i lastClicked = playerData.getLastClicked();
					TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
					TileEntity tileEntityTo = world.getTileEntity(x, y, z);
					if (tileEntity instanceof TileEntityFairyLightsFastener && tileEntityTo instanceof TileEntityFairyLightsFastener) {
						TileEntityFairyLightsFastener to = (TileEntityFairyLightsFastener) tileEntity;
						TileEntityFairyLightsFastener from = (TileEntityFairyLightsFastener) tileEntityTo;
						to.removeConnection(user);
						from.connectWith(to, heldItemStack.getTagCompound());
						playerData.setUnknownLastClicked();
						heldItemStack.stackSize--;
						return true;
					}
				} else {
					playerData.setLastClicked(x, y, z);
					TileEntityFairyLightsFastener tileEntity = (TileEntityFairyLightsFastener) world.getTileEntity(x, y, z);
					tileEntity.connectWith(user, heldItemStack.getTagCompound());
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 0) {
			return super.getColorFromItemStack(stack, renderPass);
		}
		if (stack.hasTagCompound()) {
			NBTTagList tagList = stack.getTagCompound().getTagList("pattern", 10);
			if (tagList.tagCount() > 0) {
				return ItemDye.field_150922_c[tagList.getCompoundTagAt((renderPass - 1) % tagList.tagCount()).getByte("color")];
			}
		}
		return 0xFFD584;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 5;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons[pass];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		icons = new IIcon[5];
		for (int i = 0; i < icons.length; i++) {
			icons[i] = iconRegister.registerIcon(FairyLights.MODID + ":fairy_lights_" + i);
		}
	}
}
