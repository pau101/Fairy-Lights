package me.paulf.fairylights.util.matrix;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class MatrixStack implements Matrix {
	private final Deque<Matrix4> stack;

	public MatrixStack() {
		this.stack = new ArrayDeque<>();
		Matrix4 mat = new Matrix4();
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
	public void translate(float x, float y, float z) {
		Matrix4 mat = this.stack.getLast();
		Matrix4 translation = new Matrix4();
		translation.asTranslation(x, y, z);
		mat.mul(translation);
	}

	@Override
	public void rotate(float angle, float x, float y, float z) {
		Matrix4 mat = this.stack.getLast();
		Matrix4 rotation = new Matrix4();
		rotation.asRotation(x, y, z, angle);
		mat.mul(rotation);
	}

	public void scale(float x, float y, float z) {
		Matrix4 mat = this.stack.getLast();
		Matrix4 scale = new Matrix4();
		scale.m00 = x;
		scale.m11 = y;
		scale.m22 = z;
		scale.m33 = 1.0F;
		mat.mul(scale);
	}

	public Vec3d transform(Vec3d point) {
		Objects.requireNonNull(point, "point");
		Matrix4 mat = this.stack.getLast();
		return mat.transform(point);
	}
}
