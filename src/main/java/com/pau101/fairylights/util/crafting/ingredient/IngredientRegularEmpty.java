package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class IngredientRegularEmpty implements IngredientRegular {
	@Override
	public MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new MatchResultRegular(this, input, input.isEmpty(), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return ImmutableList.of();
	}
}
