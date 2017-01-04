package com.pau101.fairylights.server.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import com.pau101.fairylights.util.crafting.GenericRecipe;
import com.pau101.fairylights.util.crafting.ingredient.IngredientRegular;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public final class GenericRecipeWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper, ICustomCraftingRecipeWrapper {
	private final GenericRecipe recipe;

	private final List<List<ItemStack>> inputs;

	private final List<ItemStack> outputs;

	// Only minimal stacks, ingredients that support multiple will only have first taken unless dictatesOutputType
	private final List<List<ItemStack>> inputStacks;

	private final int subtypeIndex;

	public GenericRecipeWrapper(GenericRecipe recipe) {
		this.recipe = recipe;
		IngredientRegular[] ingredients = recipe.getIngredients();
		int recipeSize = getWidth() * getHeight();
		inputs = new ArrayList<>(recipeSize);
		outputs = new ArrayList<>();
		inputStacks = new ArrayList<>();
		int subtypeIndex = -1;
		for (int i = 0; i < ingredients.length; i++) {
			IngredientRegular ingredient = ingredients[i];
			List<ItemStack> ingInputs = ingredient.getInputs();
			if (ingInputs.isEmpty()) {
				inputStacks.add(ingInputs);
			} else if (ingredient.dictatesOutputType()) {
				inputStacks.add(ingInputs);
				subtypeIndex = i;
			} else {
				inputStacks.add(Collections.singletonList(ingInputs.get(0)));
			}
			inputs.add(ingInputs);
		}
		this.subtypeIndex = subtypeIndex;
		forOutputMatches((v, output) -> outputs.add(output));
		// TODO: auxiliary items when feasible in JEI
	}

	private void forOutputMatches(BiConsumer<ItemStack, ItemStack> outputConsumer) {
		InventoryCrafting crafting = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getWidth(), getHeight());
		if (subtypeIndex == -1) {
			for (int i = 0; i < inputStacks.size(); i++) {
				List<ItemStack> stacks = inputStacks.get(i);
				crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.field_190927_a : stacks.get(0));
			}
			if (recipe.matches(crafting, null)) {
				outputConsumer.accept(ItemStack.field_190927_a, recipe.getCraftingResult(crafting));
			}
		} else {
			List<ItemStack> dictators = inputStacks.get(subtypeIndex);
			for (ItemStack subtype : dictators) {
				crafting.clear();
				for (int i = 0; i < inputStacks.size(); i++) {
					if (i == subtypeIndex) {
						crafting.setInventorySlotContents(i, subtype);
					} else {
						List<ItemStack> stacks = inputStacks.get(i);
						crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.field_190927_a : stacks.get(0));
					}
				}
				if (recipe.matches(crafting, null)) {
					outputConsumer.accept(subtype, recipe.getCraftingResult(crafting));
				}
			}
		}
	}

	@Override
	public int getWidth() {
		return recipe.getWidth();
	}

	@Override
	public int getHeight() {
		return recipe.getHeight();
	}

	public List<List<ItemStack>> getInputsForOutput(ItemStack focus) {
		if (subtypeIndex == -1) {
			return inputs;	
		}
		List<ItemStack> variants = new ArrayList<>();
		forOutputMatches((variant, output) -> {
			if (output.getItem() == focus.getItem() &&
				output.getItemDamage() == focus.getItemDamage() &&
				ItemStack.areItemStackTagsEqual(output, focus)
			) {
				variants.add(variant);
			}
		});
		List<List<ItemStack>> inputs = new ArrayList<>(this.inputs);
		inputs.set(subtypeIndex, variants);
		return inputs;
	}

	private List<List<ItemStack>> getInputsForIngredient(ItemStack ingredient) {
		InventoryCrafting crafting = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getWidth(), getHeight());
		 for (int i = 0; i < this.inputs.size(); i++) {
			 List<ItemStack> options = this.inputs.get(i);
			 ItemStack matched = null;
			 for (ItemStack o : options) {
				 if (ingredient.getItem() == o.getItem() && ingredient.getItemDamage() == o.getItemDamage() && ItemStack.areItemStackTagsEqual(ingredient, o)) {
					 matched = o;
					 break;
				 }
			}
			if (matched == null) {
				continue;
			}
			crafting.clear();
			for (int n = 0; n < inputStacks.size(); n++) {
				List<ItemStack> stacks = inputStacks.get(n);
				crafting.setInventorySlotContents(n, i == n ? matched : stacks.isEmpty() ? ItemStack.field_190927_a : stacks.get(0));
			}
			if (recipe.matches(crafting, null)) {
				List<List<ItemStack>> inputs = new ArrayList<>(this.inputs.size());
				for (int n = 0; n < this.inputs.size(); n++) {
					List<ItemStack> stacks = this.inputs.get(n);
					inputs.add(i == n ? Collections.singletonList(matched) : stacks.isEmpty() ? Collections.singletonList(ItemStack.field_190927_a) : stacks);
				};
				return inputs;
			}
		}
		 return null;
	}

	public List<List<ItemStack>> getOutput(List<List<ItemStack>> inputs) {
		InventoryCrafting crafting = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getWidth(), getHeight());
		if (subtypeIndex == -1 || inputs.get(subtypeIndex).size() == 1) {
			for (int i = 0; i < inputs.size(); i++) {
				List<ItemStack> stacks = inputs.get(i);
				crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.field_190927_a : stacks.get(0));
			}
			if (recipe.matches(crafting, null)) {
				return Collections.singletonList(Collections.singletonList(recipe.getCraftingResult(crafting)));
			}
		}
		return Collections.singletonList(outputs);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IIngredients ingredients) {
		IFocus<?> focus = layout.getFocus();
		Object value = focus.getValue();
		if (value instanceof ItemStack) {
			ItemStack stack = (ItemStack) value;
			List<List<ItemStack>> inputs = null;
			if (focus.getMode() == Mode.OUTPUT) {
				inputs = getInputsForOutput(stack);	
			} else {
				inputs = getInputsForIngredient(stack);
			}
			if (inputs != null) {
				ingredients.setInputLists(ItemStack.class, inputs);
				ingredients.setOutputLists(ItemStack.class, getOutput(inputs));
			} else {
				// Some Ingredient is picky with requirements, should allow a GenericRecipe to have a "smart input provider"
			}
		}
		layout.getItemStacks().set(ingredients);
	}
}
