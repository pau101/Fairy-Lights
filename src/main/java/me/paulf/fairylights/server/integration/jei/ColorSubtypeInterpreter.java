package me.paulf.fairylights.server.integration.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

public final class ColorSubtypeInterpreter implements ISubtypeInterpreter {
    @Override
    public String apply(final ItemStack stack) {
        final CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("color", NBT.TAG_BYTE)) {
            return DyeColor.byId(compound.getByte("color")).getName();
        }
        return NONE;
    }
}
