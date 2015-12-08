package com.pau101.fairylights.util;

import net.minecraft.util.MathHelper;

public class MathUtils {
	private MathUtils() {}

	public static final float PI = (float) StrictMath.PI;

	public static final float TAU = (float) (2 * StrictMath.PI);

	public static final float DEG_TO_RAD = (float) (StrictMath.PI / 180);

	public static final float RAD_TO_DEG = (float) (180 / StrictMath.PI);

	public static int modi(int a, int b) {
		return (a % b + b) % b;
	}

	public static float modf(float a, float b) {
		return (a % b + b) % b;
	}

	public static float linearTransformf(float x, float domainMin, float domainMax, float rangeMin, float rangeMax) {
		x = x < domainMin ? domainMin : x > domainMax ? domainMax : x;
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	/**
	 * @return Euler angle in the order of YZX
	 */
	public static float[] toEuler(float x, float y, float z, float angle) {
		float s = MathHelper.sin(angle);
		float c = MathHelper.cos(angle);
		float t = 1 - c;
		float magnitude = MathHelper.sqrt_float(x * x + y * y + z * z);
		if (magnitude == 0) {
			throw new Error("Ubiquitous vector!");
		}
		x /= magnitude;
		y /= magnitude;
		z /= magnitude;
		float pitch, yaw, roll;
		if (x * y * t + z * s > 0.998F) { // north pole singularity detected
			pitch = (float) (2 * Math.atan2(x * Math.sin(angle / 2), Math.cos(angle / 2)));
			yaw = PI / 2;
			roll = 0;
			return new float[] { roll, pitch, yaw };
		}
		if (x * y * t + z * s < -0.998F) { // south pole singularity detected
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
}
