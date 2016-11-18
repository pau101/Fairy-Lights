package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.DyeOreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;

public abstract class IngredientAuxiliaryDye<A> implements IngredientAuxiliary<A> {
	private final boolean isRequired;

	private final int limit;

	public IngredientAuxiliaryDye(boolean isRequired, int limit) {
		this.isRequired = isRequired;
		this.limit = limit;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		MatchResultAuxiliary result = new MatchResultAuxiliary(this, input, DyeOreDictUtils.isDye(input));
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
