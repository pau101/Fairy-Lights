package com.pau101.fairylights.util.crafting.ingredient.behavior;

import net.minecraft.item.ItemStack;

public interface BehaviorRegular {
	default void matched(ItemStack ingredient, ItemStack output) {}
}
