package me.paulf.fairylights.util;

import net.minecraft.util.math.*;

import java.util.*;
import java.util.function.*;

public final class Mth {
    private Mth() {}

    public static final float PI = (float) Math.PI;

    public static final float HALF_PI = (float) (Math.PI / 2);

    public static final float TAU = (float) (2 * Math.PI);

    public static final float DEG_TO_RAD = (float) (Math.PI / 180);

    public static final float RAD_TO_DEG = (float) (180 / Math.PI);

    public static int mod(final int a, final int b) {
        return (a % b + b) % b;
    }

    public static float mod(final float a, final float b) {
        return (a % b + b) % b;
    }

    public static double mod(final double a, final double b) {
        return (a % b + b) % b;
    }

    public static float transform(final float x, final float domainMin, final float domainMax, final float rangeMin, final float rangeMax) {
        if (x <= domainMin) {
            return rangeMin;
        }
        if (x >= domainMax) {
            return rangeMax;
        }
        return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
    }

    public static double transform(final double x, final double domainMin, final double domainMax, final double rangeMin, final double rangeMax) {
        if (x <= domainMin) {
            return rangeMin;
        }
        if (x >= domainMax) {
            return rangeMax;
        }
        return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
    }

    public static float[] toEulerYZX(float x, float y, float z, final float angle) {
        final float s = MathHelper.sin(angle);
        final float c = MathHelper.cos(angle);
        final float t = 1 - c;
        final float magnitude = MathHelper.sqrt(x * x + y * y + z * z);
        if (magnitude == 0) {
            throw new IllegalArgumentException("Ubiquitous vector!");
        }
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        final float pitch;
        float yaw;
        final float roll;
        // north pole singularity
        if (x * y * t + z * s > 0.998F) {
            pitch = (float) (2 * Math.atan2(x * Math.sin(angle / 2), Math.cos(angle / 2)));
            yaw = PI / 2;
            roll = 0;
            return new float[]{roll, pitch, yaw};
        }
        // south pole singularity
        if (x * y * t + z * s < -0.998F) {
            pitch = (float) (-2 * Math.atan2(x * Math.sin(angle / 2), Math.cos(angle / 2)));
            yaw = -PI / 2;
            roll = 0;
            return new float[]{roll, pitch, yaw};
        }
        pitch = (float) Math.atan2(y * s - x * z * t, 1 - (y * y + z * z) * t);
        yaw = (float) Math.asin(x * y * t + z * s);
        roll = (float) Math.atan2(x * s - y * z * t, 1 - (x * x + z * z) * t);
        return new float[]{roll, pitch, yaw};
    }

    public static int hash(int x) {
        x = (x >> 16 ^ x) * 0x45D9F3B;
        x = (x >> 16 ^ x) * 0x45D9F3B;
        x = x >> 16 ^ x;
        return x;
    }

    public static Vec3d negate(final Vec3d vector) {
        return new Vec3d(-Objects.requireNonNull(vector, "vector").x, -vector.y, -vector.z);
    }

    public static Vec3d lerp(final Vec3d a, final Vec3d b, final double t) {
        Objects.requireNonNull(a, "a vector");
        Objects.requireNonNull(b, "b vector");
        final double x = a.x + (b.x - a.x) * t;
        final double y = a.y + (b.y - a.y) * t;
        final double z = a.z + (b.z - a.z) * t;
        return new Vec3d(x, y, z);
    }

    public static float lerpAngle(final float a, final float b, final float t) {
        return a + t * angleDifference(a, b);
    }

    public static float angleDifference(final float a, final float b) {
        return mod(b - a + Mth.PI, Mth.TAU) - Mth.PI;
    }

    public static int floorInterval(final int x, final int n) {
        return x / n * n;
    }

    public static double min(final double a, final double b, final double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static double max(final double a, final double b, final double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static int log2(int n) {
        if (n <= 0) {
            throw new ArithmeticException("Negative infinity: " + n);
        }
        int r = 0;
        while ((n >>= 1) > 0) {
            r++;
        }
        return r;
    }

    public static int lcm(final int a, final int b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            b = mod(a, a = b);
        }
        return a;
    }

    public static double angle(final Vec3d a, final Vec3d b) {
        Objects.requireNonNull(a, "a vector");
        Objects.requireNonNull(b, "b vector");
        final double theta = a.dotProduct(b) / (a.length() * b.length());
        if (theta > 1) {
            return 0;
        }
        if (theta < -1) {
            return Math.PI;
        }
        return Math.acos(theta);
    }

    public static <T> int[] invertMap(final T[] map, final ToIntFunction<T> asInt) {
        for (final T v : map) {
            final int vp = asInt.applyAsInt(v);
            if (vp < 0 || vp >= map.length) {
                throw new IllegalArgumentException("Must be a perfect map, " + v + " out of range with " + vp);
            }
        }
        final int[] inverse = new int[map.length];
        for (int i = 0; i < map.length; i++) {
            inverse[asInt.applyAsInt(map[i])] = i;
        }
        return inverse;
    }
}
