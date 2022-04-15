package me.paulf.fairylights.server.item;

import java.util.Arrays;
import java.util.Optional;

import me.paulf.fairylights.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public final class DyeableItem {
    private DyeableItem() {}

    public static BaseComponent getColorName(final int color) {
        final int r = color >> 16 & 0xFF;
        final int g = color >> 8 & 0xFF;
        final int b = color & 0xFF;
        DyeColor closest = DyeColor.WHITE;
        int closestDist = Integer.MAX_VALUE;
        for (final DyeColor dye : DyeColor.values()) {
            final int dyeColor = getColor(dye);
            if (dyeColor == color) {
                closest = dye;
                closestDist = 0;
                break;
            }
            final int dr = dyeColor >> 16 & 0xFF;
            final int dg = dyeColor >> 8 & 0xFF;
            final int db = dyeColor & 0xFF;
            final int dist = (dr - r) * (dr - r) + (dg - g) * (dg - g) + (db - b) * (db - b);
            if (dist < closestDist) {
                closest = dye;
                closestDist = dist;
            }
        }
        final BaseComponent colorName = new TranslatableComponent("color.fairylights." + closest.getName());
        return closestDist == 0 ? colorName : new TranslatableComponent("format.fairylights.dyed_colored", colorName);
    }

    public static BaseComponent getDisplayName(final ItemStack stack, final Component name) {
        return new TranslatableComponent("format.fairylights.colored", getColorName(getColor(stack)), name);
    }

    public static int getColor(final DyeColor color) {
        if (color == DyeColor.BLACK) {
            return 0x323232;
        }
        if (color == DyeColor.GRAY) {
            return 0x606060;
        }
        return Utils.convertDyeToHex(color);
    }

    public static Optional<DyeColor> getDyeColor(final ItemStack stack) {
        final int color = getColor(stack);
        return Arrays.stream(DyeColor.values()).filter(dye -> getColor(dye) == color).findFirst();
    }

    public static ItemStack setColor(final ItemStack stack, final DyeColor dye) {
        return setColor(stack, getColor(dye));
    }

    public static ItemStack setColor(final ItemStack stack, final int color) {
        setColor(stack.getOrCreateTag(), color);
        return stack;
    }

    public static CompoundTag setColor(final CompoundTag tag, final DyeColor dye) {
        return setColor(tag, getColor(dye));
    }

    public static CompoundTag setColor(final CompoundTag tag, final int color) {
        tag.putInt("color", color);
        return tag;
    }

    public static int getColor(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null ? getColor(tag) : 0xFFFFFF;
    }

    public static int getColor(final CompoundTag tag) {
        return tag.contains("color", CompoundTag.TAG_INT) ? tag.getInt("color") : 0xFFFFFF;
    }
}
