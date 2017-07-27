package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularList;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularOre;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

public final class IngredientRegularOreFactory extends GenericRecipeFactory.IngredientRegularFactory {
	@Override
	public IngredientRegular parse(JsonContext context, JsonObject json, GenericRecipeFactory.RegularBehaviorBuilder behaviors, Consumer<List<String>> tooltip) {
		String oreName = JsonUtils.getString(json, "ore");
		if (!OreDictionary.doesOreNameExist(oreName)) {
			throw new JsonSyntaxException("Unknown ore name: " + oreName);
		}
		return new IngredientRegularList(behaviors.getBehaviors(), tooltip, behaviors.getRegularBehaviors(), IngredientRegularOre.getOres(oreName));
	}
}
