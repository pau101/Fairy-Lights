package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.base.*;
import com.google.common.collect.*;
import me.paulf.fairylights.util.crafting.*;
import net.minecraft.item.*;

import java.util.Objects;
import java.util.*;

public abstract class ListAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
    protected final List<? extends AuxiliaryIngredient<?>> ingredients;

    protected final boolean isRequired;

    protected final int limit;

    public ListAuxiliaryIngredient(final boolean isRequired, final int limit, final AuxiliaryIngredient<?>... ingredients) {
        this(Arrays.asList(ingredients), isRequired, limit);
    }

    public ListAuxiliaryIngredient(final boolean isRequired, final AuxiliaryIngredient<?>... ingredients) {
        this(Arrays.asList(ingredients), isRequired, Integer.MAX_VALUE);
    }

    public ListAuxiliaryIngredient(final List<? extends AuxiliaryIngredient<?>> ingredients, final boolean isRequired, final int limit) {
        Objects.requireNonNull(ingredients, "ingredients");
        Preconditions.checkArgument(ingredients.size() > 0, "ingredients must have at least one element");
        Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
        this.ingredients = ingredients;
        this.isRequired = isRequired;
        this.limit = limit;
    }

    @Override
    public final GenericRecipe.MatchResultAuxiliary matches(final ItemStack input, final ItemStack output) {
        GenericRecipe.MatchResultAuxiliary matchResult = null;
        final List<GenericRecipe.MatchResultAuxiliary> supplementaryResults = new ArrayList<>(this.ingredients.size());
        for (final AuxiliaryIngredient<?> ingredient : this.ingredients) {
            final GenericRecipe.MatchResultAuxiliary result = ingredient.matches(input, output);
            if (result.doesMatch() && matchResult == null) {
                matchResult = result;
            } else {
                supplementaryResults.add(result);
            }
        }
        if (matchResult == null) {
            return new GenericRecipe.MatchResultAuxiliary(this, input, false, supplementaryResults);
        }
        return matchResult.withParent(new GenericRecipe.MatchResultAuxiliary(this, input, true, supplementaryResults));
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        final ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
        for (final AuxiliaryIngredient<?> ingredient : this.ingredients) {
            inputs.addAll(ingredient.getInputs());
        }
        return inputs.build();
    }

    @Override
    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
        final List<List<ItemStack>> inputs = new ArrayList<>();
        for (final AuxiliaryIngredient<?> ingredient : this.ingredients) {
            final ImmutableList<ImmutableList<ItemStack>> subInputs = ingredient.getInput(output);
            for (int i = 0; i < subInputs.size(); i++) {
                final List<ItemStack> stacks;
                if (i < inputs.size()) {
                    stacks = inputs.get(i);
                } else {
                    inputs.add(stacks = new ArrayList<>());
                }
                stacks.addAll(subInputs.get(i));
            }
        }
        final ImmutableList.Builder<ImmutableList<ItemStack>> inputsImm = ImmutableList.builder();
        for (final List<ItemStack> list : inputs) {
            inputsImm.add(ImmutableList.copyOf(list));
        }
        return inputsImm.build();
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
    public boolean process(final Multimap<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> map, final ItemStack output) {
        for (final AuxiliaryIngredient<?> ingredient : this.ingredients) {
            if (ingredient.process(map, output)) {
                return true;
            }
        }
        return AuxiliaryIngredient.super.process(map, output);
    }

    @Override
    public String toString() {
        return this.ingredients.toString();
    }
}
