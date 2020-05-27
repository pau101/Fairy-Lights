package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public interface GenericIngredient<I extends GenericIngredient<? extends I, M>, M extends GenericRecipe.MatchResult<I, M>> {
    /**
     * Provides an immutable list of stacks that will match this ingredient.
     *
     * @return Immutable list of potential inputs for this ingredient
     */
    ImmutableList<ItemStack> getInputs();

    /**
     * Provides an immutable list of stacks which are required to craft the given output stack.
     * <p>
     * Only auxiliary ingredients should provide multiple.
     * <p>
     * Must be overriden by implementors which modify the output stack to provide accurate recipes for JEI.
     *
     * @return Immutable copy of stacks required to produce output
     */
    default ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
        return ImmutableList.of(this.getInputs());
    }

    M matches(ItemStack input, ItemStack output);

    default boolean dictatesOutputType() {
        return false;
    }

    default void present(final ItemStack output) {}

    default void absent(final ItemStack output) {}

    default ImmutableList<ItemStack> getMatchingSubtypes(final ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        final NonNullList<ItemStack> subtypes = NonNullList.create();
        final Item item = stack.getItem();
        for (final ItemGroup tab : item.getCreativeTabs()) {
            item.fillItemGroup(tab, subtypes);
        }
        final Iterator<ItemStack> iter = subtypes.iterator();
        while (iter.hasNext()) {
            if (!this.matches(iter.next(), ItemStack.EMPTY).doesMatch()) {
                iter.remove();
            }
        }
        return ImmutableList.copyOf(subtypes);
    }

    default void addTooltip(final List<String> tooltip) {}
}
