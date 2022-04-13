package me.paulf.fairylights.server.collision;

import javax.annotation.Nullable;

import net.minecraft.world.phys.Vec3;

public interface Collidable {
    @Nullable
    Intersection intersect(final Vec3 origin, final Vec3 end);

    static Collidable empty() {
        return (o, e) -> null;
    }
}
