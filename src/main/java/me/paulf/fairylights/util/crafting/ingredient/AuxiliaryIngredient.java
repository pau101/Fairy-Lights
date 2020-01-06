package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.Multimap;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface AuxiliaryIngredient<A> extends Ingredient<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> {
    boolean isRequired();

    int getLimit();

    @Nullable
    A accumulator();

    void consume(A accumulator, ItemStack ingredient);

    boolean finish(A accumulator, ItemStack output);

    default boolean process(final Multimap<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> map, final ItemStack output) {
        final Collection<GenericRecipe.MatchResultAuxiliary> results = map.get(this);
        if (results.isEmpty() && this.isRequired()) {
            return true;
        }
        final A ax = this.accumulator();
        for (final GenericRecipe.MatchResultAuxiliary result : results) {
            this.consume(ax, result.getInput());
        }
        return this.finish(ax, output);
    }

    @Override
    default void addTooltip(final List<String> tooltip) {
        if (!this.isRequired()) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.ingredient.auxiliary.optional"));
        }
    }
}
