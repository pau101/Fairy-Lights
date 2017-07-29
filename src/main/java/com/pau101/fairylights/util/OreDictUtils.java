package com.pau101.fairylights.util;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictUtils {
	private OreDictUtils() {}

	@Nullable
	private static ImmutableMultimap<Integer, ItemStack> dyeItemStacks;

	public static boolean isDye(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.getItem() == Items.DYE) {
				return true;
			}
			for (ItemStack dye : getAllDyes()) {
				if (OreDictionary.itemMatches(dye, stack, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getDyeMetadata(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.getItem() == Items.DYE) {
				return stack.getMetadata();
			}
			getDyeItemStacks();
			for (Dye dye : Dye.values()) {
				for (ItemStack dyeStack : getDyeItemStacks().get(dye.getDamage())) {
					if (OreDictionary.itemMatches(dyeStack, stack, false)) {
						return dye.getDamage();
					}
				}
			}
		}
		return -1;
	}

	public static ImmutableList<ItemStack> getDyes(EnumDyeColor color) {
		return ImmutableList.copyOf(getDyeItemStacks().get(color.getDyeDamage()));
	}

	public static ImmutableList<ItemStack> getAllDyes() {
		return ImmutableList.copyOf(getDyeItemStacks().values());
	}

	public static boolean matches(ItemStack stack, String name) {
		return OreDictionary.containsMatch(false, OreDictionary.getOres(name), stack);
	}

	private static ImmutableMultimap<Integer, ItemStack> getDyeItemStacks() {
		if (dyeItemStacks == null) {
			ImmutableMultimap.Builder<Integer, ItemStack> bob = ImmutableMultimap.builder();
			for (Dye dye : Dye.values()) {
				bob.putAll(dye.getDamage(), OreDictionary.getOres(dye.getName()));
			}
			dyeItemStacks = bob.build();
		}
		return dyeItemStacks;
	}

	private enum Dye {
		BLACK("dyeBlack"),
		RED("dyeRed"),
		GREEN("dyeGreen"),
		BROWN("dyeBrown"),
		BLUE("dyeBlue"),
		PURPLE("dyePurple"),
		CYAN("dyeCyan"),
		LIGHT_GRAY("dyeLightGray"),
		GRAY("dyeGray"),
		PINK("dyePink"),
		LIME("dyeLime"),
		YELLOW("dyeYellow"),
		LIGHT_BLUE("dyeLightBlue"),
		MAGENTA("dyeMagenta"),
		ORANGE("dyeOrange"),
		WHITE("dyeWhite");

		private final String name;

		private final int damage;

		Dye(String name) {
			this.name = name;
			damage = ordinal();
		}

		private String getName() {
			return name;
		}

		private int getDamage() {
			return damage;
		}
	}
}
