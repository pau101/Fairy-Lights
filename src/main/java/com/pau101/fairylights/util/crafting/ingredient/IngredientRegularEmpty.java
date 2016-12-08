package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

import net.minecraft.item.ItemStack;

public class IngredientRegularEmpty implements IngredientRegular {
	@Override
	public MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new MatchResultRegular(this, input, input == null, Collections.EMPTY_LIST);
	}

	@Override
	public List<ItemStack> getInputs() {
		return Collections.EMPTY_LIST;
	}
}
