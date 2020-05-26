package me.paulf.fairylights.util;

import net.minecraft.nbt.*;

public interface NBTSerializable {
    CompoundNBT serialize();

    void deserialize(CompoundNBT compound);
}
