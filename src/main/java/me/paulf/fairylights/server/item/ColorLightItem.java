package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        return DyeableItem.getDisplayName(stack, super.getDisplayName(stack));
    }

    @Override
    public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
