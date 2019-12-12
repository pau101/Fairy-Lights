package me.paulf.fairylights.util.crafting.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

public class IngredientRegularList implements IngredientRegular {
	private final List<IngredientRegular> ingredients;

	public IngredientRegularList(IngredientRegular... ingredients) {
		this(Arrays.asList(ingredients));
	}

	public IngredientRegularList(List<IngredientRegular> ingredients) {
		this.ingredients = Objects.requireNonNull(ingredients, "ingredients");
	}

	@Override
	public final GenericRecipe.MatchResultRegular matches(ItemStack input, ItemStack output) {
		GenericRecipe.MatchResultRegular matchResult = null;
		List<GenericRecipe.MatchResultRegular> supplementaryResults = new ArrayList<>(ingredients.size());
		for (IngredientRegular ingredient : ingredients) {
			GenericRecipe.MatchResultRegular result = ingredient.matches(input, output);
			if (result.doesMatch() && matchResult == null) {
				matchResult = result;
			} else {
				supplementaryResults.add(result);
			}
		}
		if (matchResult == null) {
			return new GenericRecipe.MatchResultRegular(this, input, false, supplementaryResults);
		}
		return matchResult.withParent(new GenericRecipe.MatchResultRegular(this, input, true, supplementaryResults));
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		for (IngredientRegular ingredient : ingredients) {
			inputs.addAll(ingredient.getInputs());
		}
		return inputs.build();
	}

	@Override
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (IngredientRegular ingredient : ingredients) {
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
	public String toString() {
		return ingredients.toString();
	}
}
