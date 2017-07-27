package com.pau101.fairylights.util.crafting.ingredient.behavior;

import net.minecraft.item.ItemStack;

public interface Behavior {
	default void present(ItemStack output) {}

	default void absent(ItemStack output) {}
}
