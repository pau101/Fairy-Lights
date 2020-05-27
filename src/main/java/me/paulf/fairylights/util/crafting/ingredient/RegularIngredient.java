package me.paulf.fairylights.util.crafting.ingredient;

import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

public interface RegularIngredient extends Ingredient<RegularIngredient, GenericRecipe.MatchResultRegular> {
    default void matched(final ItemStack ingredient, final ItemStack output) {}
}
