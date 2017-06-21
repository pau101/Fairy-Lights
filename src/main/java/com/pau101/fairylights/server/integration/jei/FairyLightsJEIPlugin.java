package com.pau101.fairylights.server.integration.jei;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.crafting.GenericRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public final class FairyLightsJEIPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.handleRecipes(GenericRecipe.class, GenericRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
		registry.registerSubtypeInterpreter(FairyLights.tinsel, new TinselSubtypeInterpreter());
	}
}
