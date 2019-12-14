package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class EmptyRegularIngredient implements RegularIngredient {
	@Override
	public GenericRecipe.MatchResultRegular matches(ItemStack input, ItemStack output) {
		return new GenericRecipe.MatchResultRegular(this, input, input.isEmpty(), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return ImmutableList.of();
	}
}
