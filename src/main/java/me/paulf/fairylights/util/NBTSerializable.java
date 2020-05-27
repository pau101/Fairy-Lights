package me.paulf.fairylights.util;

import net.minecraft.nbt.CompoundNBT;

public interface NBTSerializable {
    CompoundNBT serialize();

    void deserialize(CompoundNBT compound);
}
