package me.paulf.fairylights.server.fastener.connection.collision;

import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConnectionCollision implements Collidable {
    private List<Collidable> collision = Collections.emptyList();

    public void update(final Connection connection, final Vec3d origin) {
        connection.addCollision(this.collision = new ArrayList<>(), origin);
    }

    @Nullable
    @Override
    public Intersection intersect(final Vec3d origin, final Vec3d end) {
        Intersection result = null;
        double distance = Double.MAX_VALUE;
        for (final Collidable collidable : this.collision) {
            final Intersection r = collidable.intersect(origin, end);
            if (r != null) {
                final double d = r.getResult().distanceTo(origin);
                if (d < distance) {
                    result = r;
                    distance = d;
                }
            }
        }
        return result;
    }
}
