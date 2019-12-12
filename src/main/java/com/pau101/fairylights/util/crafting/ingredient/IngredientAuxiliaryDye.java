package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import net.minecraft.item.ItemStack;

import java.util.Collections;

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
		return new MatchResultAuxiliary(this, input, OreDictUtils.isDye(input), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return OreDictUtils.getAllDyes();
	}
}
