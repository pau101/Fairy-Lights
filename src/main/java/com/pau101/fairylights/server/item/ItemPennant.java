package com.pau101.fairylights.server.item;

import com.pau101.fairylights.util.Utils;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ItemPennant extends Item {
	public ItemPennant(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return Utils.formatColored(ItemLight.getLightColor(stack), super.getDisplayName(stack));
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (isInGroup(tab)) {
			for (DyeColor dye : DyeColor.values()) {
				ItemStack stack = new ItemStack(this);
				stack.getOrCreateTag().putByte("color", (byte) dye.getId());
				subItems.add(stack);
			}
		}
	}
}
