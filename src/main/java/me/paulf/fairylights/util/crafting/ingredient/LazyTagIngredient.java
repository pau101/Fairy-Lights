package me.paulf.fairylights.util.crafting.ingredient;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class LazyTagIngredient extends Ingredient {
    private final Tag<Item> tag;

    private LazyTagIngredient(final Tag<Item> tag) {
        super(Stream.empty());
        this.tag = tag;
    }

    @Override
    public ItemStack[] getItems() {
        return this.tag.getValues().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(@Nullable final ItemStack stack) {
        return stack != null && this.tag.contains(stack.getItem());
    }

    @Override
    public IntList getStackingIds() {
        final ItemStack[] stacks = this.getItems();
        final IntList list = new IntArrayList(stacks.length);
        for (final ItemStack stack : this.getItems()) {
            list.add(StackedContents.getStackingIndex(stack));
        }
        list.sort(IntComparators.NATURAL_COMPARATOR);
        return list;
    }

    @Override
    public boolean isEmpty() {
        return this.tag.getValues().isEmpty();
    }

    public static LazyTagIngredient of(final Tag<Item> tag) {
        return new LazyTagIngredient(tag);
    }
}
