package com.pau101.fairylights.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.pau101.fairylights.FairyLights;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public final class Utils {
	private Utils() {}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static MethodHandle getMethodHandle(String className, String name, Class<?>... parameterTypes) {
		try {
			return getMethodHandle(Class.forName(className, false, Utils.class.getClassLoader()), name, parameterTypes);
		} catch (ClassNotFoundException e) {
			throw new UnableToFindMethodException(new String[] { name }, e);
		}
	}

	public static MethodHandle getMethodHandle(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			Method method = clazz.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return LOOKUP.unreflect(method);
		} catch (Exception e) {
			throw new UnableToFindMethodException(new String[] { name }, e);
		}
	}

	public static Field getFieldOfType(Class<?> clazz, Class<?> type) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType().equals(type)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
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

	public static String formatRecipeTooltipValue(String value) {
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

	public static ImmutableList<ItemStack> copyItemStacks(List<ItemStack> list) {
		ImmutableList.Builder<ItemStack> copy = ImmutableList.builder();
		for (ItemStack stack : list) {
			copy.add(stack.copy());
		}
		return copy.build();
	}
}
