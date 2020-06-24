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
    public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(GenericRecipe.class, GenericRecipeWrapper::new);
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        final ClientWorld world = Minecraft.getInstance().world;
        final RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(
            recipeManager.getRecipes().stream()
                .filter(GenericRecipe.class::isInstance)
                .map(GenericRecipe.class::cast)
                .filter(GenericRecipe::isDynamic)
                .collect(Collectors.toList()),
            VanillaRecipeCategoryUid.CRAFTING
        );
    }

    @Override
    public void registerItemSubtypes(final ISubtypeRegistration registry) {
        registry.registerSubtypeInterpreter(FLItems.TINSEL.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(FLItems.TRIANGLE_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(FLItems.SPEARHEAD_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(FLItems.SWALLOWTAIL_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(FLItems.SQUARE_PENNANT.get(), new ColorSubtypeInterpreter());
        FLItems.lights().forEach(i -> registry.registerSubtypeInterpreter(i, new ColorSubtypeInterpreter()));
    }
}
