package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.Collections;
import java.util.Objects;

public abstract class IngredientAuxiliaryOre<A> implements IngredientAuxiliary<A> {
	protected final Tag<Item> tag;

	protected final boolean isRequired;

	protected final int limit;

	public IngredientAuxiliaryOre(Tag<Item> tag, boolean isRequired, int limit) {
		Preconditions.checkArgument(limit > 0, "limit must be greater than zero");
		this.tag = Objects.requireNonNull(tag, "tag");
		this.isRequired = isRequired;
		this.limit = limit;
	}

	@Override
	public final GenericRecipe.MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		return new GenericRecipe.MatchResultAuxiliary(this, input, input.getItem().isIn(tag), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return tag.getAllElements().stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	@Override
	public int getLimit() {
		return limit;
	}
}
