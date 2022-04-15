package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public final class TinselConnectionItem extends ConnectionItem {
    public TinselConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.TINSEL_GARLAND);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void fillItemCategory(final CreativeModeTab tab, final NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), color));
            }
        }
    }
}
