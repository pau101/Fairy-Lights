package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResult;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface Ingredient<I extends Ingredient, M extends MatchResult<I, M>> {
	List<ItemStack> getInputs();

	default boolean dictatesOutputType() {
		return false;
	}

	M matches(ItemStack input, ItemStack output);

	default void present(ItemStack output) {}

	default void absent(ItemStack output) {}

	default List<ItemStack> getMatchingSubtypes(ItemStack stack) {
		Objects.requireNonNull(stack, "stack");
		NonNullList<ItemStack> subtypes = NonNullList.func_191196_a();
		Item item = stack.getItem();
		for (CreativeTabs tab : item.getCreativeTabs()) {
			item.getSubItems(item, tab, subtypes);
		}
		Iterator<ItemStack> iter = subtypes.iterator();
		while (iter.hasNext()) {
			if (!matches(iter.next(), ItemStack.field_190927_a).doesMatch()) {
				iter.remove();
			}
		}
		return subtypes;
	}
}
