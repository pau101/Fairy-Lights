package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.Optional;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Properties properties) {
        super(light, properties);
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        return super.getDisplayName(stack);
    }

    @Override
    public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                items.add(setColor(new ItemStack(this), dye));
            }
        }
    }

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
