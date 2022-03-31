package me.paulf.fairylights.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;

import java.util.stream.Collectors;

public final class OreDictUtils {
    private OreDictUtils() {}

    public static boolean isDye(final ItemStack stack) {
        if (!stack.func_190926_b()) {
            if (stack.func_77973_b() instanceof DyeItem) {
                return true;
            }
            return stack.func_77973_b().func_206844_a(Tags.Items.DYES);
        }
        return false;
    }

    public static DyeColor getDyeColor(final ItemStack stack) {
        if (!stack.func_190926_b()) {
            if (stack.func_77973_b() instanceof DyeItem) {
                return ((DyeItem) stack.func_77973_b()).func_195962_g();
            }
            for (final Dye dye : Dye.values()) {
                if (stack.func_77973_b().func_206844_a(dye.getName())) {
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
            bob.putAll(dye.getColor(), dye.getName().func_230236_b_().stream().map(ItemStack::new).collect(Collectors.toList()));
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

        private final ITag<Item> name;

        private final DyeColor color;

        Dye(final ITag<Item> name, final DyeColor color) {
            this.name = name;
            this.color = color;
        }

        private ITag<Item> getName() {
            return this.name;
        }

        private DyeColor getColor() {
            return this.color;
        }
    }
}
