package me.paulf.fairylights.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

public final class Utils {
    private Utils() {}

    public static <E extends Enum<E>> E getEnumValue(final Class<E> clazz, final int ordinal) {
        final E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
        return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
    }

    public static ITextComponent formatRecipeTooltip(final String key) {
        return formatRecipeTooltipValue(I18n.format(key));
    }

    private static ITextComponent formatRecipeTooltipValue(final String value) {
        return new TranslationTextComponent("recipe.ingredient.tooltip", value);
    }
}
