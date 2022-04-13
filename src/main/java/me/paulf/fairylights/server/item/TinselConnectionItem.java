package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.connection.ConnectionTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;

public final class TinselConnectionItem extends ConnectionItem {
    public TinselConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.TINSEL_GARLAND);
    }

    @Override
    public ITextComponent func_200295_i(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.func_200295_i(stack));
    }

    @Override
    public void func_150895_a(final ItemGroup tab, final NonNullList<ItemStack> items) {
        if (this.func_194125_a(tab)) {
            for (final DyeColor color : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), color));
            }
        }
    }
}
