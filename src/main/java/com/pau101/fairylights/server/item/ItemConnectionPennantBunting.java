package com.pau101.fairylights.server.item;

import java.util.List;

import com.pau101.fairylights.server.fastener.connection.ConnectionType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemConnectionPennantBunting extends ItemConnection {
	@Override
	public void addInformation(ItemStack stack, EntityPlayer holder, List<String> stringLines, boolean advanced) {
		if (!stack.hasTagCompound()) {
			return;
		}
		NBTTagCompound compound = stack.getTagCompound();
		if (compound.hasKey("text", NBT.TAG_COMPOUND)) {
			NBTTagCompound text = compound.getCompoundTag("text");
			String val = text.getString("value");
			if (val.length() > 0) {
				stringLines.add(I18n.translateToLocalFormatted("format.text", val));
			}
		}
		if (compound.hasKey("pattern", NBT.TAG_LIST)) {
			NBTTagList tagList = compound.getTagList("pattern", NBT.TAG_COMPOUND);
			int tagCount = tagList.tagCount();
			if (tagCount > 0) {
				stringLines.add(I18n.translateToLocal("item.pennantBunting.colors"));
			}
			for (int i = 0; i < tagCount; i++) {
				NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
				stringLines.add(I18n.translateToLocalFormatted("format.pattern.entry", I18n.translateToLocal("color." + EnumDyeColor.byDyeDamage(lightCompound.getByte("color")) + ".name")));
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.PENNANT_BUNTING;
	}
}
