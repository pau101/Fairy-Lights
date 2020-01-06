package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public abstract class DyeAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
    private final boolean isRequired;

    private final int limit;

    public DyeAuxiliaryIngredient(final boolean isRequired, final int limit) {
        this.isRequired = isRequired;
        this.limit = limit;
    }

    @Override
    public boolean isRequired() {
        return this.isRequired;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public GenericRecipe.MatchResultAuxiliary matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultAuxiliary(this, input, OreDictUtils.isDye(input), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return OreDictUtils.getAllDyes();
    }
}
