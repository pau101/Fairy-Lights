package me.paulf.fairylights.util;

import net.minecraft.client.resources.*;
import net.minecraft.item.*;
import net.minecraft.util.text.*;

import java.util.*;

public final class Utils {
    private Utils() {}

    public static <E extends Enum<E>> E getEnumValue(final Class<E> clazz, final int ordinal) {
        final E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
        return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
    }

    public static ITextComponent formatColored(final DyeColor color, final ITextComponent name) {
        return new TranslationTextComponent("format.colored", new TranslationTextComponent("color." + color.getTranslationKey() + ".name"), name);
    }

    public static String formatRecipeTooltip(final String key) {
        return formatRecipeTooltipValue(I18n.format(key));
    }

    private static String formatRecipeTooltipValue(final String value) {
        return I18n.format("recipe.ingredient.tooltip", value);
    }
}
