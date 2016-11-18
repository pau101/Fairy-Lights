package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Preconditions;

public abstract class IngredientAuxiliaryOre<A> extends IngredientAuxiliaryList<A> implements IngredientAuxiliary<A> {
	public IngredientAuxiliaryOre(String name, boolean isRequired, int limit) {
		super(getOres(name), isRequired, limit);
	}

	private static List<IngredientAuxiliaryBasicInert> getOres(String name) {
		Preconditions.checkArgument(OreDictionary.doesOreNameExist(name), "ore name must exist");
		return OreDictionary.getOres(name).stream().map(IngredientAuxiliaryBasicInert::new).collect(Collectors.toList());
	}
}
