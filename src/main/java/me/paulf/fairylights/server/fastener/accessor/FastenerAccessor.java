package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FastenerAccessor {
	Fastener<?> get(World world);

	boolean isLoaded(World world);

	boolean exists(World world);

	default void update(World world, BlockPos pos) {}

	FastenerType getType();

	CompoundNBT serialize();

	void deserialize(CompoundNBT compound);
}
