package me.paulf.fairylights.server.item.crafting;

import me.paulf.fairylights.server.item.DyeableItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CopyColorRecipe extends SpecialRecipe {
    public CopyColorRecipe(final ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean func_77569_a(final CraftingInventory inv, final World world) {
        int count = 0;
        for (int i = 0; i < inv.func_70302_i_(); i++) {
            final ItemStack stack = inv.func_70301_a(i);
            if (!stack.func_190926_b() && (!stack.func_77973_b().func_206844_a(FLCraftingRecipes.DYEABLE) || count++ >= 2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack func_77572_b(final CraftingInventory inv) {
        ItemStack original = ItemStack.field_190927_a;
        for (int i = 0; i < inv.func_70302_i_(); i++) {
            final ItemStack stack = inv.func_70301_a(i);
            if (!stack.func_190926_b()) {
                if (stack.func_77973_b().func_206844_a(FLCraftingRecipes.DYEABLE)) {
                    if (original.func_190926_b()) {
                        original = stack;
                    } else {
                        final ItemStack copy = stack.func_77946_l();
                        copy.func_190920_e(1);
                        DyeableItem.setColor(copy, DyeableItem.getColor(original));
                        return copy;
                    }
                } else {
                    break;
                }
            }
        }
        return ItemStack.field_190927_a;
    }

    @Override
    public NonNullList<ItemStack> func_179532_b(final CraftingInventory inv) {
        ItemStack original = ItemStack.field_190927_a;
        final NonNullList<ItemStack> remaining = NonNullList.func_191197_a(inv.func_70302_i_(), ItemStack.field_190927_a);
        for (int i = 0; i < remaining.size(); i++) {
            final ItemStack stack = inv.func_70301_a(i);
            if (stack.hasContainerItem()) {
                remaining.set(i, stack.getContainerItem());
            } else if (original.func_190926_b() && !stack.func_190926_b() && stack.func_77973_b().func_206844_a(FLCraftingRecipes.DYEABLE)) {
                final ItemStack rem = stack.func_77946_l();
                rem.func_190920_e(1);
                remaining.set(i, rem);
                original = stack;
            }
        }
        return remaining;
    }

    @Override
    public boolean func_194133_a(final int width, final int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> func_199559_b() {
        return FLCraftingRecipes.COPY_COLOR.get();
    }
}
