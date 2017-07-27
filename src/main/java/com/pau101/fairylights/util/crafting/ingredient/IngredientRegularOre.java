package com.pau101.fairylights.util.crafting.ingredient;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.util.Utils;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientRegularOre extends IngredientRegularList {
	private final String name;

	public IngredientRegularOre(String name) {
		super(getOres(name));
		this.name = name;
	}

	public static ImmutableList<IngredientRegular> getOres(String name) {
		Preconditions.checkArgument(OreDictionary.doesOreNameExist(name), "Ore name must exist");
		return OreDictionary.getOres(name).stream().map(IngredientRegularItem::new).collect(Utils.toImmutableList());
	}
}
