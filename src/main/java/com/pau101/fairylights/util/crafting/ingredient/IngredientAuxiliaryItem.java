package com.pau101.fairylights.util.crafting.ingredient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResultAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import com.pau101.fairylights.util.crafting.ingredient.behavior.BehaviorAuxiliary;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientAuxiliaryItem extends IngredientAuxiliary {
	protected final ItemStack ingredient;

	public IngredientAuxiliaryItem(ItemStack stack) {
		this(stack, true, Integer.MAX_VALUE);
	}

	public IngredientAuxiliaryItem(ItemStack stack, boolean isRequired, int limit) {
		this(ImmutableList.of(), EMPTY_TOOLTIP, ImmutableList.of(), isRequired, limit, stack);
	}

	public IngredientAuxiliaryItem(Item item, boolean isRequired, int limit) {
		this(new ItemStack(Objects.requireNonNull(item, "item")), isRequired, limit);
	}

	public IngredientAuxiliaryItem(Item item, int metadata, boolean isRequired, int limit) {
		this(new ItemStack(Objects.requireNonNull(item, "item"), 1, metadata), isRequired, limit);
	}

	public IngredientAuxiliaryItem(Block block, boolean isRequired, int limit) {
		this(new ItemStack(Objects.requireNonNull(block, "block"), 1, OreDictionary.WILDCARD_VALUE), isRequired, limit);
	}

	public IngredientAuxiliaryItem(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip, ImmutableList<BehaviorAuxiliary<?>> auxiliaryBehaviors, boolean isRequired, int limit, ItemStack ingredient) {
		super(behaviors, tooltip, auxiliaryBehaviors, isRequired, limit);
		this.ingredient = Objects.requireNonNull(ingredient, "stack");
	}

	@Override
	public final MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		return new MatchResultAuxiliary(this, input, OreDictionary.itemMatches(ingredient, input, false), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return getMatchingSubtypes(ingredient);
	}
}
