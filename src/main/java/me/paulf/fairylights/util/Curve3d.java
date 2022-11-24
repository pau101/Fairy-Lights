package me.paulf.fairylights.util;

import net.minecraft.util.Mth;

public final class Curve3d implements Curve {

    private final int count;

    private final float[] x;

    private final float[] y;

    private final float[] z;

    private final float length;

    public Curve3d(final int count, final float[] x, final float[] y, final float[] z, final float length) {
        this.count = count;
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public float getX() {
        return this.x[this.count - 1];
    }

    @Override
    public float getY() {
        return this.y[this.count - 1];
    }

    @Override
    public float getZ() {
        return this.z[this.count - 1];
    }

    @Override
    public float getX(int i) {
        return this.x[i];
    }

    @Override
    public float getX(int i, float lerp) {
        return Mth.lerp(lerp, this.x[i], this.x[i + 1]);
    }

    @Override
    public float getY(int i) {
        return this.y[i];
    }

    @Override
    public float getY(int i, float lerp) {
        return Mth.lerp(lerp, this.y[i], this.y[i + 1]);
    }

    @Override
    public float getZ(int i) {
        return this.z[i];
    }

    @Override
    public float getZ(int i, float lerp) {
        return Mth.lerp(lerp, this.z[i], this.z[i + 1]);
    }

    @Override
    public float getDx(int i) {
        return this.x[i + 1] - this.x[i];
    }

    @Override
    public float getDy(int i) {
        return this.y[i + 1] - this.y[i];
    }

    @Override
    public float getDz(int i) {
        return this.z[i + 1] - this.z[i];
    }

    @Override
    public float getLength() {
        return this.length;
    }

    @Override
    public Curve lerp(Curve other, float delta) {
        return other;
    }

    @Override
    public SegmentIterator iterator(boolean inclusive) {
        return new CurveSegmentIterator<>(this, inclusive) {

            @Override
            public float getYaw() {
                this.checkIndex(1.0F);
                if (this.inclusive) {
                    throw new IllegalStateException();
                }
                final float dx = this.curve.x[this.index + 1] - this.curve.x[this.index];
                final float dz = this.curve.z[this.index + 1] - this.curve.z[this.index];
                return (float) Mth.atan2(dz, dx);
            }

            @Override
            protected float getPitch(int index) {
                final float dx = this.curve.x[index + 1] - this.curve.x[index];
                final float dy = this.curve.y[index + 1] - this.curve.y[index];
                final float dz = this.curve.z[index + 1] - this.curve.z[index];
                return (float) Mth.atan2(dy, Mth.sqrt(dx * dx + dz * dz));
            }

            @Override
            public float getLength(final int index) {
                final float dx = this.curve.x[index + 1] - this.curve.x[index];
                final float dy = this.curve.y[index + 1] - this.curve.y[index];
                final float dz = this.curve.z[index + 1] - this.curve.z[index];
                return Mth.sqrt(dx * dx + dy * dy + dz * dz);
            }
        };
    }
}
