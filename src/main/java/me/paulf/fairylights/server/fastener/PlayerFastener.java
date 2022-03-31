package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.PlayerFastenerAccessor;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.util.Mth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3d;

public final class PlayerFastener extends EntityFastener<PlayerEntity> {
    public PlayerFastener(final PlayerEntity entity) {
        super(entity);
    }

    @Override
    public Vector3d getConnectionPoint() {
        final Vector3d point = super.getConnectionPoint();
        if (this.entity.func_184613_cA()) {
            return point;
        }
        final double angle = (this.entity.field_70761_aq - 90) * Mth.DEG_TO_RAD;
        final double perpAngle = angle - Math.PI / 2;
        final boolean sneaking = this.entity.func_225608_bj_();
        final double perpDist = 0.4 * (this.matchesStack(this.entity.func_184614_ca()) ? 1 : -1);
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
        return point.func_72441_c(dx, dy, dz);
    }

    @Override
    public boolean isMoving() {
        return true;
    }

    @Override
    public boolean update() {
        if (!this.hasNoConnections() && !this.matchesStack(this.entity.func_184614_ca()) && !this.matchesStack(this.entity.func_184592_cb())) {
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
    public void resistSnap(final Vector3d from) {
        final double dist = this.getConnectionPoint().func_72438_d(from);
        if (dist > Connection.MAX_LENGTH) {
            final double dx = this.entity.func_226277_ct_() - from.field_72450_a;
            final double dy = this.entity.func_226278_cu_() - from.field_72448_b;
            final double dz = this.entity.func_226281_cx_() - from.field_72449_c;
            final double vectorX = dx / dist;
            final double vectorY = dy / dist;
            final double vectorZ = dz / dist;
            final double factor = Math.min((dist - Connection.MAX_LENGTH) / Connection.PULL_RANGE, Connection.PULL_RANGE);
            final Vector3d motion = this.entity.func_213322_ci();
            final double tangent = Math.cos(MathHelper.func_181159_b(dy, Math.sqrt(dx * dx + dz * dz))) * Math.signum(motion.field_72448_b);
            final double speed = motion.func_72433_c();
            final double swing = Math.abs(speed) < 1e-6 ? 0 : (1 - Math.abs(motion.field_72448_b / speed - tangent)) * 0.1;
            final double mag = Math.sqrt(motion.field_72450_a * motion.field_72450_a + tangent * tangent + motion.field_72449_c * motion.field_72449_c);
            final double arcX;
            final double arcY;
            final double arcZ;
            if (dy > 0 || Math.abs(mag) < 1e-6) {
                arcX = arcY = arcZ = 0;
            } else {
                arcX = motion.field_72450_a / mag * swing;
                arcY = tangent / mag * swing;
                arcZ = motion.field_72449_c / mag * swing;
            }
            this.entity.func_213293_j(
                motion.field_72450_a + vectorX * -Math.abs(vectorX) * factor + arcX,
                motion.field_72448_b + vectorY * -Math.abs(vectorY) * factor + arcY,
                motion.field_72449_c + vectorZ * -Math.abs(vectorZ) * factor + arcZ
            );
            this.entity.field_70143_R = 0;
            if (this.entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) this.entity).field_71135_a.func_147359_a(new SEntityVelocityPacket(this.entity));
            }
        }
    }

    @Override
    public PlayerFastenerAccessor createAccessor() {
        return new PlayerFastenerAccessor(this);
    }
}
