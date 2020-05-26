package me.paulf.fairylights.util.crafting.ingredient;

import me.paulf.fairylights.util.crafting.*;
import net.minecraft.item.*;

public interface RegularIngredient extends Ingredient<RegularIngredient, GenericRecipe.MatchResultRegular> {
    default void matched(final ItemStack ingredient, final ItemStack output) {}
}
