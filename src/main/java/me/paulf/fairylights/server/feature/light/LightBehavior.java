package me.paulf.fairylights.server.feature.light;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface LightBehavior {
    default void power(final boolean powered, final Light<?> light) {
        this.power(powered, true, light);
    }

    void power(final boolean powered, final boolean now, final Light<?> light);

    void tick(final Level world, final Vec3 origin, final Light<?> light);

    default void animateTick(final Level world, final Vec3 origin, final Light<?> light) {}
}
