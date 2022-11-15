package me.paulf.fairylights.util.matrix;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class MatrixStack implements Matrix {
    private final Deque<Matrix4> stack;

    public MatrixStack() {
        this.stack = new ArrayDeque<>();
        final Matrix4 mat = new Matrix4();
        mat.asIdentity();
        this.stack.addLast(mat);
    }

    public void push() {
        this.stack.addLast(new Matrix4(this.stack.getLast()));
    }

    public void pop() {
        if (this.stack.size() <= 1) {
            throw new IllegalStateException("stack underflow");
        }
        this.stack.removeLast();
    }

    @Override
    public void translate(final float x, final float y, final float z) {
        final Matrix4 mat = this.stack.getLast();
        final Matrix4 translation = new Matrix4();
        translation.asTranslation(x, y, z);
        mat.mul(translation);
    }

    @Override
    public void rotate(final float angle, final float x, final float y, final float z) {
        final Matrix4 mat = this.stack.getLast();
        final Matrix4 rotation = new Matrix4();
        rotation.asRotation(x, y, z, angle);
        mat.mul(rotation);
    }

    public void scale(final float x, final float y, final float z) {
        final Matrix4 mat = this.stack.getLast();
        final Matrix4 scale = new Matrix4();
        scale.m00 = x;
        scale.m11 = y;
        scale.m22 = z;
        scale.m33 = 1.0F;
        mat.mul(scale);
    }

    public Vec3 transform(final Vec3 point) {
        Objects.requireNonNull(point, "point");
        final Matrix4 mat = this.stack.getLast();
        return mat.transform(point);
    }
}
