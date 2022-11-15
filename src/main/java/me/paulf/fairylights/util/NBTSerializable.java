package me.paulf.fairylights.util;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    CompoundTag serialize();

    void deserialize(CompoundTag compound);
}
