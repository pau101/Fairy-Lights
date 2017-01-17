package com.pau101.fairylights.util;

import java.util.Objects;
import java.util.function.ToIntFunction;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Mth {
	private Mth() {}

	public static final float PI = (float) Math.PI;

	public static final float HALF_PI = (float) (Math.PI / 2);

	public static final float TAU = (float) (2 * Math.PI);

	public static final float DEG_TO_RAD = (float) (Math.PI / 180);

	public static final float RAD_TO_DEG = (float) (180 / Math.PI);

	public static int mod(int a, int b) {
		return (a % b + b) % b;
	}

	public static float mod(float a, float b) {
		return (a % b + b) % b;
	}

	public static double mod(double a, double b) {
		return (a % b + b) % b;
	}

	public static float transform(float x, float domainMin, float domainMax, float rangeMin, float rangeMax) {
		if (x <= domainMin) {
			return rangeMin;
		}
		if (x >= domainMax) {
			return rangeMax;
		}
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static double transform(double x, double domainMin, double domainMax, double rangeMin, double rangeMax) {
		if (x <= domainMin) {
			return rangeMin;
		}
		if (x >= domainMax) {
			return rangeMax;
		}
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static float[] toEulerYZX(float x, float y, float z, float angle) {
		float s = MathHelper.sin(angle);
		float c = MathHelper.cos(angle);
		float t = 1 - c;
		float magnitude = MathHelper.sqrt_float(x * x + y * y + z * z);
		if (magnitude == 0) {
			throw new IllegalArgumentException("Ubiquitous vector!");
		}
		x /= magnitude;
		y /= magnitude;
		z /= magnitude;
		float pitch, yaw, roll;
		// north pole singularity
		if (x * y * t + z * s > 0.998F) {
			pitch = (float) (2 * Math.atan2(x * Math.sin(angle / 2), Math.cos(angle / 2)));
			yaw = PI / 2;
			roll = 0;
			return new float[] { roll, pitch, yaw };
		}
		// south pole singularity
		if (x * y * t + z * s < -0.998F) {
			pitch = (float) (-2 * Math.atan2(x * Math.sin(angle / 2), Math.cos(angle / 2)));
			yaw = -PI / 2;
			roll = 0;
			return new float[] { roll, pitch, yaw };
		}
		pitch = (float) Math.atan2(y * s - x * z * t, 1 - (y * y + z * z) * t);
		yaw = (float) Math.asin(x * y * t + z * s);
		roll = (float) Math.atan2(x * s - y * z * t, 1 - (x * x + z * z) * t);
		return new float[] { roll, pitch, yaw };
	}

	public static int hash(int x) {
		x = (x >> 16 ^ x) * 0x45D9F3B;
		x = (x >> 16 ^ x) * 0x45D9F3B;
		x = x >> 16 ^ x;
		return x;
	}

	public static Vec3d negate(Vec3d vector) {
		return new Vec3d(-Objects.requireNonNull(vector, "vector").xCoord, -vector.yCoord, -vector.zCoord);
	}

	public static Vec3d lerp(Vec3d a, Vec3d b, double t) {
		Objects.requireNonNull(a, "a vector");
		Objects.requireNonNull(b, "b vector");
		double x = a.xCoord + (b.xCoord - a.xCoord) * t;
		double y = a.yCoord + (b.yCoord - a.yCoord) * t;
		double z = a.zCoord + (b.zCoord - a.zCoord) * t;
		return new Vec3d(x, y, z);
	}

	public static Vec3d lerpAngles(Vec3d a, Vec3d b, double t) {
		Objects.requireNonNull(a, "a vector");
		Objects.requireNonNull(b, "b vector");
		double x = lerpAngle(a.xCoord, b.xCoord, t);
		double y = lerpAngle(a.yCoord, b.yCoord, t);
		double z = lerpAngle(a.zCoord, b.zCoord, t);
		return new Vec3d(x, y, z);
	}

	public static double lerpAngle(double a, double b, double t) {
		return a + t * angleDifference(a, b);
	}

	public static double angleDifference(double a, double b) {
		return mod(b - a + Math.PI, Mth.TAU) - Math.PI;
	}

	public static int floorInterval(int x, int n) {
		return x / n * n;
	}

	public static double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}

	public static double max(double a, double b, double c) {
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

	public static int lcm(int a, int b) {
		return Math.abs(a * b) / gcd(a, b);
	}

	public static int gcd(int a, int b) {
		while (b != 0) {
			b = mod(a, a = b);
		}
		return a;
	}

	public static double angle(Vec3d a, Vec3d b) {
		Objects.requireNonNull(a, "a vector");
		Objects.requireNonNull(b, "b vector");
		double theta = a.dotProduct(b) / (a.lengthVector() * b.lengthVector());
		if (theta > 1) {
			return 0;
		}
		if (theta < -1) {
			return Math.PI;
		}
		return Math.acos(theta);
	}

	public static <T> int[] invertMap(T[] map, ToIntFunction<T> asInt) {
		for (T v : map) {
			int vp = asInt.applyAsInt(v);
			if (vp < 0 || vp >= map.length) {
				throw new IllegalArgumentException("Must be a perfect map, " + v + " out of range with " + vp);
			}
		}
		int[] inverse = new int[map.length];
		for (int i = 0; i < map.length; i++) {
			inverse[asInt.applyAsInt(map[i])] = i;
		}
		return inverse;
	}
}
