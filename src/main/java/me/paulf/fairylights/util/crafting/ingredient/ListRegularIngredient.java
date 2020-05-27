package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListRegularIngredient implements RegularIngredient {
    private final List<RegularIngredient> ingredients;

    public ListRegularIngredient(final RegularIngredient... ingredients) {
        this(Arrays.asList(ingredients));
    }

    public ListRegularIngredient(final List<RegularIngredient> ingredients) {
        this.ingredients = Objects.requireNonNull(ingredients, "ingredients");
    }

    @Override
    public final GenericRecipe.MatchResultRegular matches(final ItemStack input, final ItemStack output) {
        GenericRecipe.MatchResultRegular matchResult = null;
        final List<GenericRecipe.MatchResultRegular> supplementaryResults = new ArrayList<>(this.ingredients.size());
        for (final RegularIngredient ingredient : this.ingredients) {
            final GenericRecipe.MatchResultRegular result = ingredient.matches(input, output);
            if (result.doesMatch() && matchResult == null) {
                matchResult = result;
            } else {
                supplementaryResults.add(result);
            }
        }
        if (matchResult == null) {
            return new GenericRecipe.MatchResultRegular(this, input, false, supplementaryResults);
        }
        return matchResult.withParent(new GenericRecipe.MatchResultRegular(this, input, true, supplementaryResults));
    }

    @Override
    public ImmutableList<ItemStack> getInputs() {
        final ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
        for (final RegularIngredient ingredient : this.ingredients) {
            inputs.addAll(ingredient.getInputs());
        }
        return inputs.build();
    }

    @Override
    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
        final List<List<ItemStack>> inputs = new ArrayList<>();
        for (final RegularIngredient ingredient : this.ingredients) {
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
    public String toString() {
        return this.ingredients.toString();
    }
}
