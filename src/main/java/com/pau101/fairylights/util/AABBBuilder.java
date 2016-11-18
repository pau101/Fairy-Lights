package com.pau101.fairylights.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import com.google.common.base.Preconditions;

public final class AABBBuilder {
    private double minX;

    private double minY;

    private double minZ;

    private double maxX;

    private double maxY;

    private double maxZ;

	public AABBBuilder() {}

	public AABBBuilder(BlockPos pos) {
		Objects.requireNonNull(pos, "pos");
		maxX = (minX = pos.getX()) + 1;
		maxY = (minY = pos.getY()) + 1;
		maxZ = (minZ = pos.getZ()) + 1;
	}

	public AABBBuilder(Vec3d min, Vec3d max) {
		this(
			Objects.requireNonNull(min, "min").xCoord, min.yCoord, min.zCoord,
			Objects.requireNonNull(max, "max").xCoord, max.yCoord, max.zCoord
		);
	}

	public AABBBuilder(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.minZ = Math.min(minZ, maxZ);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.maxZ = Math.max(minZ, maxZ);
	}

	public AABBBuilder add(Vec3d point) {
		return add(Objects.requireNonNull(point, "point").xCoord, point.yCoord, point.zCoord);
	}

	public AABBBuilder add(Vec3i point) {
		return add(Objects.requireNonNull(point, "point").getX(), point.getY(), point.getZ());
	}

	public AABBBuilder add(double x, double y, double z) {
		minX += x;
		minY += y;
		minZ += z;
		maxX += x;
		maxY += y;
		maxZ += z;
		return this;
	}

	public AABBBuilder include(Vec3d point) {
		return include(Objects.requireNonNull(point, "point").xCoord, point.yCoord, point.zCoord);
	}

	public AABBBuilder include(double x, double y, double z) {
		if (x < minX) minX = x;
		if (y < minY) minY = y;
		if (z < minZ) minZ = z;
		if (x > maxX) maxX = x;
		if (y > maxY) maxY = y;
		if (z > maxZ) maxZ = z;
		return this;
	}

	public AABBBuilder expand(double amount) {
		minX -= amount;
		minY -= amount;
		minZ -= amount;
		maxX += amount;
		maxY += amount;
		maxZ += amount;
		return this;
	}

	public AxisAlignedBB build() {
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static AxisAlignedBB union(List<AxisAlignedBB> aabbs) {
		Objects.requireNonNull(aabbs, "AABBs");
		return union(aabbs, aabb -> aabb);
	}

	public static <T> AxisAlignedBB union(List<T> aabbs, Function<T, AxisAlignedBB> mapper) {
		Objects.requireNonNull(aabbs, "AABBs");
		Objects.requireNonNull(mapper, "mapper");
		Preconditions.checkArgument(aabbs.size() > 0, "Must have more than zero AABBs");
		AxisAlignedBB bounds = mapper.apply(aabbs.get(0));
		if (aabbs.size() == 1) {
			return Objects.requireNonNull(bounds, "mapper returned bounds");
		}
		double minX = bounds.minX, minY = bounds.minY, minZ = bounds.minZ,
			   maxX = bounds.maxX, maxY = bounds.maxY, maxZ = bounds.maxZ;
		for (int i = 1, size = aabbs.size(); i < size; i++) {
			bounds = Objects.requireNonNull(mapper.apply(aabbs.get(i)), "mapper returned bounds");
			minX = Mth.min(minX, bounds.minX, bounds.maxX);
			minY = Mth.min(minY, bounds.minY, bounds.maxY);
			minZ = Mth.min(minZ, bounds.minZ, bounds.maxZ);
			maxX = Mth.max(maxX, bounds.minX, bounds.maxX);
			maxY = Mth.max(maxY, bounds.minY, bounds.maxY);
			maxZ = Mth.max(maxZ, bounds.minZ, bounds.maxZ);
		}
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
