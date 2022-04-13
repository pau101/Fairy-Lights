package me.paulf.fairylights.util;

import com.mojang.math.Vector3d;

public class VectorUtils {
	/** length
	 *   returns the length of a vector
	 *   essentially a replacement for the old Vector3d::length function.
	 * */
	public static double length(Vector3d vec) {
		return Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
	}
	
	// reimplements Vector3d::dotProduct
	public static double dotProduct(Vector3d left, Vector3d right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}
}
