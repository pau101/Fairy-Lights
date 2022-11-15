package me.paulf.fairylights.server.integration.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class ColorSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(final ItemStack stack, final UidContext context) {
        final CompoundTag compound = stack.getTag();
        if (compound != null && compound.contains("color", Tag.TAG_INT)) {
            return String.format("%06x", compound.getInt("color"));
        }
        return NONE;
    }
}
