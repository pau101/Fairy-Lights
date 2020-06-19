package me.paulf.fairylights.server.item;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.Optional;

public final class DyeableItem {
    private DyeableItem() {}

    public static int getColor(final DyeColor color) {
        if (color == DyeColor.BLACK) {
            return 0x323232;
        }
        if (color == DyeColor.GRAY) {
            return 0x606060;
        }
        return color.getColorValue();
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

    public static CompoundNBT setColor(final CompoundNBT tag, final DyeColor dye) {
        return setColor(tag, getColor(dye));
    }

    public static CompoundNBT setColor(final CompoundNBT tag, final int color) {
        tag.putInt("color", color);
        return tag;
    }

    public static int getColor(final ItemStack stack) {
        final CompoundNBT tag = stack.getTag();
        return tag != null ? getColor(tag) : 0xFFFFFF;
    }

    public static int getColor(final CompoundNBT tag) {
        return tag.contains("color", Constants.NBT.TAG_INT) ? tag.getInt("color") : 0xFFFFFF;
    }
}
