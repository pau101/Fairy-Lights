package me.paulf.fairylights.util.crafting.ingredient;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public abstract class DyeAuxiliaryIngredient<A> implements AuxiliaryIngredient<A> {
	private final boolean isRequired;

	private final int limit;

	public DyeAuxiliaryIngredient(boolean isRequired, int limit) {
		this.isRequired = isRequired;
		this.limit = limit;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public GenericRecipe.MatchResultAuxiliary matches(ItemStack input, ItemStack output) {
		return new GenericRecipe.MatchResultAuxiliary(this, input, OreDictUtils.isDye(input), Collections.emptyList());
	}

	@Override
	public ImmutableList<ItemStack> getInputs() {
		return OreDictUtils.getAllDyes();
	}
}
