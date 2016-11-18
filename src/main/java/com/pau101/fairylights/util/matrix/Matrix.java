package com.pau101.fairylights.util.matrix;

import java.util.Objects;
import java.util.Stack;

import net.minecraft.util.math.Vec3d;

public final class Matrix {
	private final Stack<Mat4d> matrixStack;

	public Matrix() {
		matrixStack = new Stack<Mat4d>();
		Mat4d mat = new Mat4d();
		mat.asIdentity();
		matrixStack.push(mat);
	}

	public void push() {
		matrixStack.push(new Mat4d(matrixStack.peek()));
	}

	public void pop() {
		if (matrixStack.size() < 2) {
			throw new RuntimeException("StackUnderflow");
		}
		matrixStack.pop();
	}

	public void translate(double x, double y, double z) {
		Mat4d mat = matrixStack.peek();
		Mat4d translation = new Mat4d();
		translation.asTranslation(x, y, z);
		mat.mul(translation);
	}

	public void rotate(double angle, double x, double y, double z) {
		Mat4d mat = matrixStack.peek();
		Mat4d rotation = new Mat4d();
		rotation.asRotation(x, y, z, angle);
		mat.mul(rotation);
	}

	public void scale(double x, double y, double z) {
		Mat4d mat = matrixStack.peek();
		Mat4d scale = new Mat4d();
		scale.m00 = x;
		scale.m11 = y;
		scale.m22 = z;
		scale.m33 = 1;
		mat.mul(scale);
	}

	public Vec3d transform(Vec3d point) {
		Objects.requireNonNull(point, "point");
		Mat4d mat = matrixStack.peek();
		return mat.transform(point);
	}
}
