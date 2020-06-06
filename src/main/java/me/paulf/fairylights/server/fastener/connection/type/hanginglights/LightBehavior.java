package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface LightBehavior {
    void power(final boolean powered);

    void tick(final World world, final Vec3d origin, final Light<?> light);

    default void animateTick(final World world, final Vec3d origin, final Light<?> light) {}
}
