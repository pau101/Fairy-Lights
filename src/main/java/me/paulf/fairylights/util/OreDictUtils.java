package me.paulf.fairylights.util;

import com.google.common.collect.*;
import net.minecraft.item.*;
import net.minecraft.tags.*;
import net.minecraftforge.common.*;

import java.util.stream.*;

public final class OreDictUtils {
    private OreDictUtils() {}

    public static boolean isDye(final ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof DyeItem) {
                return true;
            }
            return stack.getItem().isIn(Tags.Items.DYES);
        }
        return false;
    }

    public static DyeColor getDyeMetadata(final ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof DyeItem) {
                return ((DyeItem) stack.getItem()).getDyeColor();
            }
            for (final Dye dye : Dye.values()) {
                if (stack.getItem().isIn(dye.getName())) {
                    return dye.getColor();
                }
            }
        }
        return DyeColor.YELLOW;
    }

    public static ImmutableList<ItemStack> getDyes(final DyeColor color) {
        return getDyeItemStacks().get(color).asList();
    }

    public static ImmutableList<ItemStack> getAllDyes() {
        return getDyeItemStacks().values().asList();
    }

    private static ImmutableMultimap<DyeColor, ItemStack> getDyeItemStacks() {
        final ImmutableMultimap.Builder<DyeColor, ItemStack> bob = ImmutableMultimap.builder();
        for (final Dye dye : Dye.values()) {
            bob.putAll(dye.getColor(), dye.getName().getAllElements().stream().map(ItemStack::new).collect(Collectors.toList()));
        }
        return bob.build();
    }

    private enum Dye {
        WHITE(Tags.Items.DYES_WHITE, DyeColor.WHITE),
        ORANGE(Tags.Items.DYES_ORANGE, DyeColor.ORANGE),
        MAGENTA(Tags.Items.DYES_MAGENTA, DyeColor.MAGENTA),
        LIGHT_BLUE(Tags.Items.DYES_LIGHT_BLUE, DyeColor.LIGHT_BLUE),
        YELLOW(Tags.Items.DYES_YELLOW, DyeColor.YELLOW),
        LIME(Tags.Items.DYES_LIME, DyeColor.LIME),
        PINK(Tags.Items.DYES_PINK, DyeColor.PINK),
        GRAY(Tags.Items.DYES_GRAY, DyeColor.GRAY),
        LIGHT_GRAY(Tags.Items.DYES_LIGHT_GRAY, DyeColor.LIGHT_GRAY),
        CYAN(Tags.Items.DYES_CYAN, DyeColor.CYAN),
        PURPLE(Tags.Items.DYES_PURPLE, DyeColor.PURPLE),
        BLUE(Tags.Items.DYES_BLUE, DyeColor.BLUE),
        BROWN(Tags.Items.DYES_BROWN, DyeColor.BROWN),
        GREEN(Tags.Items.DYES_GREEN, DyeColor.GREEN),
        RED(Tags.Items.DYES_RED, DyeColor.RED),
        BLACK(Tags.Items.DYES_BLACK, DyeColor.BLACK);

        private final Tag<Item> name;

        private final DyeColor color;

        Dye(final Tag<Item> name, final DyeColor color) {
            this.name = name;
            this.color = color;
        }

        private Tag<Item> getName() {
            return this.name;
        }

        private DyeColor getColor() {
            return this.color;
        }
    }
}
