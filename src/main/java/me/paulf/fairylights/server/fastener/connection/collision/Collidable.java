package me.paulf.fairylights.server.fastener.connection.collision;

import javax.annotation.Nullable;

import net.minecraft.util.math.Vec3d;

public interface Collidable {
	@Nullable
	Intersection intersect(Vec3d origin, Vec3d end);
}
