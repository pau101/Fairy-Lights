package com.pau101.fairylights.util.crafting.ingredient;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientAuxiliaryOre extends IngredientAuxiliaryList {
	private final String name;

	public IngredientAuxiliaryOre(String name, boolean isRequired, int limit) {
		super(isRequired, limit, getOres(name));
		this.name = name;
	}

	public static List<IngredientAuxiliary> getOres(String name) {
		Preconditions.checkArgument(OreDictionary.doesOreNameExist(name), "ore name must exist");
		return OreDictionary.getOres(name).stream().map(IngredientAuxiliaryItem::new).collect(Collectors.toList());
	}
}
