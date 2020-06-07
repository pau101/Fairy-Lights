package me.paulf.fairylights.server.fastener.connection.type;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Feature;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class HangingFeature implements Feature {
    protected final int index;

    protected Vec3d point;

    protected Vec3d prevPoint;

    protected float yaw, pitch, roll;

    protected float prevYaw, prevPitch, prevRoll;

    public HangingFeature(final int index, final Vec3d point, final float yaw, final float pitch, final float roll) {
        this.index = index;
        this.point = this.prevPoint = point;
        this.prevYaw = this.yaw = yaw;
        this.prevPitch = this.pitch = pitch;
        this.prevRoll = this.roll = roll;
    }

    public void set(final Vec3d point, final float yaw, final float pitch) {
        this.prevPoint = this.point;
        this.point = point;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public final int getId() {
        return this.index;
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

    public final Vec3d getAbsolutePoint(final Fastener<?> fastener) {
        return this.getAbsolutePoint(fastener.getConnectionPoint());
    }

    public final Vec3d getAbsolutePoint(final Vec3d origin) {
        return this.point.add(origin);
    }

    public final Vec3d getTransformedPoint(final Vec3d origin, final Vec3d point) {
        final MatrixStack matrix = new MatrixStack();
        matrix.rotate(-this.getYaw(), 0.0F, 1.0F, 0.0F);
        if (this.parallelsCord()) {
            matrix.rotate(this.getPitch(), 0.0F, 0.0F, 1.0F);
        }
        matrix.rotate(this.getRoll(), 1.0F, 0.0F, 0.0F);
        return this.point.add(matrix.transform(point)).add(origin);
    }

    public void tick(final World world) {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;
    }

    public abstract AxisAlignedBB getBounds();

    public abstract boolean parallelsCord();
}
