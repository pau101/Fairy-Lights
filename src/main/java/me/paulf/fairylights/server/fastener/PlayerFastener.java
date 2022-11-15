package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.fastener.accessor.PlayerFastenerAccessor;
import me.paulf.fairylights.util.Mth;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class PlayerFastener extends EntityFastener<Player> {
    public PlayerFastener(final Player entity) {
        super(entity);
    }

    @Override
    public Vec3 getConnectionPoint() {
        final Vec3 point = super.getConnectionPoint();
        if (this.entity.isFallFlying()) {
            return point;
        }
        final double angle = (this.entity.yBodyRot - 90) * Mth.DEG_TO_RAD;
        final double perpAngle = angle - Math.PI / 2;
        final boolean sneaking = this.entity.isCrouching();
        final double perpDist = 0.4 * (this.matchesStack(this.entity.getMainHandItem()) ? 1 : -1);
        final double forwardDist;
        final double dy;
        if (sneaking) {
            forwardDist = 0;
            dy = 0.6;
        } else {
            forwardDist = 0.2;
            dy = 0.8;
        }
        final double dx = Math.cos(perpAngle) * perpDist - Math.cos(angle) * forwardDist;
        final double dz = Math.sin(perpAngle) * perpDist - Math.sin(angle) * forwardDist;
        return point.add(dx, dy, dz);
    }

    @Override
    public boolean isMoving() {
        return true;
    }

    @Override
    public boolean update() {
        if (!this.hasNoConnections() && !this.matchesStack(this.entity.getMainHandItem()) && !this.matchesStack(this.entity.getOffhandItem())) {
            for (final Connection connection : this.getAllConnections()) {
                if (!connection.shouldDrop()) {
                    connection.remove();
                }
            }
        }
        return super.update();
    }

    private boolean matchesStack(final ItemStack stack) {
        return this.getFirstConnection().filter(connection -> connection.matches(stack)).isPresent();
    }

    @Override
    public void resistSnap(final Vec3 from) {
        final double dist = this.getConnectionPoint().distanceTo(from);
        if (dist > Connection.MAX_LENGTH) {
            final double dx = this.entity.getX() - from.x;
            final double dy = this.entity.getY() - from.y;
            final double dz = this.entity.getZ() - from.z;
            final double vectorX = dx / dist;
            final double vectorY = dy / dist;
            final double vectorZ = dz / dist;
            final double factor = Math.min((dist - Connection.MAX_LENGTH) / Connection.PULL_RANGE, Connection.PULL_RANGE);
            final Vec3 motion = this.entity.getDeltaMovement();
            final double tangent = Math.cos(net.minecraft.util.Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz))) * Math.signum(motion.y);
            final double speed = motion.length();
            final double swing = Math.abs(speed) < 1e-6 ? 0 : (1 - Math.abs(motion.y / speed - tangent)) * 0.1;
            final double mag = Math.sqrt(motion.x * motion.x + tangent * tangent + motion.z * motion.z);
            final double arcX;
            final double arcY;
            final double arcZ;
            if (dy > 0 || Math.abs(mag) < 1e-6) {
                arcX = arcY = arcZ = 0;
            } else {
                arcX = motion.x / mag * swing;
                arcY = tangent / mag * swing;
                arcZ = motion.z / mag * swing;
            }
            this.entity.setDeltaMovement(
                motion.x + vectorX * -Math.abs(vectorX) * factor + arcX,
                motion.y + vectorY * -Math.abs(vectorY) * factor + arcY,
                motion.z + vectorZ * -Math.abs(vectorZ) * factor + arcZ
            );
            this.entity.fallDistance = 0;
            if (this.entity instanceof ServerPlayer) {
                ((ServerPlayer) this.entity).connection.send(new ClientboundSetEntityMotionPacket(this.entity));
            }
        }
    }

    @Override
    public PlayerFastenerAccessor createAccessor() {
        return new PlayerFastenerAccessor(this);
    }
}
