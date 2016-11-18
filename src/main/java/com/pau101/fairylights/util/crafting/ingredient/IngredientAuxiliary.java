package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;

public interface IngredientAuxiliary<A> extends Ingredient<IngredientAuxiliary<?>, MatchResultAuxiliary> {
	boolean isRequired();

	int getLimit();

	@Nullable
	A accumulator();

	void consume(A accumulator, ItemStack ingredient);

	boolean finish(A accumulator, ItemStack output);

	default boolean process(Multimap<IngredientAuxiliary<?>, MatchResultAuxiliary> map, ItemStack output) {
		Collection<MatchResultAuxiliary> results = map.get(this);
		if (results.isEmpty() && isRequired()) {
			return true;
		}
		A ax = accumulator();
		for (MatchResultAuxiliary result : results) {
			consume(ax, result.getInput());
		}
		return finish(ax, output);
	}
}
