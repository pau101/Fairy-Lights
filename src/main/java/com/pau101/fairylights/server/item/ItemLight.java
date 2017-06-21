package com.pau101.fairylights.server.item;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.util.Utils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

public final class ItemLight extends Item {
	public static final int COLOR_COUNT = 16;

	public ItemLight() {
		setCreativeTab(FairyLights.fairyLightsTab);
		setHasSubtypes(true);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String localizedLightName = I18n.translateToLocal(super.getUnlocalizedName(stack) + '.' + getLightVariant(stack.getMetadata()).getUnlocalizedName() + ".name");
		return Utils.formatColored(getLightColor(stack.getMetadata()), localizedLightName);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		LightVariant[] variants = LightVariant.values();
		for (int variant = 0; variant < variants.length; variant++) {
			if (variants[variant] == LightVariant.LUXO_BALL) {
				continue;
			}
			for (int color = 0; color < COLOR_COUNT; color++) {
				subItems.add(new ItemStack(this, 1, variant * COLOR_COUNT + color));
			}
		}
	}

	public static final int getLightMeta(LightVariant variant, EnumDyeColor color) {
		return getLightMeta(variant, color.ordinal());
	}

	public static final int getLightMeta(LightVariant variant, int color) {
		return variant.ordinal() * COLOR_COUNT + color;
	}

	public static final EnumDyeColor getLightColor(int meta) {
		return EnumDyeColor.byDyeDamage(getLightColorOrdinal(meta));
	}

	public static final LightVariant getLightVariant(int meta) {
		return LightVariant.getLightVariant(getLightVariantOrdinal(meta));
	}

	public static final byte getLightColorOrdinal(int meta) {
		return (byte) (meta % COLOR_COUNT); 
	}

	public static final int getLightVariantOrdinal(int meta) {
		return meta / COLOR_COUNT; 
	}

	public static final int getColorValue(EnumDyeColor color) {
		if (color == EnumDyeColor.BLACK) {
			return 0x323232;
		}
		if (color == EnumDyeColor.GRAY) {
			return 0x606060;
		}
		return color.getColorValue();
	}
}
