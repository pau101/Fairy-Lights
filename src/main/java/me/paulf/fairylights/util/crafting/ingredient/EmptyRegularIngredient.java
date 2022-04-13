package me.paulf.fairylights.util.crafting.ingredient;

import java.util.Collections;

import com.google.common.collect.ImmutableList;

import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.world.item.ItemStack;

public class EmptyRegularIngredient implements RegularIngredient {
    @Override
    public GenericRecipe.MatchResultRegular matches(final ItemStack input) {
        return new GenericRecipe.MatchResultRegular(this, input, input.isEmpty(), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return ImmutableList.of();
    }
}
