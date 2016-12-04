package com.pau101.fairylights.server.integration.jei;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class GenericRecipeCategory extends BlankRecipeCategory<GenericRecipeWrapper> {
	public static final String UID = "fairylights.generic";

	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

	private static final int CRAFT_OUTPUT_SLOT = 0;

	private static final int CRAFT_INPUT_SLOT = 1;

	private static final int BACKGROUND_U = 29;

	private static final int BACKGROUND_V = 16;

	private static final int WIDTH = 116;

	private static final int HEIGHT = 54;

	private final IDrawable background;

	private final String localizedName;

	private final ICraftingGridHelper craftingGridHelper;

	public GenericRecipeCategory(IGuiHelper helper) {
		background = helper.createDrawable(TEXTURE, BACKGROUND_U, BACKGROUND_V, WIDTH, HEIGHT);
		localizedName = I18n.format("gui.jei.category.craftingTable");
		craftingGridHelper = helper.createCraftingGridHelper(CRAFT_INPUT_SLOT, CRAFT_OUTPUT_SLOT);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, GenericRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiItemStackGroup isg = layout.getItemStacks();
		isg.init(CRAFT_OUTPUT_SLOT, false, 94, 18);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int index = CRAFT_INPUT_SLOT + x + (y * 3);
				isg.init(index, true, x * 18, y * 18);
			}
		}
		List<List<ItemStack>> inputs;
		Object focus = layout.getFocus().getValue();
		if (focus instanceof ItemStack) {
			inputs = wrapper.getInputs((ItemStack) focus);
		} else {
			inputs = ingredients.getInputs(ItemStack.class);
		}
		craftingGridHelper.setInputStacks(isg, inputs, wrapper.getWidth(), wrapper.getHeight());
		craftingGridHelper.setOutput(isg, ingredients.getOutputs(ItemStack.class).get(0));
	}
}
