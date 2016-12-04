package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import net.minecraftforge.oredict.OreDictionary;

public class IngredientRegularOre extends IngredientRegularList {
	private final String name;

	public IngredientRegularOre(String name) {
		super(getOres(name));
		this.name = name;
	}

	private static List<IngredientRegular> getOres(String name) {
		Preconditions.checkArgument(OreDictionary.doesOreNameExist(name), "Ore name must exist");
		return OreDictionary.getOres(name).stream().map(IngredientRegularBasic::new).collect(Collectors.toList());
	}
}
