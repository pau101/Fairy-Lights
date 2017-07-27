package com.pau101.fairylights.util.crafting.ingredient.behavior;

import net.minecraft.item.ItemStack;

public interface BehaviorAuxiliary<A> {
	A accumulator();

	void consume(A accumulator, ItemStack ingredient);

	boolean finish(A accumulator, ItemStack output);
}
