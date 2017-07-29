package com.pau101.fairylights.util;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.pau101.fairylights.FairyLights;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class Utils {
	private Utils() {}

	private static final Converter<String, String> UNDRSCR_TO_CML = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

	// For silencing constant conditions for @ObjectHolder field references
	// Requires disabling runtime assertions for not-null-annotated methods and parameters.
	@SuppressWarnings("ConstantConditions")
	@Nonnull
	public static <T> T nil() {
		return null;
	}

	public static <I extends Item> I name(I item, String registryName) {
		name(item, registryName, item::setUnlocalizedName);
		return item;
	}

	public static <B extends Block> B name(B block, String registryName) {
		name(block, registryName, block::setUnlocalizedName);
		return block;
	}

	private static <T extends IForgeRegistryEntry.Impl<T>> T name(T entry, String registryName, Consumer<String> unlocalizedNameSetter) {
		entry.setRegistryName(registryName);
		unlocalizedNameSetter.accept(underScoreToCamel(registryName));
		return entry;
	}

	public static String underScoreToCamel(String value) {
		return UNDRSCR_TO_CML.convert(value);
	}

	public static Field getFieldOfType(Class<?> clazz, Class<?> type) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType().equals(type)) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new ReflectionHelper.UnableToFindFieldException(null, null);
	}

	public static <E extends Enum<E>> E getEnumValue(Class<E> clazz, int ordinal) {
		E[] values = Objects.requireNonNull(clazz, "clazz").getEnumConstants();
		return values[ordinal < 0 || ordinal >= values.length ? 0 : ordinal];
	}

	public static String formatColored(EnumDyeColor color, String name) {
		return I18n.translateToLocalFormatted("format.colored", I18n.translateToLocal("color." + color.getUnlocalizedName() + ".name"), name);
	}

	public static String formatRecipeTooltip(String key) {
		return formatRecipeTooltipValue(I18n.translateToLocal(key));
	}

	private static String formatRecipeTooltipValue(String value) {
		return I18n.translateToLocalFormatted("recipe.ingredient.tooltip", value);
	}

	public static String getEntityName(Entity e) {
		if (e.hasCustomName()) {
			return e.getCustomNameTag();
		}
		String s = EntityList.getEntityString(e);
		if (s == null) {
			s = "generic";
		}
		return I18n.translateToLocal("entity." + FairyLights.ID + "." + s + ".name");
	}
}
