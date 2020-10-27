package me.paulf.fairylights.server.collision;

import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface Collidable {
    @Nullable
    Intersection intersect(final Vector3d origin, final Vector3d end);

    static Collidable empty() {
        return (o, e) -> null;
    }
}
