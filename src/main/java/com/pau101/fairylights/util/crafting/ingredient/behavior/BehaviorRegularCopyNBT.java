package com.pau101.fairylights.util.crafting.ingredient.behavior;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class BehaviorRegularCopyNBT implements BehaviorRegular {
	@Override
	public void matched(ItemStack ingredient, ItemStack output) {
		NBTTagCompound compound = ingredient.getTagCompound();
		if (compound != null) {
			output.setTagCompound(compound.copy());
		}
	}
}
