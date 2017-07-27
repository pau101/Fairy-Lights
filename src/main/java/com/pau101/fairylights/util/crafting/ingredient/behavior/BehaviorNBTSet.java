package com.pau101.fairylights.util.crafting.ingredient.behavior;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

public final class BehaviorNBTSet extends BehaviorNBT {
	public BehaviorNBTSet(ImmutableList<String> keys) {
		super(keys);
	}

	@Override
	public void present(ItemStack output) {
		setBoolean(output, true);
	}

	@Override
	public void absent(ItemStack output) {
		setBoolean(output, false);
	}
}
