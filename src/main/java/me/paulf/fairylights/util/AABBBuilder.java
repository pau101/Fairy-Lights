package me.paulf.fairylights.util;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class AABBBuilder {
    private double minX;

    private double minY;

    private double minZ;

    private double maxX;

    private double maxY;

    private double maxZ;

    public AABBBuilder() {}

    public AABBBuilder(final BlockPos pos) {
        Objects.requireNonNull(pos, "pos");
        this.maxX = (this.minX = pos.getX()) + 1;
        this.maxY = (this.minY = pos.getY()) + 1;
        this.maxZ = (this.minZ = pos.getZ()) + 1;
    }

    public AABBBuilder(final Vector3d min, final Vector3d max) {
        this(
            Objects.requireNonNull(min, "min").x, min.y, min.z,
            Objects.requireNonNull(max, "max").x, max.y, max.z
        );
    }

    public AABBBuilder(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public AABBBuilder add(final Vector3d point) {
        return this.add(Objects.requireNonNull(point, "point").x, point.y, point.z);
    }

    public AABBBuilder add(final Vector3i point) {
        return this.add(Objects.requireNonNull(point, "point").getX(), point.getY(), point.getZ());
    }

    public AABBBuilder add(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return this;
    }

    public AABBBuilder include(final Vector3d point) {
        return this.include(Objects.requireNonNull(point, "point").x, point.y, point.z);
    }

    public AABBBuilder include(final double x, final double y, final double z) {
        if (x < this.minX) this.minX = x;
        if (y < this.minY) this.minY = y;
        if (z < this.minZ) this.minZ = z;
        if (x > this.maxX) this.maxX = x;
        if (y > this.maxY) this.maxY = y;
        if (z > this.maxZ) this.maxZ = z;
        return this;
    }

    public AABBBuilder expand(final double amount) {
        this.minX -= amount;
        this.minY -= amount;
        this.minZ -= amount;
        this.maxX += amount;
        this.maxY += amount;
        this.maxZ += amount;
        return this;
    }

    public AxisAlignedBB build() {
        return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public static AxisAlignedBB union(final List<AxisAlignedBB> aabbs) {
        Objects.requireNonNull(aabbs, "AABBs");
        return union(aabbs, aabb -> aabb);
    }

    public static <T> AxisAlignedBB union(final List<T> aabbs, final Function<T, AxisAlignedBB> mapper) {
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
