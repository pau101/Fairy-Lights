package me.paulf.fairylights.server.integration.jei;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.ingredient.AuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.Ingredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GenericRecipeWrapper implements ICustomCraftingCategoryExtension {
    private final GenericRecipe recipe;

    private final ImmutableList<ImmutableList<ItemStack>> allInputs;

    // Only minimal stacks, ingredients that support multiple will only have first taken unless dictatesOutputType
    private final ImmutableList<ImmutableList<ItemStack>> minimalInputStacks;

    private final ImmutableList<ItemStack> outputs;

    private final Ingredient<?, ?>[] ingredientMatrix;

    private final int subtypeIndex;

    public GenericRecipeWrapper(final GenericRecipe recipe) {
        this.recipe = recipe;
        final ImmutableList.Builder<ImmutableList<ItemStack>> allInputs = ImmutableList.builder();
        final ImmutableList.Builder<ImmutableList<ItemStack>> minimalInputStacks = ImmutableList.builder();
        final RegularIngredient[] ingredients = recipe.getGenericIngredients();
        final AuxiliaryIngredient<?>[] aux = recipe.getAuxiliaryIngredients();
        this.ingredientMatrix = new Ingredient<?, ?>[9];
        int subtypeIndex = -1;
        for (int i = 0, auxIdx = 0; i < 9; i++) {
            final int x = i % 3;
            final int y = i / 3;
            boolean isEmpty = true;
            if (x < recipe.getWidth() && y < recipe.getHeight()) {
                final RegularIngredient ingredient = ingredients[x + y * recipe.getWidth()];
                final ImmutableList<ItemStack> ingInputs = ingredient.getInputs();
                if (ingInputs.size() > 0) {
                    if (ingredient.dictatesOutputType()) {
                        minimalInputStacks.add(ingInputs);
                        subtypeIndex = i;
                    } else {
                        minimalInputStacks.add(ImmutableList.of(ingInputs.get(0)));
                    }
                    this.ingredientMatrix[i] = ingredient;
                    allInputs.add(ingInputs);
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                AuxiliaryIngredient<?> ingredient = null;
                ImmutableList<ItemStack> stacks = null;
                boolean dictator = false;
                for (; auxIdx < aux.length; ) {
                    ingredient = aux[auxIdx++];
                    final ImmutableList<ItemStack> a = ingredient.getInputs();
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
                this.ingredientMatrix[i] = ingredient;
                allInputs.add(stacks);
            }
        }
        this.allInputs = allInputs.build();
        this.minimalInputStacks = minimalInputStacks.build();
        this.subtypeIndex = subtypeIndex;
        final ImmutableList.Builder<ItemStack> outputs = ImmutableList.builder();
        this.forOutputMatches((v, output) -> outputs.add(output));
        this.outputs = outputs.build();
    }

    private void forOutputMatches(final BiConsumer<ItemStack, ItemStack> outputConsumer) {
        final CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
            @Override
            public boolean canInteractWith(final PlayerEntity player) {
                return false;
            }

            @Override
            public void onCraftMatrixChanged(final IInventory inventory) {}
        }, this.getSize().width, this.getSize().height);
        if (this.subtypeIndex == -1) {
            for (int i = 0; i < this.minimalInputStacks.size(); i++) {
                final List<ItemStack> stacks = this.minimalInputStacks.get(i);
                crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
            }
            if (this.recipe.matches(crafting, null)) {
                outputConsumer.accept(ItemStack.EMPTY, this.recipe.getCraftingResult(crafting));
            }
        } else {
            final List<ItemStack> dictators = this.minimalInputStacks.get(this.subtypeIndex);
            for (final ItemStack subtype : dictators) {
                crafting.clear();
                for (int i = 0; i < this.minimalInputStacks.size(); i++) {
                    if (i == this.subtypeIndex) {
                        crafting.setInventorySlotContents(i, subtype);
                    } else {
                        final List<ItemStack> stacks = this.minimalInputStacks.get(i);
                        crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
                    }
                }
                if (this.recipe.matches(crafting, null)) {
                    outputConsumer.accept(subtype, this.recipe.getCraftingResult(crafting));
                }
            }
        }
    }

    @Override
    public Size2i getSize() {
        return new Size2i(3, 3);
    }

    public Input getInputsForOutput(final ItemStack output) {
        final RegularIngredient[] ingredients = this.recipe.getGenericIngredients();
        final List<List<ItemStack>> inputs = new ArrayList<>(9);
        final Ingredient<?, ?>[] ingredientMat = new Ingredient<?, ?>[9];
        final AuxiliaryIngredient<?>[] aux = this.recipe.getAuxiliaryIngredients();
        for (int i = 0, auxIngIdx = 0, auxIdx = 0; i < 9; i++) {
            final int x = i % 3;
            final int y = i / 3;
            final ImmutableList<ImmutableList<ItemStack>> ingInputs;
            Ingredient<?, ?> ingredient = null;
            if (x < this.recipe.getWidth() && y < this.recipe.getHeight()) {
                ingredient = ingredients[x + y * this.recipe.getWidth()];
                ingInputs = ingredient.getInput(output);
            } else {
                ingInputs = null;
            }
            if (ingInputs == null || ingInputs.isEmpty()) {
                boolean isEmpty = true;
                if (auxIngIdx < aux.length) {
                    ImmutableList<ImmutableList<ItemStack>> auxInputs = null;
                    AuxiliaryIngredient<?> ingredientAux = null;
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

    private Input getInputsForIngredient(final ItemStack ingredient) {
        final CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
            @Override
            public boolean canInteractWith(final PlayerEntity player) {
                return false;
            }

            @Override
            public void onCraftMatrixChanged(final IInventory inventory) {}
        }, this.getSize().width, this.getSize().height);
        for (int i = 0; i < this.allInputs.size(); i++) {
            final List<ItemStack> options = this.allInputs.get(i);
            ItemStack matched = null;
            for (final ItemStack o : options) {
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
            for (int n = 0; n < this.minimalInputStacks.size(); n++) {
                final List<ItemStack> stacks = this.minimalInputStacks.get(n);
                crafting.setInventorySlotContents(n, i == n ? matched : stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
            }
            if (this.recipe.matches(crafting, null)) {
                final List<List<ItemStack>> inputs = new ArrayList<>(this.allInputs.size());
                for (int n = 0; n < this.allInputs.size(); n++) {
                    final List<ItemStack> stacks = this.allInputs.get(n);
                    inputs.add(i == n ? Collections.singletonList(matched) : stacks.isEmpty() ? Collections.singletonList(ItemStack.EMPTY) : stacks);
                }
                return new Input(inputs, this.ingredientMatrix);
            }
        }
        return null;
    }

    public List<List<ItemStack>> getOutput(final List<List<ItemStack>> inputs) {
        final CraftingInventory crafting = new CraftingInventory(new Container(null, 0) {
            @Override
            public boolean canInteractWith(final PlayerEntity player) {
                return false;
            }

            @Override
            public void onCraftMatrixChanged(final IInventory inventory) {}
        }, this.getSize().width, this.getSize().height);
        int size = 1;
        for (final List<ItemStack> stack : inputs) {
            if (stack.size() > 0) {
                size = Mth.lcm(stack.size(), size);
            }
        }
        final List<ItemStack> outputs = new ArrayList<>(size);
        for (int n = 0; n < size; n++) {
            for (int i = 0; i < inputs.size(); i++) {
                final List<ItemStack> stacks = inputs.get(i);
                crafting.setInventorySlotContents(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(n % stacks.size()));
            }
            if (this.recipe.matches(crafting, null)) {
                outputs.add(this.recipe.getCraftingResult(crafting));
            } else {
                throw new IllegalStateException("Recipe generation didn't give output for: " +
                    IntStream.range(0, crafting.getWidth() * crafting.getHeight())
                        .mapToObj(crafting::getStackInSlot)
                        .map(s -> Objects.toString(s.getItem().getRegistryName()))
                        .collect(Collectors.joining(", ")));
            }
        }
        return Collections.singletonList(outputs);
    }

    @Override
    public void setIngredients(final IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, (List<List<ItemStack>>) (List<?>) this.allInputs);
        ingredients.setOutputs(VanillaTypes.ITEM, this.outputs);
    }

    @Override
    public void setRecipe(final IRecipeLayout layout, final IIngredients ingredients) {
        final IFocus<?> focus = layout.getFocus();
        final IGuiItemStackGroup group = layout.getItemStacks();
        if (focus != null && focus.getValue() instanceof ItemStack) {
            final ItemStack stack = (ItemStack) focus.getValue();
            final Input input;
            if (focus.getMode() == Mode.OUTPUT) {
                input = this.getInputsForOutput(stack);
            } else {
                input = this.getInputsForIngredient(stack);
            }
            if (input != null) {
                ingredients.setInputLists(VanillaTypes.ITEM, input.inputs);
                ingredients.setOutputLists(VanillaTypes.ITEM, this.getOutput(input.inputs));
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

        private Input(final List<List<ItemStack>> inputs, final Ingredient<?, ?>[] ingredients) {
            this.inputs = inputs;
            this.ingredients = ingredients;
        }
    }

    private final class Tooltips implements ITooltipCallback<ItemStack> {
        Ingredient<?, ?>[] ingredients;

        public Tooltips(final Ingredient<?, ?>[] ingredients) {
            this.ingredients = ingredients;
        }

        @Override
        public void onTooltip(final int slot, final boolean input, final ItemStack ingredient, final List<String> tooltip) {
            if (input) {
                final Ingredient<?, ?> ing = this.ingredients[slot - 1];
                if (ing != null) {
                    ing.addTooltip(tooltip);
                }
            }
        }
    }
}
