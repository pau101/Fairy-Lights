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
    public ITextComponent getDisplayName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getDisplayName(stack));
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
            for (final DyeColor dye : DyeColor.values()) {
                subItems.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
