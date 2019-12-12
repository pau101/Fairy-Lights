package me.paulf.fairylights.server.integration.jei;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.ingredient.Ingredient;
import me.paulf.fairylights.util.crafting.ingredient.IngredientAuxiliary;
import me.paulf.fairylights.util.crafting.ingredient.IngredientRegular;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Size2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public final class GenericRecipeWrapper implements ICustomCraftingCategoryExtension {
	private final GenericRecipe recipe;

	private final ImmutableList<ImmutableList<ItemStack>> allInputs;

	// Only minimal stacks, ingredients that support multiple will only have first taken unless dictatesOutputType
	private final ImmutableList<ImmutableList<ItemStack>> minimalInputStacks;

	private final ImmutableList<ItemStack> outputs;

	private final Ingredient<?, ?>[] ingredientMatrix;

	private final int subtypeIndex;

	public GenericRecipeWrapper(GenericRecipe recipe) {
		this.recipe = recipe;
		ImmutableList.Builder<ImmutableList<ItemStack>> allInputs = ImmutableList.builder();
		ImmutableList.Builder<ImmutableList<ItemStack>> minimalInputStacks = ImmutableList.builder();
		IngredientRegular[] ingredients = recipe.getGenericIngredients();
		IngredientAuxiliary<?>[] aux = recipe.getAuxiliaryIngredients();
		ingredientMatrix = new Ingredient<?, ?>[9];
		int subtypeIndex = -1;
		for (int i = 0, auxIdx = 0; i < 9; i++) {
			int x = i % 3, y = i / 3;
			boolean isEmpty = true;
			if (x < recipe.getWidth() && y < recipe.getHeight()) {
				IngredientRegular ingredient = ingredients[x + y * recipe.getWidth()];
				ImmutableList<ItemStack> ingInputs = ingredient.getInputs();
				if (ingInputs.size() > 0) {
					if (ingredient.dictatesOutputType()) {
						minimalInputStacks.add(ingInputs);
						subtypeIndex = i;
					} else {
						minimalInputStacks.add(ImmutableList.of(ingInputs.get(0)));
					}
					ingredientMatrix[i] = ingredient;
					allInputs.add(ingInputs);
					isEmpty = false;
				}
			}
			if (isEmpty) {
				IngredientAuxiliary<?> ingredient = null;
				ImmutableList<ItemStack> stacks = null;
				boolean dictator = false;
				for (; auxIdx < aux.length;) {
					ingredient = aux[auxIdx++];
					ImmutableList<ItemStack> a = ingredient.getInputs(); 
					if (a.size() > 0) {
						stacks = a;
						if (ingredient.dictatesOutputType()) {
							subtypeIndex = i;
							dictator = true;
						}
						break;
					}
				}
				if (stacks == null) {
					stacks = ImmutableList.of();
					ingredient = null;
				}
				minimalInputStacks.add(stacks.isEmpty() || dictator ? stacks : ImmutableList.of(stacks.get(0)));
				ingredientMatrix[i] = ingredient;
				allInputs.add(stacks);
			}
		}
		this.allInputs = allInputs.build();
		this.minimalInputStacks = minimalInputStacks.build();
		this.subtypeIndex = subtypeIndex; 
		ImmutableList.Builder<ItemStack> outputs = ImmutableList.builder();
		forOutputMatches((v, output) -> outputs.add(output));
		this.outputs = outputs.build();
	}

	private void forOutputMatches(BiConsumer<ItemStack, ItemStack> outputConsumer) {
		CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
			@Override
			public boolean canInteractWith(PlayerEntity player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getSize().width, getSize().height);
		if (subtypeIndex == -1) {
			for (int i = 0; i < minimalInputStacks.size(); i++) {
				List<ItemStack> stacks = minimalInputStacks.get(i);
				crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
			}
			if (recipe.matches(crafting, null)) {
				outputConsumer.accept(ItemStack.EMPTY, recipe.getCraftingResult(crafting));
			}
		} else {
			List<ItemStack> dictators = minimalInputStacks.get(subtypeIndex);
			for (ItemStack subtype : dictators) {
				crafting.clear();
				for (int i = 0; i < minimalInputStacks.size(); i++) {
					if (i == subtypeIndex) {
						crafting.setInventorySlotContents(i, subtype);
					} else {
						List<ItemStack> stacks = minimalInputStacks.get(i);
						crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
					}
				}
				if (recipe.matches(crafting, null)) {
					outputConsumer.accept(subtype, recipe.getCraftingResult(crafting));
				}
			}
		}
	}

	@Override
	public Size2i getSize() {
		return new Size2i(3, 3);
	}

	public Input getInputsForOutput(ItemStack output) {
		IngredientRegular[] ingredients = recipe.getGenericIngredients();
		List<List<ItemStack>> inputs = new ArrayList<>(9);
		Ingredient<?, ?>[] ingredientMat = new Ingredient<?, ?>[9];
		IngredientAuxiliary<?>[] aux = recipe.getAuxiliaryIngredients();
		for (int i = 0, auxIngIdx = 0, auxIdx = 0; i < 9; i++) {
			int x = i % 3, y = i / 3;
			ImmutableList<ImmutableList<ItemStack>> ingInputs;
			Ingredient<?, ?> ingredient = null;
			if (x < recipe.getWidth() && y < recipe.getHeight()) {
				ingredient = ingredients[x + y * recipe.getWidth()];
				ingInputs = ingredient.getInput(output);
			} else {
				ingInputs = null;
			}
			if (ingInputs == null || ingInputs.isEmpty()) {
				boolean isEmpty = true;
				if (auxIngIdx < aux.length) {
					ImmutableList<ImmutableList<ItemStack>> auxInputs = null;
					IngredientAuxiliary<?> ingredientAux = null;
					for (; auxIngIdx < aux.length; auxIngIdx++) {
						ingredientAux = aux[auxIngIdx];
						auxInputs = ingredientAux.getInput(output);
						if (auxInputs.size() > 0) {
							break;
						}
					}
					if (auxInputs != null && auxInputs.size() > 0) {
						inputs.add(auxInputs.get(auxIdx++));
						ingredientMat[i] = ingredientAux;
						if (auxIdx == auxInputs.size()) {
							auxIdx = 0;
							auxIngIdx++;
						}
						isEmpty = false;
					}
				}
				if (isEmpty) {
					inputs.add(Collections.emptyList());
				}
			} else {
				inputs.add(ingInputs.get(0));
				ingredientMat[i] = ingredient;
			}
		}
		return new Input(inputs, ingredientMat);
	}

	private Input getInputsForIngredient(ItemStack ingredient) {
		CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
			@Override
			public boolean canInteractWith(PlayerEntity player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getSize().width, getSize().height);
		 for (int i = 0; i < allInputs.size(); i++) {
			 List<ItemStack> options = allInputs.get(i);
			 ItemStack matched = null;
			 for (ItemStack o : options) {
				 if (ingredient.getItem() == o.getItem()) {
					 matched = ingredient.copy();
					 matched.setCount(1);
					 break;
				 }
			}
			if (matched == null) {
				continue;
			}
			crafting.clear();
			for (int n = 0; n < minimalInputStacks.size(); n++) {
				List<ItemStack> stacks = minimalInputStacks.get(n);
				crafting.setInventorySlotContents(n, i == n ? matched : stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
			}
			if (recipe.matches(crafting, null)) {
				List<List<ItemStack>> inputs = new ArrayList<>(allInputs.size());
				for (int n = 0; n < allInputs.size(); n++) {
					List<ItemStack> stacks = allInputs.get(n);
					inputs.add(i == n ? Collections.singletonList(matched) : stacks.isEmpty() ? Collections.singletonList(ItemStack.EMPTY) : stacks);
				};
				return new Input(inputs, ingredientMatrix);
			}
		}
		 return null;
	}

	public List<List<ItemStack>> getOutput(List<List<ItemStack>> inputs) {
		CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
			@Override
			public boolean canInteractWith(PlayerEntity player) {
				return false;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventory) {}
		}, getSize().width, getSize().height);
		int size = 1;
		for (List<ItemStack> stack : inputs) {
			if (stack.size() > 0) {
				size = Mth.lcm(stack.size(), size);	
			}
		}
		List<ItemStack> outputs = new ArrayList<>(size);
		for (int n = 0; n < size; n++) {
			for (int i = 0; i < inputs.size(); i++) {
				List<ItemStack> stacks = inputs.get(i);
				crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(n % stacks.size()));
			}
			if (recipe.matches(crafting, null)) {
				outputs.add(recipe.getCraftingResult(crafting));
			} else {
				throw new IllegalStateException("Bad recipe generation which doesn't give output");
			}
		}
		return Collections.singletonList(outputs);
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, (List<List<ItemStack>>) (List<?>) allInputs);
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IIngredients ingredients) {
		IFocus<?> focus = layout.getFocus();
		IGuiItemStackGroup group = layout.getItemStacks();
		if (focus != null && focus.getValue() instanceof ItemStack) {
			ItemStack stack = (ItemStack) focus.getValue();
			Input input;
			if (focus.getMode() == Mode.OUTPUT) {
				input = getInputsForOutput(stack);	
			} else {
				input = getInputsForIngredient(stack);
			}
			if (input != null) {
				ingredients.setInputLists(VanillaTypes.ITEM, input.inputs);
				ingredients.setOutputLists(VanillaTypes.ITEM, getOutput(input.inputs));
				group.addTooltipCallback(new Tooltips(input.ingredients));
			} else {
				// Some Ingredient is picky with requirements, should allow a GenericRecipe to have a "smart input provider"
			}
		}
		group.set(ingredients);
	}

	private final class Input {
		List<List<ItemStack>> inputs;

		Ingredient<?, ?>[] ingredients;

		private Input(List<List<ItemStack>> inputs, Ingredient<?, ?>[] ingredients) {
			this.inputs = inputs;
			this.ingredients = ingredients;
		}
	}

	private final class Tooltips implements ITooltipCallback<ItemStack> {
		Ingredient<?, ?>[] ingredients;

		public Tooltips(Ingredient<?, ?>[] ingredients) {
			this.ingredients = ingredients;
		}

		@Override
		public void onTooltip(int slot, boolean input, ItemStack ingredient, List<String> tooltip) {
			if (input) {
				Ingredient<?, ?> ing = ingredients[slot - 1];
				if (ing != null) {
					ing.addTooltip(tooltip);
				}
			}
		}
	}
}
