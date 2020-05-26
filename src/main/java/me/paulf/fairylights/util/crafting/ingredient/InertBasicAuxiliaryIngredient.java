package me.paulf.fairylights.util.crafting.ingredient;

import net.minecraft.block.*;
import net.minecraft.item.*;

import javax.annotation.*;

public class InertBasicAuxiliaryIngredient extends BasicAuxiliaryIngredient<Void> {
    public InertBasicAuxiliaryIngredient(final Item item, final boolean isRequired, final int limit) {
        super(item, isRequired, limit);
    }

    public InertBasicAuxiliaryIngredient(final Block block, final boolean isRequired, final int limit) {
        super(block, isRequired, limit);
    }

    public InertBasicAuxiliaryIngredient(final ItemStack stack, final boolean isRequired, final int limit) {
        super(stack, isRequired, limit);
    }

    public InertBasicAuxiliaryIngredient(final ItemStack stack) {
        super(stack, true, Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public final Void accumulator() {
        return null;
    }

    @Override
    public final void consume(final Void v, final ItemStack ingredient) {}

    @Override
    public final boolean finish(final Void v, final ItemStack stack) {
        return false;
    }
}
