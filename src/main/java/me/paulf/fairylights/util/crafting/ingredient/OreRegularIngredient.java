package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.Collections;
import java.util.Objects;

public class OreRegularIngredient implements RegularIngredient {
	private final Tag<Item> tag;

	public OreRegularIngredient(Tag<Item> tag) {
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
