package me.paulf.fairylights.server.item;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PennantItem extends Item {
    public PennantItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }
}
