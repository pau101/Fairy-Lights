package com.pau101.fairylights.util.crafting.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public class IngredientRegularList implements IngredientRegular {
	private final List<IngredientRegular> ingredients;

	public IngredientRegularList(IngredientRegular... ingredients) {
		this(Arrays.asList(ingredients));
	}

	public IngredientRegularList(List<IngredientRegular> ingredients) {
		this.ingredients = Objects.requireNonNull(ingredients, "ingredients");
	}

	@Override
	public final MatchResultRegular matches(ItemStack input, ItemStack output) {
		MatchResultRegular matchResult = null;
		for (IngredientRegular ingredient : ingredients) {
			MatchResultRegular result = ingredient.matches(input, output);
			if (result.doesMatch()) {
				if (matchResult == null) {
					matchResult = result;
				}
			}
		}
		if (matchResult == null) {
			if (output != null) {
				absent(output);
			}
			return new MatchResultRegular(this, input, false);
		}
		return matchResult.withParent(new MatchResultRegular(this, input, true));
	}

	@Override
	public List<ItemStack> getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		for (IngredientRegular ingredient : ingredients) {
			inputs.addAll(ingredient.getInputs());
		}
		return inputs;
	}

	@Override
	public String toString() {
		return ingredients.toString();
	}
}
