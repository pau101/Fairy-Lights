package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorRegular;
import net.minecraft.item.ItemStack;

public class IngredientRegularDye extends IngredientRegular {
	public IngredientRegularDye() {}

	public IngredientRegularDye(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorRegular> regularBehaviors) {
		super(behaviors, tooltip, regularBehaviors);
	}

	@Override
	public final MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new MatchResultRegular(this, input, OreDictUtils.isDye(input), Collections.emptyList());
	}

	@Override
	public final ImmutableList<ItemStack> getInputs() {
		return OreDictUtils.getAllDyes();
	}
}
