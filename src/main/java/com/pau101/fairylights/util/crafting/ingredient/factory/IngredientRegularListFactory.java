package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularList;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;

public final class IngredientRegularListFactory extends GenericRecipeFactory.IngredientRegularFactory {
	@Override
	public IngredientRegular parse(JsonContext context, JsonObject json, GenericRecipeFactory.RegularBehaviorBuilder behaviors, Consumer<List<String>> tooltip) {
		JsonArray arr = JsonUtils.getJsonArray(json, "ingredients");
		ImmutableList.Builder<IngredientRegular> ingredients = ImmutableList.builder();
		for (int i = 0; i < arr.size(); i++) {
			ingredients.add(GenericRecipeFactory.parseRegularIngredient(context, arr.get(i), "ingredient[" + i + "]"));
		}
		return new IngredientRegularList(behaviors.getBehaviors(), tooltip, behaviors.getRegularBehaviors(), ingredients.build());
	}
}
