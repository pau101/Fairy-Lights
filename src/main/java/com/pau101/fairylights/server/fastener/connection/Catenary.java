package com.pau101.fairylights.server.fastener.connection;

import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.util.CatenaryUtils;
import com.pau101.fairylights.util.CubicBezier;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Catenary {
	private static final int MIN_VERTEX_COUNT = 8;

	private static final int SCALE = 16;

	private final Vec3d vector;

	private final Segment[] segments;

	private final float length;

	private Catenary(Vec3d vector, Segment[] segments, float length) {
		this.vector = vector;
		this.segments = segments;
		this.length = length;
	}

	public Vec3d getVector() {
		return vector;
	}

	public Segment[] getSegments() {
		return segments;
	}

	public float getLength() {
		return length;
	}

	public static Catenary from(Vec3d direction, CubicBezier bezier, float slack) {
		float dist = (float) direction.length();
		if (dist > 2.0F * Connection.MAX_LENGTH) {
			final Segment seg = new Segment(Vec3d.ZERO);
			seg.connectTo(direction.scale(SCALE));
			return new Catenary(direction, new Segment[] { seg }, dist);
		}
		float length;
		if (slack < 1e-2 || Math.abs(direction.x) < 1e-6 && Math.abs(direction.z) < 1e-6) {
			length = dist;
		} else {
			length = dist + (lengthFunc(bezier, dist) - dist) * slack;
		}
		return from(direction, length);
	}

	private static float lengthFunc(CubicBezier bezier, double length) {
		return bezier.eval(MathHelper.clamp((float) length / Connection.MAX_LENGTH, 0, 1)) * Connection.MAX_LENGTH;
	}

	public static Catenary from(Vec3d direction, float ropeLength) {
		float rotation = (float) MathHelper.atan2(direction.z, direction.x);
		int vertexCount = (int) (ropeLength * CatenaryUtils.SEG_LENGTH);
		if (vertexCount < MIN_VERTEX_COUNT) {
			vertexCount = MIN_VERTEX_COUNT;
		}
		float endX = MathHelper.sqrt(direction.x * direction.x + direction.z * direction.z);
		float[][] result = CatenaryUtils.catenary(0, 0, endX, (float) direction.y, ropeLength, vertexCount);
		float[] xs = result[0];
		float[] ys = result[1];
		Segment[] segments = new Segment[xs.length - 1];
		float rotationCos = MathHelper.cos(rotation), rotationSin = MathHelper.sin(rotation);
		/*
		 * / double mag = Math.sqrt(direction.x * direction.x + direction.z * direction.z); double perpX = -direction.z / mag, perpZ =
		 * direction.x / mag; double t = System.currentTimeMillis() / 200D; //
		 */
		float length = 0;
		Segment prev = null;
		for (int i = 0, end = xs.length - 1;; i++) {
			/*
			 * / int dist = Math.min(i, xs.length - i - 1); double mult = Math.min(dist, 6) / 6D; double dev = Math.sin(t + i * 0.6) * 8 * mult; double
			 * devY = Math.sin(t + Math.PI / 2 + i * 0.6) * 8 * mult; //
			 */
			Vec3d vertex = new Vec3d(xs[i] * rotationCos * SCALE/* + perpX * dev */, ys[i] * SCALE/* + devY */,
				xs[i] * rotationSin * SCALE/* + perpZ * dev */
			);
			if (i > 0) {
				prev.connectTo(vertex);
				length += prev.getLength();
			}
			if (i == end) {
				break;
			}
			segments[i] = prev = new Segment(vertex);
		}
		return new Catenary(direction, segments, length);
	}
}
