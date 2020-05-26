package me.paulf.fairylights.server.fastener.connection.collision;

import net.minecraft.util.math.*;

import javax.annotation.*;

public interface Collidable {
    @Nullable
    Intersection intersect(final Vec3d origin, final Vec3d end);

    static Collidable empty() {
        return (o, e) -> null;
    }
}
