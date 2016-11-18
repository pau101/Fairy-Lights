package com.pau101.fairylights.server.fastener.connection.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.math.Vec3d;

import com.pau101.fairylights.server.fastener.connection.type.Connection;

public final class ConnectionCollision implements Collidable {
	private List<Collidable> collision = Collections.EMPTY_LIST;

	public void update(Connection connection, Vec3d origin) {
		connection.addCollision(collision = new ArrayList<>(), origin);
	}

	@Nullable
	@Override
	public Intersection intersect(Vec3d origin, Vec3d end) {
		Intersection result = null;
		double distance = Double.MAX_VALUE;
		for (Collidable collidable : collision) {
			Intersection r = collidable.intersect(origin, end);
			if (r != null) {
				double d = r.getResult().hitVec.distanceTo(origin);
				if (d < distance) {
					result = r;
					distance = d;
				}
			}
		}
		return result;
	}
}
