package com.pau101.fairylights.util;

import net.minecraft.util.MathHelper;

import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class Catenary {
	private static final float RATE = -0.05F / 10;

	private Segment[] segments;

	private float length;

	private Catenary() {
		length = 0;
	}

	public float getLength() {
		return length;
	}

	public Segment[] getSegments() {
		return segments;
	}

	private static float lengthFunc(float length) {
		return length * (length * RATE + 1.1F);
	}

	public static Catenary from(Vector3f direction, boolean tight) {
		Catenary catenary = new Catenary();
		float rotation = (float) Math.atan2(direction.z, direction.x);
		float length = tight ? direction.length() : lengthFunc(direction.length());
		int vertexCount = (int) (length * CatenaryUtils.SEG_LENGTH);
		if (vertexCount <= 1) {
			vertexCount = 2;
		}
		float[][] vertices2D = CatenaryUtils.catenary(new float[] { 0, 0 },
			new float[] { MathHelper.sqrt_float(direction.x * direction.x + direction.z * direction.z), direction.y }, length, vertexCount);
		catenary.segments = new Segment[vertices2D[0].length - 1];
		float[] xCoords = vertices2D[0];
		float[] yCoords = vertices2D[1];
		float rotationCos = MathHelper.cos(rotation), rotationSin = MathHelper.sin(rotation), scale = 16;
		for (int i = 0; i < xCoords.length; i++) {
			Point3f vertex = new Point3f(xCoords[i] * rotationCos * scale, yCoords[i] * scale, xCoords[i] * rotationSin * scale);
			if (i < xCoords.length - 1) {
				catenary.segments[i] = new Segment(vertex);
			}
			if (i > 0) {
				Segment segment = catenary.segments[i - 1];
				segment.connectTo(vertex);
				catenary.length += segment.getLength();
			}
		}
		return catenary;
	}
}
