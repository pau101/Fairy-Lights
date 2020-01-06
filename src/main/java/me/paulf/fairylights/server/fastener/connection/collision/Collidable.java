package me.paulf.fairylights.server.fastener.connection.collision;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public interface Collidable {
    @Nullable
    Intersection intersect(Vec3d origin, Vec3d end);
}
