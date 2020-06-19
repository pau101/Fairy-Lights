package me.paulf.fairylights.server.feature.light;

import me.paulf.fairylights.server.feature.light.Light;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface LightBehavior {
    default void power(final boolean powered) {
        this.power(powered, true);
    }

    void power(final boolean powered, final boolean now);

    void tick(final World world, final Vec3d origin, final Light<?> light);

    default void animateTick(final World world, final Vec3d origin, final Light<?> light) {}
}
