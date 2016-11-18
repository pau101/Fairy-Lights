package com.pau101.fairylights.server.integration.jei;

import com.pau101.fairylights.FairyLights;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;

@JEIPlugin
public final class FairyLightsJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(new GenericRecipeHandler());
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new GenericRecipeCategory(helper));
		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		registry.addRecipeClickArea(GuiCrafting.class, 88, 32, 28, 23, GenericRecipeCategory.UID);
		transferRegistry.addRecipeTransferHandler(ContainerWorkbench.class, GenericRecipeCategory.UID, 1, 9, 10, 36);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.CRAFTING_TABLE), GenericRecipeCategory.UID);
		
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
		registry.registerNbtInterpreter(FairyLights.tinsel, new TinselSubtypeInterpreter());
	}
}
