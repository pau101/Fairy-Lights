package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.Collections;
import java.util.Objects;

public class IngredientRegularOre implements IngredientRegular {
	private final Tag<Item> tag;

	public IngredientRegularOre(Tag<Item> tag) {
		this.tag = Objects.requireNonNull(tag, "tag");
	}

	@Override
	public GenericRecipe.MatchResultRegular matches(final ItemStack input, final ItemStack output) {
		return new GenericRecipe.MatchResultRegular(this, input, input.getItem().isIn(tag), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return tag.getAllElements().stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
	}
}
