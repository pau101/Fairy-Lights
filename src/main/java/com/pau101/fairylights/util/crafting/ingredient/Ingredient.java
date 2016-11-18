package com.pau101.fairylights.util.crafting.ingredient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResult;

public interface Ingredient<I extends Ingredient, M extends MatchResult<I, M>> {
	List<ItemStack> getInputs();

	default boolean dictatesOutputType() {
		return false;
	}

	M matches(ItemStack input, @Nullable ItemStack output);

	default void present(ItemStack output) {}

	default void absent(ItemStack output) {}

	default List<ItemStack> getMatchingSubtypes(ItemStack stack) {
		Objects.requireNonNull(stack, "stack");
		List<ItemStack> subtypes = new ArrayList<>();
		Item item = stack.getItem();
		for (CreativeTabs tab : item.getCreativeTabs()) {
			item.getSubItems(item, tab, subtypes);
		}
		Iterator<ItemStack> iter = subtypes.iterator();
		while (iter.hasNext()) {
			if (!matches(iter.next(), null).doesMatch()) {
				iter.remove();
			}
		}
		return subtypes;
	}
}
