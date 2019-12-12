package me.paulf.fairylights.server.integration.jei;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Collectors;

@JeiPlugin
public final class FairyLightsJEIPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(FairyLights.ID, "plugin");
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(GenericRecipe.class, GenericRecipeWrapper::new);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientWorld world = Minecraft.getInstance().world;
		RecipeManager recipeManager = world.getRecipeManager();
		registration.addRecipes(recipeManager.getRecipes().stream()
			.filter(GenericRecipe.class::isInstance)
			.collect(Collectors.toList()),
			VanillaRecipeCategoryUid.CRAFTING
		);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		registry.registerSubtypeInterpreter(FLItems.TINSEL.orElseThrow(IllegalStateException::new), new TinselSubtypeInterpreter());
		//registry.useNbtForSubtypes(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
	}
}
