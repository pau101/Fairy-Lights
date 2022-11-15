package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.Objects;

public abstract class BasicAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
    protected final Ingredient ingredient;

    protected final boolean isRequired;

    protected final int limit;

    public BasicAuxiliaryIngredient(final Ingredient ingredient, final boolean isRequired, final int limit) {
        Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
        this.ingredient = Objects.requireNonNull(ingredient, "ingredient");
        this.isRequired = isRequired;
        this.limit = limit;
    }

    @Override
    public final GenericRecipe.MatchResultAuxiliary matches(final ItemStack input) {
        return new GenericRecipe.MatchResultAuxiliary(this, input, this.ingredient.test(input), Collections.emptyList());
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        return this.getMatchingSubtypes(this.ingredient);
    }

    @Override
    public boolean isRequired() {
        return this.isRequired;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }
}
