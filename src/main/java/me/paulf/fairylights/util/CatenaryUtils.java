package me.paulf.fairylights.util;

import net.minecraft.util.math.*;

/*
 * Based off of
 * https://www.mathworks.com/matlabcentral/fileexchange/38550-catenary-hanging-rope-between-two-points/content/catenary.m
 */
public final class CatenaryUtils {
    private CatenaryUtils() {}

    public static final int SEG_LENGTH = 3;

    private static final int MAX_ITER = 100;

    private static final float MIN_GRAD = 1e-10F;

    private static final float MIN_VAL = 1e-8F;

    private static final float STEP_DEC = 0.5F;

    private static final float MIN_STEP = 1e-9F;

    private static final float MIN_HORIZ = 1e-3F;

    public static void catenary(final float x1, final float y1, final float x2, final float y2, final float length, final int pointCount, final float[] x, final float[] y) {
        catenary(x1, y1, x2, y2, length, pointCount, x, y, 1);
    }

    private static void catenary(float x1, float y1, float x2, float y2, final float length, final int pointCount, final float[] x, final float[] y, float sag) {
        if (x1 > x2) {
            float temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        final float d = x2 - x1;
        final float h = y2 - y1;
        if (MathHelper.abs(d) < MIN_HORIZ) {
            for (int i = 0, len = x.length; i < len; i++) {
                x[i] = (x1 + x2) / 2;
            }
            if (length < MathHelper.abs(h)) {
                linspace(y1, y2, pointCount, y, 0);
            } else {
                sag = (length - MathHelper.abs(h)) / 2;
                final int nSag = MathHelper.ceil(pointCount * sag / length);
                final float yMax = Math.max(y1, y2);
                final float yMin = Math.min(y1, y2);
                linspace(yMax, yMin - sag, pointCount - nSag, y, 0);
                linspace(yMin - sag, yMin, nSag, y, pointCount - nSag);
            }
            return;
        }
        linspace(x1, x2, pointCount, x, 0);
        if (length <= MathHelper.sqrt(d * d + h * h)) {
            linspace(y1, y2, pointCount, y, 0);
            return;
        }
        for (int iter = 0; iter < MAX_ITER; iter++) {
            final float val = g(sag, d, length, h);
            final float grad = dg(sag, d);
            if (MathHelper.abs(val) < MIN_VAL || MathHelper.abs(grad) < MIN_GRAD) {
                break;
            }
            final float search = -g(sag, d, length, h) / dg(sag, d);
            float alpha = 1;
            float sagNew = sag + alpha * search;
            final float valAbs = MathHelper.abs(val);
            while (sagNew < 0 || MathHelper.abs(g(sagNew, d, length, h)) > valAbs) {
                alpha = STEP_DEC * alpha;
                if (alpha < MIN_STEP) {
                    break;
                }
                sagNew = sag + alpha * search;
            }
            sag = sagNew;
        }
        final float xLeft = 0.5F * ((float) Math.log((length + h) / (length - h)) / sag - d);
        final float xMin = x1 - xLeft;
        final float bias = y1 - (float) Math.cosh(xLeft * sag) / sag;
        for (int i = 0; i < pointCount; i++) {
            y[i] = (float) Math.cosh((x[i] - xMin) * sag) / sag + bias;
        }
    }

    private static float dg(final float s, final float d) {
        return 2 * (float) Math.cosh(s * d / 2) * d / (2 * s) - 2 * (float) Math.sinh(s * d / 2) / (s * s);
    }

    private static float g(final float s, final float d, final float length, final float h) {
        return 2 * (float) Math.sinh(s * d / 2) / s - MathHelper.sqrt(length * length - h * h);
    }

    private static void linspace(final float base, final float limit, final int n, final float[] elements, final int offset) {
        final float scalar = n > 1 ? (limit - base) / (n - 1) : 0;
        for (int i = 0; i < n; i++) {
            elements[offset + i] = base + scalar * i;
        }
    }
}
