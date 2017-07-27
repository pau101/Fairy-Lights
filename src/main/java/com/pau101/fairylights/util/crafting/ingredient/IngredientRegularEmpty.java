package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public final class IngredientRegularEmpty extends IngredientRegular {
	@Override
	public MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new MatchResultRegular(this, input, input.isEmpty(), Collections.EMPTY_LIST);
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return ImmutableList.of();
	}
}
