package me.paulf.fairylights.util;

import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Utils {
    private Utils() {}

    public static <E extends Enum<E>> E getEnumValue(final Class<E> clazz, final int ordinal) {
        final E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
        return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
    }

    public static Component formatRecipeTooltip(final String key) {
        return formatRecipeTooltipValue(Language.getInstance().getOrDefault(key));
    }

    private static Component formatRecipeTooltipValue(final String value) {
        return Component.translatable("recipe.ingredient.tooltip", value);
    }

    public static boolean impliesNbt(@Nullable Tag antecedent, @Nullable Tag consequent) {
        if (antecedent == consequent) return true;
        if ((antecedent == null) != (consequent == null)) return false;
        if (!antecedent.getClass().equals(consequent.getClass())) return false;
        if (antecedent instanceof CompoundTag) {
            for (String key : ((CompoundTag) antecedent).getAllKeys()) {
                if (!impliesNbt(((CompoundTag) antecedent).get(key), ((CompoundTag) consequent).get(key))) {
                    return false;
                }
            }
            return true;
        }
        return antecedent.equals(consequent);
    }
}
