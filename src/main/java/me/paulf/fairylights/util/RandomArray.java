package me.paulf.fairylights.util;

import java.util.Random;

import com.google.common.base.Preconditions;

import net.minecraft.util.Mth;

public final class RandomArray {
    private final float[] values;

    public RandomArray(final long seed, final int length) {
        this(seed, length, -1, 1);
    }

    public RandomArray(final long seed, final int length, final float min, final float max) {
        Preconditions.checkArgument(length > 0, "length must be greater than zero");
        this.values = new float[length];
        final Random rng = new Random(seed);
        final float range = max - min;
        for (int i = 0; i < length; i++) {
            this.values[i] = rng.nextFloat() * range + min;
        }
    }

    public float get(final int index) {
        return this.values[FLMath.mod(index, this.values.length)];
    }

    public float get(final float t) {
        final int t0 = Mth.floor(FLMath.mod(t, this.values.length));
        final int t1 = FLMath.mod(t0 + 1, this.values.length);
        return this.values[t0] * (1 - t % 1) + this.values[t1] * (t % 1);
    }
}
