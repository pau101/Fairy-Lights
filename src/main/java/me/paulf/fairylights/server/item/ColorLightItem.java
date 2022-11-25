package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Item.Properties properties) {
        super(light, properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("colors", Tag.TAG_LIST)) {
            return Component.translatable("format.fairylights.color_changing", super.getName(stack));
        }
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(DyeableItem.setColor(new ItemStack(this), dye));
            }
        }
    }
}
