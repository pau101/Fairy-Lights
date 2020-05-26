package me.paulf.fairylights.server.item;

import me.paulf.fairylights.util.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;

public class PennantItem extends Item {
    public PennantItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        return Utils.formatColored(LightItem.getLightColor(stack), super.getDisplayName(stack));
    }

    @Override
    public void fillItemGroup(final ItemGroup tab, final NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
            for (final DyeColor dye : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                LightItem.setLightColor(stack, dye);
                subItems.add(stack);
            }
        }
    }
}
