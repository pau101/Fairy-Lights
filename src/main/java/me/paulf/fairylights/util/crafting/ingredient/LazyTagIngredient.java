package me.paulf.fairylights.util.crafting.ingredient;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class LazyTagIngredient extends Ingredient {
    private final ITag<Item> tag;

    private LazyTagIngredient(final ITag<Item> tag) {
        super(Stream.empty());
        this.tag = tag;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return this.tag.getAllElements().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(@Nullable final ItemStack stack) {
        return stack != null && stack.getItem().isIn(this.tag);
    }

    @Override
    public IntList getValidItemStacksPacked() {
        final ItemStack[] stacks = this.getMatchingStacks();
        final IntList list = new IntArrayList(stacks.length);
        for (final ItemStack stack : this.getMatchingStacks()) {
            list.add(RecipeItemHelper.pack(stack));
        }
        list.sort(IntComparators.NATURAL_COMPARATOR);
        return list;
    }

    @Override
    public boolean hasNoMatchingItems() {
        return this.tag.getAllElements().isEmpty();
    }

    public static LazyTagIngredient of(final ITag<Item> tag) {
        return new LazyTagIngredient(tag);
    }
}
