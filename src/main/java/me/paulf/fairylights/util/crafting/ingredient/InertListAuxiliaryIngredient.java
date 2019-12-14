package me.paulf.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public class InertListAuxiliaryIngredient extends ListAuxiliaryIngredient<Void> {
	public InertListAuxiliaryIngredient(boolean isRequired, int limit, AuxiliaryIngredient<?>... ingredients) {
		super(isRequired, limit, ingredients);
	}

	public InertListAuxiliaryIngredient(boolean isRequired, AuxiliaryIngredient<?>... ingredients) {
		super(isRequired, ingredients);
	}

	public InertListAuxiliaryIngredient(List<? extends AuxiliaryIngredient<?>> ingredients, boolean isRequired, int limit) {
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
