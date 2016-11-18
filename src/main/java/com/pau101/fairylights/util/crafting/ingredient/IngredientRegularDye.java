package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.DyeOreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public class IngredientRegularDye implements IngredientRegular {
	@Override
	public MatchResultRegular matches(ItemStack input, ItemStack output) {
		MatchResultRegular result = new MatchResultRegular(this, input, DyeOreDictUtils.isDye(input));
		if (!result.doesMatch() && output != null) {
			absent(output);
		}
		return result;
	}

	@Override
	public List<ItemStack> getInputs() {
		return DyeOreDictUtils.getAllDyes();
	}
}
