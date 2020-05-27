package me.paulf.fairylights.server.item;

import me.paulf.fairylights.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public final class LightItem extends BlockItem {
    public LightItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        return Utils.formatColored(LightItem.getLightColor(stack), super.getDisplayName(stack));
    }

    @Override
    public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (final DyeColor dye : DyeColor.values()) {
                final ItemStack stack = new ItemStack(this);
                LightItem.setLightColor(stack, dye);
                items.add(stack);
            }
        }
    }

    public static DyeColor getLightColor(final ItemStack stack) {
        return stack.hasTag() ? DyeColor.byId(stack.getTag().getByte("color")) : DyeColor.YELLOW;
    }

    public static void setLightColor(final ItemStack stack, final DyeColor color) {
        stack.getOrCreateTag().putByte("color", (byte) color.getId());
    }

    public static int getColorValue(final DyeColor color) {
        if (color == DyeColor.BLACK) {
            return 0x323232;
        }
        if (color == DyeColor.GRAY) {
            return 0x606060;
        }
        final float[] rgb = color.getColorComponentValues();
        return (int) (rgb[0] * 255) << 16 | (int) (rgb[1] * 0xFF) << 8 | (int) (rgb[2] * 255);
    }
}
