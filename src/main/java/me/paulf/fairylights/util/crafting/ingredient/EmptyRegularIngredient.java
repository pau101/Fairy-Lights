package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.*;
import me.paulf.fairylights.util.crafting.*;
import net.minecraft.item.*;

import java.util.*;

public class EmptyRegularIngredient implements RegularIngredient {
    @Override
    public GenericRecipe.MatchResultRegular matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultRegular(this, input, input.isEmpty(), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return ImmutableList.of();
    }
}
