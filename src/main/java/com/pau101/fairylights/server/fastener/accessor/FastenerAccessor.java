package com.pau101.fairylights.server.fastener.accessor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerType;

public interface FastenerAccessor {
	Fastener<?> get(World world);

	boolean isLoaded(World world);

	boolean exists(World world);

	default void update(World world, BlockPos pos) {}

	FastenerType getType();

	NBTTagCompound serialize();

	void deserialize(NBTTagCompound compound);
}
