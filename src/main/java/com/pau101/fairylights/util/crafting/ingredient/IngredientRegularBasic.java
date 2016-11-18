package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultRegular;

public class IngredientRegularBasic implements IngredientRegular {
	protected final ItemStack ingredient;

	public IngredientRegularBasic(Item item) {
		this(new ItemStack(Objects.requireNonNull(item, "item")));
	}

	public IngredientRegularBasic(Item item, int metadata) {
		this(new ItemStack(Objects.requireNonNull(item, "item"), 1, metadata));
	}

	public IngredientRegularBasic(Block block) {
		this(new ItemStack(Objects.requireNonNull(block, "block"), 1, OreDictionary.WILDCARD_VALUE));
	}

	public IngredientRegularBasic(ItemStack stack) {
		this.ingredient = Objects.requireNonNull(stack, "stack");
		Objects.requireNonNull(stack.getItem(), "item");
	}

	@Override
	public final MatchResultRegular matches(ItemStack input, ItemStack output) {
		MatchResultRegular result = new MatchResultRegular(this, input, OreDictionary.itemMatches(ingredient, input, false));
		if (!result.doesMatch() && output != null) {
			absent(output);
		}
		return result;
	}

	@Override
	public List<ItemStack> getInputs() {
		return getMatchingSubtypes(ingredient);
	}

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
