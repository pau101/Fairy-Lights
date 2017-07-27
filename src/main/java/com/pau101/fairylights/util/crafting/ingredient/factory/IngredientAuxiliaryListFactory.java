package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryList;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public final class IngredientAuxiliaryListFactory extends GenericRecipeFactory.IngredientAuxiliaryFactory {
	@Override
	protected IngredientAuxiliary parse(JsonContext context, JsonObject json, GenericRecipeFactory.AuxiliaryBehaviorBuilder behaviors, Consumer<List<String>> tooltip, boolean isRequired, int limit) {
		JsonArray arr = JsonUtils.getJsonArray(json, "ingredients");
		ImmutableList.Builder<IngredientAuxiliary> ingredients = ImmutableList.builder();
		for (int i = 0; i < arr.size(); i++) {
			ingredients.add(GenericRecipeFactory.parseAuxiliaryIngredient(context, arr.get(i), "ingredient[" + i + "]"));
		}
		return new IngredientAuxiliaryList(behaviors.getBehaviors(), tooltip, behaviors.getAuxiliaryBehaviors(), isRequired, limit, ingredients.build());
	}
}
