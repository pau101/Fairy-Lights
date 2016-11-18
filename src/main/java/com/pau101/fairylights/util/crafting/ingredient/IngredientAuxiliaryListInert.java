package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public class IngredientAuxiliaryListInert extends IngredientAuxiliaryList<Void> {
	public IngredientAuxiliaryListInert(boolean isRequired, int limit, IngredientAuxiliary<?>... ingredients) {
		super(isRequired, limit, ingredients);
	}
	public IngredientAuxiliaryListInert(boolean isRequired, IngredientAuxiliary<?>... ingredients) {
		super(isRequired, ingredients);
	}

	public IngredientAuxiliaryListInert(List<? extends IngredientAuxiliary<?>> ingredients, boolean isRequired, int limit) {
		super(Objects.requireNonNull(ingredients, "ingredients"), isRequired, limit);
	}

	@Nullable
	@Override
	public Void accumulator() {
		return null;
	}

	@Override
	public void consume(Void v, ItemStack ingredient) {}

	@Override
	public boolean finish(Void v, ItemStack output) {
		return false;
	}
}
