package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import net.minecraft.item.ItemStack;

public abstract class IngredientAuxiliary extends Ingredient<IngredientAuxiliary, MatchResultAuxiliary> {
	public static final int MAX_LIMIT = Integer.MAX_VALUE;

	private final ImmutableList<BehaviorAuxiliary<?>> auxiliaryBehaviors;

	private final boolean isRequired;

	private final int limit;

	protected IngredientAuxiliary(boolean isRequired, int limit) {
		this(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(), isRequired, limit);
	}

	protected IngredientAuxiliary(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorAuxiliary<?>> auxiliaryBehaviors, boolean isRequired, int limit) {
		super(behaviors, tooltip);
		Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
		this.auxiliaryBehaviors = auxiliaryBehaviors;
		this.isRequired = isRequired;
		this.limit = limit;
	}

	public final boolean isRequired() {
		return isRequired;
	}

	public final int getLimit() {
		return limit;
	}

	public boolean process(Multimap<IngredientAuxiliary, MatchResultAuxiliary> map, ItemStack output) {
		Collection<MatchResultAuxiliary> results = map.get(this);
		if (results.isEmpty() && isRequired()) {
			return true;
		}
		for (BehaviorAuxiliary<?> behavior : auxiliaryBehaviors) {
			if (accumulate(behavior, results, output)) {
				return true;
			}
		}
		return false;
	}

	/*
	@Override
	public void addTooltip(List<String> tooltip) {
		if (!isRequired()) {
			tooltip.add(Utils.formatRecipeTooltip("recipe.ingredient.auxiliary.optional"));
		}
	}*/

	private static <A> boolean accumulate(BehaviorAuxiliary<A> behavior, Collection<MatchResultAuxiliary> results, ItemStack output) {
		A ax = behavior.accumulator();
		for (MatchResultAuxiliary result : results) {
			behavior.consume(ax, result.getInput());
		}
		return behavior.finish(ax, output);
	}
}
