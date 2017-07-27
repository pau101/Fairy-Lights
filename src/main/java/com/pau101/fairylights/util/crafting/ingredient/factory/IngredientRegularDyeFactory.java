package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegularDye;
import net.minecraftforge.common.crafting.JsonContext;

public final class IngredientRegularDyeFactory extends GenericRecipeFactory.IngredientRegularFactory {
	@Override
	public IngredientRegular parse(JsonContext context, JsonObject json, GenericRecipeFactory.RegularBehaviorBuilder behaviors, Consumer<List<String>> tooltip) {
		return new IngredientRegularDye(behaviors.getBehaviors(), tooltip, behaviors.getRegularBehaviors());
	}
}
