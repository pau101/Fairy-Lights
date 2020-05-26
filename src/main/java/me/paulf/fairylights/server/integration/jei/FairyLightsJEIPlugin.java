package me.paulf.fairylights.server.integration.jei;

import me.paulf.fairylights.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.util.crafting.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.*;
import mezz.jei.api.registration.*;
import net.minecraft.client.*;
import net.minecraft.client.world.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;

import java.util.stream.*;

@JeiPlugin
public final class FairyLightsJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FairyLights.ID, "plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(GenericRecipe.class, GenericRecipeWrapper::new);
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        final ClientWorld world = Minecraft.getInstance().world;
        final RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(recipeManager.getRecipes().stream()
                .filter(GenericRecipe.class::isInstance)
                .collect(Collectors.toList()),
            VanillaRecipeCategoryUid.CRAFTING
        );
    }

    @Override
    public void registerItemSubtypes(final ISubtypeRegistration registry) {
        registry.registerSubtypeInterpreter(FLItems.TINSEL.orElseThrow(IllegalStateException::new), new TinselSubtypeInterpreter());
        //registry.useNbtForSubtypes(FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
    }
}
