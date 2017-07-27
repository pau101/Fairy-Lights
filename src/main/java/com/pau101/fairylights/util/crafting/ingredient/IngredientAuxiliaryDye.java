package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.OreDictUtils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import net.minecraft.item.ItemStack;

public class IngredientAuxiliaryDye extends IngredientAuxiliary {
	public IngredientAuxiliaryDye(boolean isRequired, int limit) {
		super(isRequired, limit);
	}

	public IngredientAuxiliaryDye(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorAuxiliary<?>> auxiliaryBehaviors, boolean isRequired, int limit) {
		super(behaviors, tooltip, auxiliaryBehaviors, isRequired, limit);
	}

	@Override
	public final MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		return new MatchResultAuxiliary(this, input, OreDictUtils.isDye(input), Collections.emptyList());
	}

	@Override
	public final ImmutableList<ItemStack> getInputs() {
		return OreDictUtils.getAllDyes();
	}
}
