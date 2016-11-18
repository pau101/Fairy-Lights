package com.pau101.fairylights.util.crafting.ingredient;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public class IngredientAuxiliaryOreInert extends IngredientAuxiliaryOre<Void> {
	public IngredientAuxiliaryOreInert(String name, boolean isRequired, int limit) {
		super(name, isRequired, limit);
	}

	@Nullable
	@Override
	public final Void accumulator() {
		return null;
	}

	@Override
	public final void consume(Void v, ItemStack ingredient) {}

	@Override
	public final boolean finish(Void v, ItemStack output) {
		return false;
	}
}
