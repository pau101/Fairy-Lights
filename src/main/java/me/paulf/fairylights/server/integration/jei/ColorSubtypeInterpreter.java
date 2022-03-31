package me.paulf.fairylights.server.integration.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

public final class ColorSubtypeInterpreter implements ISubtypeInterpreter {
    @Override
    public String apply(final ItemStack stack) {
        final CompoundNBT compound = stack.func_77978_p();
        if (compound != null && compound.func_150297_b("color", NBT.TAG_INT)) {
            return String.format("%06x", compound.func_74762_e("color"));
        }
        return NONE;
    }
}
