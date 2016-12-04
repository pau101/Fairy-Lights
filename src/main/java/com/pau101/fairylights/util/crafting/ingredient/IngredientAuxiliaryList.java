package com.pau101.fairylights.util.crafting.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public abstract class IngredientAuxiliaryList<A> implements IngredientAuxiliary<A> {
	private final List<? extends IngredientAuxiliary<?>> ingredients;

	private final boolean isRequired;

	private final int limit;

	public IngredientAuxiliaryList(boolean isRequired, int limit, IngredientAuxiliary<?>... ingredients) {
		this(Arrays.asList(ingredients), isRequired, limit);
	}

	public IngredientAuxiliaryList(boolean isRequired, IngredientAuxiliary<?>... ingredients) {
		this(Arrays.asList(ingredients), isRequired, Integer.MAX_VALUE);
	}

	public IngredientAuxiliaryList(List<? extends IngredientAuxiliary<?>> ingredients, boolean isRequired, int limit) {
		Objects.requireNonNull(ingredients, "ingredients");
		Preconditions.checkArgument(ingredients.size() > 0, "ingredients must have at least one element");
		Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
		this.ingredients = ingredients;
		this.isRequired = isRequired;
		this.limit = limit;
	}

	@Override
	public final MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		MatchResultAuxiliary matchResult = null;
		List<MatchResultAuxiliary> supplementaryResults = new ArrayList<>(ingredients.size());
		for (IngredientAuxiliary<?> ingredient : ingredients) {
			MatchResultAuxiliary result = ingredient.matches(input, output);
			if (result.doesMatch() && matchResult == null) {
				matchResult = result;
			} else {
				supplementaryResults.add(result);
			}
		}
		if (matchResult == null) {
			return new MatchResultAuxiliary(this, input, false, supplementaryResults);
		}
		return matchResult.withParent(new MatchResultAuxiliary(this, input, true, supplementaryResults));
	}

	@Override
	public List<ItemStack> getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		for (IngredientAuxiliary ingredient : ingredients) {
			inputs.addAll(ingredient.getInputs());
		}
		return inputs;
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
	public boolean process(Multimap<IngredientAuxiliary<?>, MatchResultAuxiliary> map, ItemStack output) {
		for (IngredientAuxiliary<?> ingredient : ingredients) {
			if (ingredient.process(map, output)) {
				return true;
			}
		}
		return IngredientAuxiliary.super.process(map, output);
	}

	@Override
	public String toString() {
		return ingredients.toString();
	}
}
