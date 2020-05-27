package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class DyeRegularIngredient implements RegularIngredient {
    @Override
    public GenericRecipe.MatchResultRegular matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultRegular(this, input, OreDictUtils.isDye(input), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return OreDictUtils.getAllDyes();
    }
}
