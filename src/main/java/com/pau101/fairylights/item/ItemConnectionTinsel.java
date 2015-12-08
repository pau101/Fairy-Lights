package com.pau101.fairylights.item;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.util.mc.EnumDyeColor;

public class ItemConnectionTinsel extends ItemConnection {
	public ItemConnectionTinsel() {
		setTextureName(FairyLights.MODID + ":tinsel");
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.TINSEL;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		String localizedTinselName = StatCollector.translateToLocal(super.getUnlocalizedName(itemStack) + ".name");
		if (itemStack.hasTagCompound()) {
			NBTTagCompound compound = itemStack.getTagCompound();
			String localizedColor = StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(compound.getByte("color")).getUnlocalizedName());
			return StatCollector.translateToLocalFormatted("item.fairy_lights.colored_light", localizedColor, localizedTinselName);
		}
		return localizedTinselName;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (stack.hasTagCompound()) {
			return EnumDyeColor.byDyeDamage(stack.getTagCompound().getByte("color")).getMapColor().colorValue;
		}
		return MapColor.yellowColor.colorValue;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List subItems) {
		for (int color = 0, max = EnumDyeColor.values().length; color < max; color++) {
			ItemStack subItem = new ItemStack(item, 1);
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("color", color);
			subItem.setTagCompound(compound);
			subItems.add(subItem);
		}
	}
}
