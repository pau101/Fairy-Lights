package me.paulf.fairylights.server.collision;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public interface Collidable {
    @Nullable
    Intersection intersect(final Vec3d origin, final Vec3d end);

    static Collidable empty() {
        return (o, e) -> null;
    }
}
