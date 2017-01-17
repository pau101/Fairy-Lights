package com.pau101.fairylights.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class DyeOreDictUtils {
	private DyeOreDictUtils() {}

	private static final String[] DYE_NAMES = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };

	@Nullable
	private static List<ItemStack>[] dyeItemStacks;

	@Nullable
	private static List<ItemStack> allDyeItemStacks;

	public static boolean isDye(ItemStack stack) {
		if (!stack.func_190926_b()) {
			if (stack.getItem() == Items.DYE) {
				return true;
			}
			initDyeItemStacks();
			for (ItemStack dye : allDyeItemStacks) {
				if (OreDictionary.itemMatches(dye, stack, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getDyeMetadata(ItemStack stack) {
		if (!stack.func_190926_b()) {
			if (stack.getItem() == Items.DYE) {
				return stack.getMetadata();
			}
			initDyeItemStacks();
			for (int i = 0; i < DYE_NAMES.length; i++) {
				List<ItemStack> dyes = dyeItemStacks[i];
				for (ItemStack dye : dyes) {
					if (OreDictionary.itemMatches(dye, stack, false)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	public static ImmutableList<ItemStack> getDyes(EnumDyeColor color) {
		initDyeItemStacks();
		return Utils.copyItemStacks(dyeItemStacks[color.getDyeDamage()]);
	}

	public static ImmutableList<ItemStack> getAllDyes() {
		initDyeItemStacks();
		return Utils.copyItemStacks(allDyeItemStacks);
	}

	private static void initDyeItemStacks() {
		if (dyeItemStacks == null) {
			dyeItemStacks = new List[DYE_NAMES.length];
			allDyeItemStacks = new ArrayList<>();
			for (int i = 0; i < DYE_NAMES.length; i++) {
				dyeItemStacks[i] = new ArrayList<>(OreDictionary.getOres(DYE_NAMES[i]));
				allDyeItemStacks.addAll(dyeItemStacks[i]);
			}
		}
	}
}
