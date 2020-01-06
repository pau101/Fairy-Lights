package me.paulf.fairylights.server.fastener.connection;

import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.CatenaryUtils;
import me.paulf.fairylights.util.CubicBezier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Catenary {
    private static final int MIN_VERTEX_COUNT = 8;

    private static final int SCALE = 16;

    private final Vec3d vector;

    private final Segment[] segments;

    private final float length;

    private Catenary(final Vec3d vector, final Segment[] segments, final float length) {
        this.vector = vector;
        this.segments = segments;
        this.length = length;
    }

    public Vec3d getVector() {
        return this.vector;
    }

    public Segment[] getSegments() {
        return this.segments;
    }

    public float getLength() {
        return this.length;
    }

    public static Catenary from(final Vec3d direction, final CubicBezier bezier, final float slack) {
        final float dist = (float) direction.length();
        if (dist > 2.0F * Connection.MAX_LENGTH) {
            final Segment seg = new Segment(Vec3d.ZERO);
            seg.connectTo(direction.scale(SCALE));
            return new Catenary(direction, new Segment[]{seg}, dist);
        }
        final float length;
        if (slack < 1e-2 || Math.abs(direction.x) < 1e-6 && Math.abs(direction.z) < 1e-6) {
            length = dist;
        } else {
            length = dist + (lengthFunc(bezier, dist) - dist) * slack;
        }
        return from(direction, length);
    }

    private static float lengthFunc(final CubicBezier bezier, final double length) {
        return bezier.eval(MathHelper.clamp((float) length / Connection.MAX_LENGTH, 0, 1)) * Connection.MAX_LENGTH;
    }

    public static Catenary from(final Vec3d direction, final float ropeLength) {
        final float rotation = (float) MathHelper.atan2(direction.z, direction.x);
        int vertexCount = (int) (ropeLength * CatenaryUtils.SEG_LENGTH);
        if (vertexCount < MIN_VERTEX_COUNT) {
            vertexCount = MIN_VERTEX_COUNT;
        }
        final float endX = MathHelper.sqrt(direction.x * direction.x + direction.z * direction.z);
        final float[][] result = CatenaryUtils.catenary(0, 0, endX, (float) direction.y, ropeLength, vertexCount);
        final float[] xs = result[0];
        final float[] ys = result[1];
        final Segment[] segments = new Segment[xs.length - 1];
        final float rotationCos = MathHelper.cos(rotation);
        final float rotationSin = MathHelper.sin(rotation);
        /*
         * / double mag = Math.sqrt(direction.x * direction.x + direction.z * direction.z); double perpX = -direction.z / mag, perpZ =
         * direction.x / mag; double t = System.currentTimeMillis() / 200D; //
         */
        float length = 0;
        Segment prev = null;
        for (int i = 0, end = xs.length - 1; ; i++) {
            /*
             * / int dist = Math.min(i, xs.length - i - 1); double mult = Math.min(dist, 6) / 6D; double dev = Math.sin(t + i * 0.6) * 8 * mult; double
             * devY = Math.sin(t + Math.PI / 2 + i * 0.6) * 8 * mult; //
             */
            final Vec3d vertex = new Vec3d(xs[i] * rotationCos * SCALE/* + perpX * dev */, ys[i] * SCALE/* + devY */,
                xs[i] * rotationSin * SCALE/* + perpZ * dev */
            );
            if (i > 0) {
                prev.connectTo(vertex);
                length += prev.getLength();
            }
            if (i == end) {
                break;
            }
            segments[i] = prev = new Segment(vertex);
        }
        return new Catenary(direction, segments, length);
    }
}
