package me.paulf.fairylights.server.integration.jei;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.util.FLMth;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.ingredient.AuxiliaryIngredient;
import me.paulf.fairylights.util.crafting.ingredient.GenericIngredient;
import me.paulf.fairylights.util.crafting.ingredient.RegularIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GenericRecipeWrapper implements ICraftingCategoryExtension {
    private final GenericRecipe recipe;

    private final List<List<ItemStack>> allInputs;

    // Only minimal stacks, ingredients that support multiple will only have first taken unless dictatesOutputType
    private final List<List<ItemStack>> minimalInputStacks;

    private final List<ItemStack> outputs;

    private final GenericIngredient<?, ?>[] ingredientMatrix;

    private final int subtypeIndex;

    public GenericRecipeWrapper(final GenericRecipe recipe) {
        this.recipe = recipe;
        final List<List<ItemStack>> allInputs = new ArrayList<>();
        final List<List<ItemStack>> minimalInputStacks = new ArrayList<>();
        final RegularIngredient[] ingredients = recipe.getGenericIngredients();
        final AuxiliaryIngredient<?>[] aux = recipe.getAuxiliaryIngredients();
        this.ingredientMatrix = new GenericIngredient<?, ?>[9];
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
        this.allInputs = allInputs;
        this.minimalInputStacks = minimalInputStacks;
        this.subtypeIndex = subtypeIndex;
        final ImmutableList.Builder<ItemStack> outputs = ImmutableList.builder();
        this.forOutputMatches((v, output) -> outputs.add(output));
        this.outputs = outputs.build();
    }

    private void forOutputMatches(final BiConsumer<ItemStack, ItemStack> outputConsumer) {
        final CraftingContainer crafting = new TransientCraftingContainer(new AbstractContainerMenu(null, 0) {
            @Override
            public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, this.getWidth(), this.getHeight());
        if (this.subtypeIndex == -1) {
            for (int i = 0; i < this.minimalInputStacks.size(); i++) {
                final List<ItemStack> stacks = this.minimalInputStacks.get(i);
                crafting.setItem(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
            }
            if (this.recipe.matches(crafting, null)) {
                outputConsumer.accept(ItemStack.EMPTY, this.recipe.assemble(crafting, null));
            }
        } else {
            final List<ItemStack> dictators = this.minimalInputStacks.get(this.subtypeIndex);
            for (final ItemStack subtype : dictators) {
                crafting.clearContent();
                for (int i = 0; i < this.minimalInputStacks.size(); i++) {
                    if (i == this.subtypeIndex) {
                        crafting.setItem(i, subtype);
                    } else {
                        final List<ItemStack> stacks = this.minimalInputStacks.get(i);
                        crafting.setItem(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
                    }
                }
                if (this.recipe.matches(crafting, null)) {
                    outputConsumer.accept(subtype, this.recipe.assemble(crafting, null));
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    public Input getInputsForOutput(final ItemStack output) {
        final RegularIngredient[] ingredients = this.recipe.getGenericIngredients();
        final List<List<ItemStack>> inputs = new ArrayList<>(9);
        final GenericIngredient<?, ?>[] ingredientMat = new GenericIngredient<?, ?>[9];
        final AuxiliaryIngredient<?>[] aux = this.recipe.getAuxiliaryIngredients();
        for (int i = 0, auxIngIdx = 0, auxIdx = 0; i < 9; i++) {
            final int x = i % 3;
            final int y = i / 3;
            final ImmutableList<ImmutableList<ItemStack>> ingInputs;
            GenericIngredient<?, ?> ingredient = null;
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
                    if (auxInputs.size() > 0) {
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
        final CraftingContainer crafting = new TransientCraftingContainer(new AbstractContainerMenu(null, 0) {
            @Override
            public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, this.getWidth(), this.getHeight());
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
            crafting.clearContent();
            for (int n = 0; n < this.minimalInputStacks.size(); n++) {
                final List<ItemStack> stacks = this.minimalInputStacks.get(n);
                crafting.setItem(n, i == n ? matched : stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(0));
            }
            if (this.recipe.matches(crafting, null)) {
                final List<List<ItemStack>> inputs = new ArrayList<>(this.allInputs.size());
                for (int n = 0; n < this.allInputs.size(); n++) {
                    final List<ItemStack> stacks = this.allInputs.get(n);
                    inputs.add(i == n ? Collections.singletonList(matched) : stacks);
                }
                return new Input(inputs, this.ingredientMatrix);
            }
        }
        return null;
    }

    public List<ItemStack> getOutput(final List<List<ItemStack>> inputs) {
        final CraftingContainer crafting = new TransientCraftingContainer(new AbstractContainerMenu(null, 0) {
            @Override
            public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, this.getWidth(), this.getHeight());
        int size = 1;
        for (final List<ItemStack> stack : inputs) {
            if (stack.size() > 0) {
                size = FLMth.lcm(stack.size(), size);
            }
        }
        final List<ItemStack> outputs = new ArrayList<>(size);
        for (int n = 0; n < size; n++) {
            for (int i = 0; i < inputs.size(); i++) {
                final List<ItemStack> stacks = inputs.get(i);
                crafting.setItem(i, stacks.isEmpty() ? ItemStack.EMPTY : stacks.get(n % stacks.size()));
            }
            if (this.recipe.matches(crafting, null)) {
                outputs.add(this.recipe.assemble(crafting, null));
            } else {
                LogManager.getLogger().debug("No recipe match for {} using inputs {}",
                    ForgeRegistries.ITEMS.getKey(this.recipe.getOutput().getItem()),
                    IntStream.range(0, crafting.getWidth() * crafting.getHeight())
                        .mapToObj(crafting::getItem)
                        .map(s -> Objects.toString(ForgeRegistries.ITEMS.getKey(s.getItem())))
                        .collect(Collectors.joining(", "))
                );
            }
        }
        return outputs;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        focuses.getFocuses(VanillaTypes.ITEM_STACK).flatMap(focus -> {
            ItemStack stack = focus.getTypedValue().getIngredient();
            Input input = null;
            if (focus.getRole() == RecipeIngredientRole.INPUT) {
                input = this.getInputsForIngredient(stack);
            } else if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
                input = this.getInputsForOutput(stack);
            }
            return Stream.ofNullable(input);
        }).findFirst().ifPresentOrElse(input -> {
            craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, this.getOutput(input.inputs));
            List<IRecipeSlotBuilder> slots = craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, input.inputs, this.getWidth(), this.getHeight());
            for (int i = 0; i < 9; i++) {
                GenericIngredient<?, ?> ingredient = input.ingredients[i];
                IRecipeSlotBuilder slot = slots.get(i);
                slot.addTooltipCallback((recipeSlotView, tooltip) -> {
                    if (recipeSlotView.getRole() == RecipeIngredientRole.INPUT) {
                        ingredient.addTooltip(tooltip);
                    }
                });
            }
        }, () -> {
            craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, this.outputs);
            craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, this.allInputs, this.getWidth(), this.getHeight());
        });
    }

    private static final class Input {
        List<List<ItemStack>> inputs;

        GenericIngredient<?, ?>[] ingredients;

        private Input(final List<List<ItemStack>> inputs, final GenericIngredient<?, ?>[] ingredients) {
            this.inputs = inputs;
            this.ingredients = ingredients;
        }
    }
}
