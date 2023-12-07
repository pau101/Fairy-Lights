package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class TinselConnectionItem extends ConnectionItem
{

    public TinselConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.TINSEL_GARLAND);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }
}
