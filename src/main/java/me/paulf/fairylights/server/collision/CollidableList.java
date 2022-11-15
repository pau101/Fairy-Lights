package me.paulf.fairylights.server.collision;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class CollidableList implements Collidable {
    private final ImmutableList<Collidable> collision;

    private CollidableList(final Builder builder) {
        this.collision = builder.collision.build();
    }

    @Nullable
    @Override
    public Intersection intersect(final Vec3 origin, final Vec3 end) {
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

    public static class Builder {
        final ImmutableList.Builder<Collidable> collision = new ImmutableList.Builder<>();

        public Builder add(final Collidable collidable) {
            this.collision.add(collidable);
            return this;
        }

        public CollidableList build() {
            return new CollidableList(this);
        }
    }
}
