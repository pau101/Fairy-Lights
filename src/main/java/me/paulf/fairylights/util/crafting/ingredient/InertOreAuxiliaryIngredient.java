package me.paulf.fairylights.util.crafting.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import javax.annotation.Nullable;

public class InertOreAuxiliaryIngredient extends OreAuxiliaryIngredient<Void> {
    public InertOreAuxiliaryIngredient(final Tag<Item> tag, final boolean isRequired, final int limit) {
        super(tag, isRequired, limit);
    }

    @Nullable
    @Override
    public final Void accumulator() {
        return null;
    }

    @Override
    public final void consume(final Void v, final ItemStack ingredient) {}

    @Override
    public final boolean finish(final Void v, final ItemStack output) {
        return false;
    }
}
