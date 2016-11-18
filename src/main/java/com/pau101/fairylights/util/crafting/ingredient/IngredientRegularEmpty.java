package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public class IngredientRegularEmpty implements IngredientRegular {
	@Override
	public MatchResultRegular matches(ItemStack input, ItemStack output) {
		MatchResultRegular result = new MatchResultRegular(this, input, input == null);
		if (!result.doesMatch() && output != null) {
			absent(output);
		}
		return result;
	}

	@Override
	public List<ItemStack> getInputs() {
		return Collections.EMPTY_LIST;
	}
}
