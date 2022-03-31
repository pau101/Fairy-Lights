package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class EmptyRegularIngredient implements RegularIngredient {
    @Override
    public GenericRecipe.MatchResultRegular matches(final ItemStack input) {
        return new GenericRecipe.MatchResultRegular(this, input, input.func_190926_b(), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return ImmutableList.of();
    }
}
