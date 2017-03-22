package com.pau101.fairylights.util;

import java.util.Random;

import com.google.common.base.Preconditions;

import net.minecraft.util.math.MathHelper;

public final class RandomArray {
	private final float[] values;

	public RandomArray(long seed, int length) {
		this(seed, length, -1, 1);
	}

	public RandomArray(long seed, int length, float min, float max) {
		Preconditions.checkArgument(length > 0, "length must be greater than zero");
		values = new float[length];
		Random rng = new Random(seed);
		float range = max - min;
		for (int i = 0; i < length; i++) {
			values[i] = rng.nextFloat() * range + min;
		}
	}

	public float get(int index) {
		return values[Mth.mod(index, values.length)];
	}

	public float get(float t) {
		int t0 = MathHelper.floor(Mth.mod(t, values.length));
		int t1 = Mth.mod(t0 + 1, values.length);
		return values[t0] * (1 - t % 1) + values[t1] * (t % 1);
	}
}
