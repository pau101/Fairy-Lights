package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.fastener.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public interface FastenerAccessor {
    Fastener<?> get(World world);

    boolean isLoaded(World world);

    boolean exists(World world);

    default void update(final World world, final BlockPos pos) {}

    FastenerType getType();

    CompoundNBT serialize();

    void deserialize(CompoundNBT compound);
}
