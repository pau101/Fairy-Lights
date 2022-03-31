package me.paulf.fairylights.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Utils {
    private Utils() {}

    public static <E extends Enum<E>> E getEnumValue(final Class<E> clazz, final int ordinal) {
        final E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
        return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
    }

    public static ITextComponent formatRecipeTooltip(final String key) {
        return formatRecipeTooltipValue(I18n.func_135052_a(key));
    }

    private static ITextComponent formatRecipeTooltipValue(final String value) {
        return new TranslationTextComponent("recipe.ingredient.tooltip", value);
    }

    public static boolean impliesNbt(@Nullable INBT antecedent, @Nullable INBT consequent) {
        if (antecedent == consequent) return true;
        if ((antecedent == null) != (consequent == null)) return false;
        if (!antecedent.getClass().equals(consequent.getClass())) return false;
        if (antecedent instanceof CompoundNBT) {
            for (String key : ((CompoundNBT) antecedent).func_150296_c()) {
                if (!impliesNbt(((CompoundNBT) antecedent).func_74781_a(key), ((CompoundNBT) consequent).func_74781_a(key))) {
                    return false;
                }
            }
            return true;
        }
        return antecedent.equals(consequent);
    }
}
