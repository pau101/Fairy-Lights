package me.paulf.fairylights.server.integration.jei;

import mezz.jei.api.ingredients.subtypes.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants.*;

public final class TinselSubtypeInterpreter implements ISubtypeInterpreter {
    @Override
    public String apply(final ItemStack stack) {
        final CompoundNBT compound = stack.getTag();
        if (compound != null && compound.contains("color", NBT.TAG_BYTE)) {
            return DyeColor.byId(compound.getByte("color")).getName();
        }
        return NONE;
    }
}
