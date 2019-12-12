package me.paulf.fairylights.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

public final class Utils {
	private Utils() {}

	public static <E extends Enum<E>> E getEnumValue(Class<E> clazz, int ordinal) {
		E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
		return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
	}

	public static ITextComponent formatColored(DyeColor color, ITextComponent name) {
		return new TranslationTextComponent("format.colored", new TranslationTextComponent("color." + color.getTranslationKey() + ".name"), name);
	}

	public static String formatRecipeTooltip(String key) {
		return formatRecipeTooltipValue(I18n.format(key));
	}

	private static String formatRecipeTooltipValue(String value) {
		return I18n.format("recipe.ingredient.tooltip", value);
	}
}
