package me.paulf.fairylights.util.crafting.ingredient;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class InertBasicAuxiliaryIngredient extends BasicAuxiliaryIngredient<Void> {
    public InertBasicAuxiliaryIngredient(final Ingredient ingredient, final boolean isRequired, final int limit) {
        super(ingredient, isRequired, limit);
    }

    public InertBasicAuxiliaryIngredient(final Ingredient ingredient) {
        super(ingredient, true, Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public final Void accumulator() {
        return null;
    }

    @Override
    public final void consume(final Void v, final ItemStack ingredient) {}

    @Override
    public final boolean finish(final Void v, final CompoundTag stack) {
        return false;
    }
}
