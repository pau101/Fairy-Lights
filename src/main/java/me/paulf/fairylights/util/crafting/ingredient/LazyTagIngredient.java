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
    public ItemStack[] func_193365_a() {
        return this.tag.func_230236_b_().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(@Nullable final ItemStack stack) {
        return stack != null && stack.func_77973_b().func_206844_a(this.tag);
    }

    @Override
    public IntList func_194139_b() {
        final ItemStack[] stacks = this.func_193365_a();
        final IntList list = new IntArrayList(stacks.length);
        for (final ItemStack stack : this.func_193365_a()) {
            list.add(RecipeItemHelper.func_194113_b(stack));
        }
        list.sort(IntComparators.NATURAL_COMPARATOR);
        return list;
    }

    @Override
    public boolean func_203189_d() {
        return this.tag.func_230236_b_().isEmpty();
    }

    public static LazyTagIngredient of(final ITag<Item> tag) {
        return new LazyTagIngredient(tag);
    }
}
