package com.pau101.fairylights.util.crafting.ingredient.factory;

import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.pau101.fairylights.util.crafting.GenericRecipeFactory;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import com.pau101.fairylights.util.crafting.ingredient.IngredientAuxiliaryDye;
import net.minecraftforge.common.crafting.JsonContext;

public final class IngredientAuxiliaryDyeFactory extends GenericRecipeFactory.IngredientAuxiliaryFactory {
	@Override
	protected IngredientAuxiliary parse(JsonContext context, JsonObject json, GenericRecipeFactory.AuxiliaryBehaviorBuilder behaviors, Consumer<List<String>> tooltip, boolean isRequired, int limit) {
		return new IngredientAuxiliaryDye(behaviors.getBehaviors(), tooltip, behaviors.getAuxiliaryBehaviors(), isRequired, limit);
	}
}
