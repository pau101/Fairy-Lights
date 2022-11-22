package me.paulf.fairylights.util;

import me.paulf.fairylights.server.connection.Connection;
import net.minecraft.world.phys.Vec3;

import java.util.NoSuchElementException;

public final class Catenary implements Curve {
    private static final int MIN_VERTEX_COUNT = 8;

    private final int count;

    private final float yaw;

    private final float dx;

    private final float dz;

    private final float[] x;

    private final float[] y;

    private final float length;

    private Catenary(final int count, final float yaw, final float dx, final float dz, final float[] x, final float[] y, final float length) {
        this.count = count;
        this.yaw = yaw;
        this.dx = dx;
        this.dz = dz;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public float getX() {
        return this.x[this.count - 1] * this.dx;
    }


    @Override
    public float getY() {
        return this.y[this.count - 1];
    }


    @Override
    public float getZ() {
        return this.x[this.count - 1] * this.dz;
    }

    @Override
    public float getX(final int i) {
        return this.x[i] * this.dx;
    }

    @Override
    public float getX(final int i, final float t) {
        return net.minecraft.util.Mth.lerp(t, this.x[i], this.x[i + 1]) * this.dx;
    }

    @Override
    public float getY(final int i) {
        return this.y[i];
    }

    @Override
    public float getY(final int i, final float t) {
        return net.minecraft.util.Mth.lerp(t, this.y[i], this.y[i + 1]);
    }

    @Override
    public float getZ(final int i) {
        return this.x[i] * this.dz;
    }

    @Override
    public float getZ(final int i, final float t) {
        return net.minecraft.util.Mth.lerp(t, this.x[i], this.x[i + 1]) * this.dz;
    }

    @Override
    public float getDx(final int i) {
        return (this.x[i + 1] - this.x[i]) * this.dx;
    }

    @Override
    public float getDy(final int i) {
        return (this.y[i + 1] - this.y[i]);
    }

    @Override
    public float getDz(final int i) {
        return (this.x[i + 1] - this.x[i]) * this.dz;
    }

    @Override
    public float getLength() {
        return this.length;
    }

    @Override
    public SegmentIterator iterator() {
        return this.iterator(false);
    }

    @Override
    public Curve lerp(final Curve curve, final float delta) {
        if (this == curve) {
            return this;
        }
        if (curve.getClass() != this.getClass()) {
            return curve;
        }
        Catenary other = (Catenary) curve;
        if (this.count > other.count) {
            return other.lerp(this, 1.0F - delta);
        }
        final float[] nx = new float[this.count];
        final float[] ny = new float[this.count];
        for (int i = 0; i < this.count; i++) {
            final boolean end = this.count != other.count && i == this.count - 1;
            nx[i] = net.minecraft.util.Mth.lerp(delta, this.x[i], other.x[end ? other.count - 1 : i]);
            ny[i] = net.minecraft.util.Mth.lerp(delta, this.y[i], other.y[end ? other.count - 1 : i]);
        }
        final float angle = Mth.lerpAngle(this.yaw, other.yaw, delta);
        final float vx = net.minecraft.util.Mth.cos(angle);
        final float vz = net.minecraft.util.Mth.sin(angle);
        return new Catenary(this.count, angle, vx, vz, nx, ny, net.minecraft.util.Mth.lerp(delta, this.length, other.length));
    }

    public void visitPoints(final float spacing, final boolean center, final PointVisitor visitor) {
        float distance = center ? (this.length % spacing + spacing) / 2.0F : 0;
        int index = 0;
        final Catenary.SegmentIterator it = this.iterator();
        while (it.next()) {
            final float length = it.getLength();
            while (distance < length) {
                final float t = distance / length;
                visitor.visit(index++, it.getX(t), it.getY(t), it.getZ(t), it.getYaw(), it.getPitch());
                distance += spacing;
            }
            distance -= length;
            if (!center && !it.hasNext()) {
                visitor.visit(index++, it.getX(1.0F), it.getY(1.0F), it.getZ(1.0F), it.getYaw(), it.getPitch());
            }
        }
    }

    @Override
    public SegmentIterator iterator(final boolean inclusive) {
        return new CurveSegmentIterator<>(this, inclusive) {

            @Override
            public float getYaw() {
                return this.curve.yaw;
            }

            @Override
            protected float getPitch(int index) {
                final float dx = this.curve.x[index + 1] - this.curve.x[index];
                final float dy = this.curve.y[index + 1] - this.curve.y[index];
                return (float) net.minecraft.util.Mth.atan2(dy, dx);
            }

            @Override
            public float getLength(final int index) {
                final float dx = this.curve.x[index + 1] - this.curve.x[index];
                final float dy = this.curve.y[index + 1] - this.curve.y[index];
                return net.minecraft.util.Mth.sqrt(dx * dx + dy * dy);
            }
        };
    }


    public static Catenary from(final Vec3 direction, final float verticalYaw, final CubicBezier bezier, final float slack) {
        final float dist = (float) direction.length();
        final float length;
        if (slack < 1e-2 || Math.abs(direction.x) < 1e-6 && Math.abs(direction.z) < 1e-6) {
            length = dist;
        } else {
            length = dist + (lengthFunc(bezier, dist) - dist) * slack;
        }
        return from(direction, verticalYaw, length);
    }

    private static float lengthFunc(final CubicBezier bezier, final double length) {
        return bezier.eval(net.minecraft.util.Mth.clamp((float) length / Connection.MAX_LENGTH, 0, 1)) * Connection.MAX_LENGTH;
    }

    public static Catenary from(final Vec3 dir, final float verticalYaw, final float ropeLength) {
        final float endX = net.minecraft.util.Mth.sqrt((float) (dir.x * dir.x + dir.z * dir.z));
        final float endY = (float) dir.y;
        final float angle = endX < 1e-3F ? endY < 0.0F ? verticalYaw + Mth.PI : verticalYaw : (float) net.minecraft.util.Mth.atan2(dir.z, dir.x);
        final float vx = net.minecraft.util.Mth.cos(angle);
        final float vz = net.minecraft.util.Mth.sin(angle);
        if (dir.length() > 2.0F * Connection.MAX_LENGTH) {
            return new Catenary(2, angle, vx, vz, new float[]{0.0F, endX}, new float[]{0.0F, endY}, net.minecraft.util.Mth.sqrt(endX * endX + endY * endY));
        }
        final int count = Math.max((int) (ropeLength * CatenaryUtils.SEG_LENGTH), MIN_VERTEX_COUNT);
        final float[] x = new float[count];
        final float[] y = new float[count];
        CatenaryUtils.catenary(0.0F, 0.0F, endX, endY, ropeLength, count, x, y);
        float length = 0.0F;
        for (int i = 1; i < count; i++) {
            final float dx = x[i] - x[i - 1];
            final float dy = y[i] - y[i - 1];
            length += net.minecraft.util.Mth.sqrt(dx * dx + dy * dy);
        }
        return new Catenary(count, angle, vx, vz, x, y, length);
    }
}
