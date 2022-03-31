package me.paulf.fairylights.server.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class PennantItem extends Item {
    public PennantItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ITextComponent func_200295_i(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.func_200295_i(stack));
    }

    @Override
    public void func_150895_a(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.func_194125_a(tab)) {
            for (final DyeColor dye : DyeColor.values()) {
                subItems.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
