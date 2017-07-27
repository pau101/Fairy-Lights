package com.pau101.fairylights.util.crafting.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import net.minecraft.item.ItemStack;

public class IngredientAuxiliaryList extends IngredientAuxiliary {
	protected final List<IngredientAuxiliary> ingredients;

	public IngredientAuxiliaryList(boolean isRequired, int limit, IngredientAuxiliary... ingredients) {
		this(isRequired, limit, Arrays.asList(ingredients));
	}

	public IngredientAuxiliaryList(boolean isRequired, int limit, List<IngredientAuxiliary> ingredients) {
		this(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(), isRequired, limit, ingredients);
	}

	public IngredientAuxiliaryList(boolean isRequired, IngredientAuxiliary... ingredients) {
		this(isRequired, MAX_LIMIT, ingredients);
	}

	public IngredientAuxiliaryList(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorAuxiliary<?>> auxiliaryBehaviors, boolean isRequired, int limit, List<IngredientAuxiliary> ingredients) {
		super(behaviors, tooltip, auxiliaryBehaviors, isRequired, limit);
		Objects.requireNonNull(ingredients, "ingredients");
		Preconditions.checkArgument(ingredients.size() > 0, "ingredients must have at least one element");
		this.ingredients = ingredients;
	}

	@Override
	public final MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		MatchResultAuxiliary matchResult = null;
		List<MatchResultAuxiliary> supplementaryResults = new ArrayList<>(ingredients.size());
		for (IngredientAuxiliary ingredient : ingredients) {
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
	public final ImmutableList<ItemStack> getInputs() {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		for (IngredientAuxiliary ingredient : ingredients) {
			inputs.addAll(ingredient.getInputs());
		}
		return inputs.build();
	}

	@Override
	public final ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (IngredientAuxiliary ingredient : ingredients) {
			ImmutableList<ImmutableList<ItemStack>> subInputs = ingredient.getInput(output);
			for (int i = 0; i < subInputs.size(); i++) {
				List<ItemStack> stacks;
				if (i < inputs.size()) {
					stacks = inputs.get(i);
				} else {
					inputs.add(stacks = new ArrayList<>());
				}
				stacks.addAll(subInputs.get(i));
			}
		}
		ImmutableList.Builder<ImmutableList<ItemStack>> inputsImm = ImmutableList.builder();
		for (List<ItemStack> list : inputs) {
			inputsImm.add(ImmutableList.copyOf(list));
		}
		return inputsImm.build();
	}

	@Override
	public final boolean process(Multimap<IngredientAuxiliary, MatchResultAuxiliary> map, ItemStack output) {
		for (IngredientAuxiliary ingredient : ingredients) {
			if (ingredient.process(map, output)) {
				return true;
			}
		}
		return super.process(map, output);
	}
}
