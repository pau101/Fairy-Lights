package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import net.minecraft.item.ItemStack;

public abstract class IngredientRegular extends Ingredient<IngredientRegular, MatchResultRegular> {
	private final ImmutableList<BehaviorRegular> regularBehaviors;

	public IngredientRegular() {
		this(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of());
	}

	public IngredientRegular(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip) {
		this(behaviors, tooltip, ImmutableList.of());
	}

	public IngredientRegular(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorRegular> regularBehaviors) {
		super(behaviors, tooltip);
		this.regularBehaviors = regularBehaviors;
	}

	public final void matched(ItemStack ingredient, ItemStack output) {
		regularBehaviors.forEach(b -> b.matched(ingredient, output));
	}
}
