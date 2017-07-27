package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientRegularItem extends IngredientRegular {
	protected final ItemStack ingredient;

	public IngredientRegularItem(ItemStack stack) {
		this(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(), stack);
	}

	public IngredientRegularItem(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorRegular> regularBehaviors, ItemStack stack) {
		super(behaviors, tooltip, regularBehaviors);
		ingredient = Objects.requireNonNull(stack, "stack");
	}

	@Override
	public final MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new MatchResultRegular(this, input, OreDictionary.itemMatches(ingredient, input, false), Collections.EMPTY_LIST);
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return getMatchingSubtypes(ingredient);
	}

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
