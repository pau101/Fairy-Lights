package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public interface FastenerAccessor {
    default LazyOptional<Fastener<?>> get(final World world) {
        return this.get(world, true);
    }

    LazyOptional<Fastener<?>> get(final World world, final boolean load);

    boolean exists(World world);

    default void update(final World world, final BlockPos pos) {}

    FastenerType getType();

    CompoundNBT serialize();

    void deserialize(CompoundNBT compound);
}
