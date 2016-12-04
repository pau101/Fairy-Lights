package com.pau101.fairylights.util.crafting.ingredient;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

import net.minecraft.item.ItemStack;

public interface IngredientRegular extends Ingredient<IngredientRegular, MatchResultRegular> {
	default void matched(ItemStack ingredient, ItemStack output) {}
}
