package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryList;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryOre;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

public final class IngredientAuxiliaryOreFactory extends GenericRecipeFactory.IngredientAuxiliaryFactory {
	@Override
	protected IngredientAuxiliary parse(JsonContext context, JsonObject json, GenericRecipeFactory.AuxiliaryBehaviorBuilder behaviors, Consumer<List<String>> tooltip, boolean isRequired, int limit) {
		String oreName = JsonUtils.getString(json, "ore");
		if (!OreDictionary.doesOreNameExist(oreName)) {
			throw new JsonSyntaxException("Unknown ore name: " + oreName);
		}
		return new IngredientAuxiliaryList(behaviors.getBehaviors(), tooltip, behaviors.getAuxiliaryBehaviors(), isRequired, limit, IngredientAuxiliaryOre.getOres(oreName));
	}
}
