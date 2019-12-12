package com.pau101.fairylights.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;

import java.util.stream.Collectors;

public final class OreDictUtils {
	private OreDictUtils() {}

	public static boolean isDye(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof DyeItem) {
				return true;
			}
			return stack.getItem().isIn(Tags.Items.DYES);
		}
		return false;
	}

	public static int getDyeMetadata(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof DyeItem) {
				return ((DyeItem) stack.getItem()).getDyeColor().getId();
			}
			for (Dye dye : Dye.values()) {
				if (stack.getItem().isIn(dye.getName())) {
					return dye.getId();
				}
			}
		}
		return -1;
	}

	public static ImmutableList<ItemStack> getDyes(DyeColor color) {
		return getDyeItemStacks().get(color.getId()).asList();
	}

	public static ImmutableList<ItemStack> getAllDyes() {
		return getDyeItemStacks().values().asList();
	}

	private static ImmutableMultimap<Integer, ItemStack> getDyeItemStacks() {
		ImmutableMultimap.Builder<Integer, ItemStack> bob = ImmutableMultimap.builder();
		for (Dye dye : Dye.values()) {
			bob.putAll(dye.getId(), dye.getName().getAllElements().stream().map(ItemStack::new).collect(Collectors.toList()));
		}
		return bob.build();
	}

	private enum Dye {
		WHITE(Tags.Items.DYES_WHITE),
		ORANGE(Tags.Items.DYES_ORANGE),
		MAGENTA(Tags.Items.DYES_MAGENTA),
		LIGHT_BLUE(Tags.Items.DYES_LIGHT_BLUE),
		YELLOW(Tags.Items.DYES_YELLOW),
		LIME(Tags.Items.DYES_LIME),
		PINK(Tags.Items.DYES_PINK),
		GRAY(Tags.Items.DYES_GRAY),
		LIGHT_GRAY(Tags.Items.DYES_LIGHT_GRAY),
		CYAN(Tags.Items.DYES_CYAN),
		PURPLE(Tags.Items.DYES_PURPLE),
		BLUE(Tags.Items.DYES_BLUE),
		BROWN(Tags.Items.DYES_BROWN),
		GREEN(Tags.Items.DYES_GRAY),
		RED(Tags.Items.DYES_RED),
		BLACK(Tags.Items.DYES_BLACK);

		private final Tag<Item> name;

		private final int id;

		Dye(Tag<Item> name) {
			this.name = name;
			id = ordinal();
		}

		private Tag<Item> getName() {
			return name;
		}

		private int getId() {
			return id;
		}
	}
}
