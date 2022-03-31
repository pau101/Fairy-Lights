package me.paulf.fairylights.util.crafting.ingredient;

import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface RegularIngredient extends GenericIngredient<RegularIngredient, GenericRecipe.MatchResultRegular> {
    default void matched(final ItemStack ingredient, final CompoundNBT nbt) {}
}
