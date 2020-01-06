package me.paulf.fairylights.server.fastener.connection;

import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class Segment implements Feature {
    private final Vec3d start;

    private Vec3d end;

    private Vec3d rotation;

    private Vec3d vector;

    private double length;

    public Segment(final Vec3d start) {
        this.start = start;
    }

    public void connectTo(final Vec3d end) {
        this.end = end;
        this.length = this.start.distanceTo(end);
        this.vector = this.start.subtract(end).normalize();
        final double rotationYaw = -MathHelper.atan2(this.vector.z, this.vector.x) - Mth.HALF_PI;
        final double rotationPitch = MathHelper.atan2(this.vector.y, Math.sqrt(this.vector.x * this.vector.x + this.vector.z * this.vector.z));
        this.rotation = new Vec3d(rotationYaw, rotationPitch, 0);
    }

    public double getLength() {
        return this.length;
    }

    public Vec3d getRotation() {
        return this.rotation;
    }

    public Vec3d getStart() {
        return this.start;
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d pointAt(final double t) {
        return Mth.lerp(this.start, this.end, t);
    }

    public Vec3d getVector() {
        return this.vector;
    }

    @Override
    public int getId() {
        return 0;
    }
}
