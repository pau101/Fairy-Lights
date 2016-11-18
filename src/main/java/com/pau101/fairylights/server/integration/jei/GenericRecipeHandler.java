package com.pau101.fairylights.server.integration.jei;

import com.pau101.fairylights.util.crafting.GenericRecipe;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public final class GenericRecipeHandler implements IRecipeHandler<GenericRecipe> {
	@Override
	public Class<GenericRecipe> getRecipeClass() {
		return GenericRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(GenericRecipe recipe) {
		return GenericRecipeCategory.UID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(GenericRecipe recipe) {
		return new GenericRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(GenericRecipe recipe) {
		return true;
	}
}
