package me.paulf.fairylights.server.feature;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class HangingFeature implements Feature {
    protected final int index;

    protected Vec3d point;

    protected Vec3d prevPoint;

    private Vec3d targetPoint;

    protected float yaw, pitch, roll;

    protected float prevYaw, prevPitch, prevRoll;

    protected float targetYaw, targetPitch;

    protected final float descent;

    public HangingFeature(final int index, final Vec3d point, final float yaw, final float pitch, final float roll, final float descent) {
        this.index = index;
        this.point = this.prevPoint = this.targetPoint = point;
        this.prevYaw = this.yaw = this.targetYaw = yaw;
        this.prevPitch = this.pitch = this.targetPitch = pitch;
        this.prevRoll = this.roll = roll;
        this.descent = descent;
    }

    public void set(final Vec3d point, final float yaw, final float pitch) {
        this.targetPoint = point;
        this.targetYaw = yaw;
        this.targetPitch = pitch;
    }

    @Override
    public final int getId() {
        return this.index;
    }

    public final Vec3d getPoint() {
        return this.targetPoint;
    }

    public final Vec3d getPoint(final float delta) {
        return this.point.subtract(this.prevPoint).scale(delta).add(this.prevPoint);
    }

    public final float getYaw() {
        return this.yaw;
    }

    public final float getPitch() {
        return this.pitch;
    }

    public final float getRoll() {
        return this.roll;
    }

    public final float getYaw(final float t) {
        return Mth.lerpAngle(this.prevYaw, this.yaw, t);
    }

    public final float getPitch(final float t) {
        return Mth.lerpAngle(this.prevPitch, this.pitch, t);
    }

    public final float getRoll(final float t) {
        return Mth.lerpAngle(this.prevRoll, this.roll, t);
    }

    public float getDescent() {
        return this.descent;
    }

    public final Vec3d getAbsolutePoint(final Fastener<?> fastener) {
        return this.getAbsolutePoint(fastener.getConnectionPoint());
    }

    public final Vec3d getAbsolutePoint(final Vec3d origin) {
        return this.point.add(origin);
    }

    public Vec3d getTransformedPoint(final Vec3d origin, final Vec3d point) {
        final MatrixStack matrix = new MatrixStack();
        matrix.rotate(-this.getYaw(), 0.0F, 1.0F, 0.0F);
        if (this.parallelsCord()) {
            matrix.rotate(this.getPitch(), 0.0F, 0.0F, 1.0F);
        }
        matrix.rotate(this.getRoll(), 1.0F, 0.0F, 0.0F);
        matrix.translate(0.0F, -this.getDescent(), 0.0F);
        return this.point.add(matrix.transform(point)).add(origin);
    }

    public void tick(final World world) {
        this.prevPoint = this.point;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;
        this.point = this.targetPoint;
        this.yaw = this.targetYaw;
        this.pitch = this.targetPitch;
    }

    public abstract AxisAlignedBB getBounds();

    public abstract boolean parallelsCord();
}
