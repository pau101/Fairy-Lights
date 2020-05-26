package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.base.*;
import com.google.common.collect.*;
import me.paulf.fairylights.util.crafting.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

import java.util.Objects;
import java.util.*;

public abstract class BasicAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
    protected final ItemStack ingredient;

    protected final boolean isRequired;

    protected final int limit;

    public BasicAuxiliaryIngredient(final Item item, final boolean isRequired, final int limit) {
        this(new ItemStack(Objects.requireNonNull(item, "item")), isRequired, limit);
    }

    public BasicAuxiliaryIngredient(final Block block, final boolean isRequired, final int limit) {
        this(new ItemStack(Objects.requireNonNull(block, "block")), isRequired, limit);
    }

    public BasicAuxiliaryIngredient(final ItemStack stack, final boolean isRequired, final int limit) {
        Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
        this.ingredient = Objects.requireNonNull(stack, "stack");
        this.isRequired = isRequired;
        this.limit = limit;
    }

    @Override
    public final GenericRecipe.MatchResultAuxiliary matches(final ItemStack input, final ItemStack output) {
        return new GenericRecipe.MatchResultAuxiliary(this, input, this.ingredient.getItem() == input.getItem(), Collections.emptyList());
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
