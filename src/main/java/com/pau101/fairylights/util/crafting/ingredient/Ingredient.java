package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.Utils;
import com.pau101.fairylights.util.crafting.GenericRecipe.MatchResult;
import com.pau101.fairylights.util.crafting.ingredient.behavior.Behavior;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class Ingredient<I extends Ingredient<I, M>, M extends MatchResult<I, M>> {
	public static final Consumer<List<String>> EMPTY_TOOLTIP = list -> {};

	private final ImmutableList<Behavior> behaviors;

	private final Consumer<List<String>> tooltip;

	public Ingredient() {
		this(ImmutableList.of(), EMPTY_TOOLTIP);
	}

	public Ingredient(Behavior behavior) {
		this(ImmutableList.of(behavior), EMPTY_TOOLTIP);
	}

	public Ingredient(ImmutableList<Behavior> behaviors, Consumer<List<String>> tooltip) {
		this.behaviors = Objects.requireNonNull(behaviors, "behaviors");
		this.tooltip = Objects.requireNonNull(tooltip, "tooltip");
	}

	/**
	 * Provides an immutable list of stacks that will match this ingredient.
	 *
	 * @return Immutable list of potential inputs for this ingredient
	 */
	public abstract ImmutableList<ItemStack> getInputs();

	/**
	 * Provides an immutable list of stacks which are required to craft the given output stack.
	 *
	 * Only auxiliary ingredients should provide multiple.
	 *
	 * Must be overridden by implementors which modify the output stack to provide accurate recipes for JEI.
	 *
	 * @return Immutable copy of stacks required to produce output
	 */
	public ImmutableList<ImmutableList<ItemStack>> getInput(ItemStack output) {
		return ImmutableList.of(getInputs());
	}

	public abstract M matches(ItemStack input, ItemStack output);

	public boolean dictatesOutputType() {
		return false;
	}

	public final void present(ItemStack output) {
		behaviors.forEach(b -> b.present(output));
	}

	public final void absent(ItemStack output) {
		behaviors.forEach(b -> b.absent(output));
	}

	public ImmutableList<ItemStack> getMatchingSubtypes(ItemStack stack) {
		Objects.requireNonNull(stack, "stack");
		NonNullList<ItemStack> subtypes = NonNullList.create();
		Item item = stack.getItem();
		for (CreativeTabs tab : item.getCreativeTabs()) {
			if (tab != null) {
				item.getSubItems(tab, subtypes);
			}
		}
		return subtypes.stream()
			.filter(itemStack -> matches(itemStack, ItemStack.EMPTY).doesMatch())
			.collect(Utils.toImmutableList());
	}

	public final void addTooltip(List<String> tooltip) {
		this.tooltip.accept(tooltip);
	}
}
