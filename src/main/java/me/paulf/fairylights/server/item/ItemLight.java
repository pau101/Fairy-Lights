package me.paulf.fairylights.server.item;

import me.paulf.fairylights.util.Utils;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public final class ItemLight extends Item {
	public ItemLight(Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return Utils.formatColored(ItemLight.getLightColor(stack), super.getDisplayName(stack));
	}

	@Override
	public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
		if (isInGroup(group)) {
			for (DyeColor dye : DyeColor.values()) {
				ItemStack stack = new ItemStack(this);
				stack.getOrCreateTag().putByte("color", (byte) dye.getId());
				items.add(stack);
			}
		}
	}

	public static DyeColor getLightColor(ItemStack stack) {
		return stack.hasTag() ? DyeColor.byId(stack.getTag().getByte("color")) : DyeColor.YELLOW;
	}

	public static int getColorValue(DyeColor color) {
		if (color == DyeColor.BLACK) {
			return 0x323232;
		}
		if (color == DyeColor.GRAY) {
			return 0x606060;
		}
		float[] rgb = color.getColorComponentValues();
		return (int) (rgb[0] * 255) << 16 | (int) (rgb[1] * 0xFF) << 8 | (int) (rgb[2] * 255);
	}
}
