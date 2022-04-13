package me.paulf.fairylights.util.crafting.ingredient;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public interface AuxiliaryIngredient<A> extends GenericIngredient<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> {
    boolean isRequired();

    int getLimit();

    @Nullable
    A accumulator();

    void consume(A accumulator, ItemStack ingredient);

    boolean finish(A accumulator, CompoundTag nbt);

    default boolean process(final Multimap<AuxiliaryIngredient<?>, GenericRecipe.MatchResultAuxiliary> map, final CompoundTag nbt) {
        final Collection<GenericRecipe.MatchResultAuxiliary> results = map.get(this);
        if (results.isEmpty() && this.isRequired()) {
            return true;
        }
        final A ax = this.accumulator();
        for (final GenericRecipe.MatchResultAuxiliary result : results) {
            this.consume(ax, result.getInput());
        }
        return this.finish(ax, nbt);
    }

    @Override
    default void addTooltip(final List<Component> tooltip) {
        if (!this.isRequired()) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.ingredient.auxiliary.optional"));
        }
    }
}
