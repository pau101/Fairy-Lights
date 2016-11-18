package com.pau101.fairylights.server.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.Utils;

public class ItemPennant extends Item {
	public ItemPennant() {
		setHasSubtypes(true);
		setCreativeTab(FairyLights.fairyLightsTab);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return Utils.formatColored(ItemLight.getLightColor(stack.getMetadata()), super.getItemStackDisplayName(stack));
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (int meta = 0; meta < ItemLight.COLOR_COUNT; meta++) {
			subItems.add(new ItemStack(item, 1, meta));
		}
	}
}
