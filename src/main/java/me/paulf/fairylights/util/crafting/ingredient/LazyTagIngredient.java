package me.paulf.fairylights.util.crafting.ingredient;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LazyTagIngredient extends Ingredient {
    private final TagKey<Item> tag;

    private LazyTagIngredient(final TagKey<Item> tag) {
        super(Stream.empty());
        this.tag = tag;
    }

    @Override
    public ItemStack[] getItems() {
        return StreamSupport.stream(Registry.ITEM.getTagOrEmpty(this.tag).spliterator(), false).map(ItemStack::new).toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(@Nullable final ItemStack stack) {
        return stack != null && stack.is(this.tag);
    }

    @Override
    public IntList getStackingIds() {
        final ItemStack[] stacks = this.getItems();
        final IntList list = new IntArrayList(stacks.length);
        for (final ItemStack stack : stacks) {
            list.add(StackedContents.getStackingIndex(stack));
        }
        list.sort(IntComparators.NATURAL_COMPARATOR);
        return list;
    }

    @Override
    public boolean isEmpty() {
        return !Registry.ITEM.getTagOrEmpty(this.tag).iterator().hasNext();
    }

    public static LazyTagIngredient of(final TagKey<Item> tag) {
        return new LazyTagIngredient(tag);
    }
}
