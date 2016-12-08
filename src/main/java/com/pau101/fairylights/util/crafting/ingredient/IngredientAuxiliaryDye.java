package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;

import com.pau101.fairylights.util.DyeOreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;

import net.minecraft.item.ItemStack;

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
		return new MatchResultAuxiliary(this, input, DyeOreDictUtils.isDye(input), Collections.EMPTY_LIST);
	}

	@Override
	public List<ItemStack> getInputs() {
		return DyeOreDictUtils.getAllDyes();
	}
}
