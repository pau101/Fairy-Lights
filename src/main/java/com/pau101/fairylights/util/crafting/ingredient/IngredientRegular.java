package com.pau101.fairylights.util.crafting.ingredient;

import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public interface IngredientRegular extends Ingredient<IngredientRegular, MatchResultRegular> {
	default void matched(ItemStack ingredient, ItemStack output) {}
}
