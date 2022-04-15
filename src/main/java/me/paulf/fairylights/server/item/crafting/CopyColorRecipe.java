package me.paulf.fairylights.server.item.crafting;

import me.paulf.fairylights.server.item.DyeableItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CopyColorRecipe extends CustomRecipe {
    public CopyColorRecipe(final ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(final CraftingContainer inv, final Level world) {
        int count = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && (!FLCraftingRecipes.DYEABLE.contains(stack.getItem()) || count++ >= 2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(final CraftingContainer inv) {
        ItemStack original = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (FLCraftingRecipes.DYEABLE.contains(stack.getItem())) {
                    if (original.isEmpty()) {
                        original = stack;
                    } else {
                        final ItemStack copy = stack.copy();
                        copy.setCount(1);
                        DyeableItem.setColor(copy, DyeableItem.getColor(original));
                        return copy;
                    }
                } else {
                    break;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer inv) {
        ItemStack original = ItemStack.EMPTY;
        final NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); i++) {
            final ItemStack stack = inv.getItem(i);
            if (stack.hasContainerItem()) {
                remaining.set(i, stack.getContainerItem());
            } else if (original.isEmpty() && !stack.isEmpty() && FLCraftingRecipes.DYEABLE.contains(stack.getItem())) {
                final ItemStack rem = stack.copy();
                rem.setCount(1);
                remaining.set(i, rem);
                original = stack;
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(final int width, final int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FLCraftingRecipes.COPY_COLOR.get();
    }
}
