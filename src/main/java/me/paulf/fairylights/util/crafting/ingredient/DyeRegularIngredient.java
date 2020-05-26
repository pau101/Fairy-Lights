package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.*;
import me.paulf.fairylights.util.*;
import me.paulf.fairylights.util.crafting.*;
import net.minecraft.item.*;

import java.util.*;

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
