package com.pau101.fairylights.util;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTSerializable {
	NBTTagCompound serialize();

	void deserialize(NBTTagCompound compound);
}
