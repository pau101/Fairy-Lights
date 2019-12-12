package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorPlayer;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.util.Mth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

public final class FastenerPlayer extends FastenerEntity<PlayerEntity> {
	public FastenerPlayer(PlayerEntity entity) {
		super(entity);
	}

	@Override
	public Vec3d getConnectionPoint() {
		Vec3d point = super.getConnectionPoint();
		if (entity.isElytraFlying()) {
			return point;
		}
		double angle = (entity.renderYawOffset - 90) * Mth.DEG_TO_RAD;
		double perpAngle = angle - Math.PI / 2;
		boolean sneaking = entity.isSneaking();
		double perpDist = 0.4 * (matchesStack(entity.getHeldItemMainhand()) ? 1 : -1);
		double forwardDist;
		double dy;
		if (sneaking) {
			forwardDist = 0;
			dy = 0.6;
		} else {
			forwardDist = 0.2;
			dy = 0.8;
		}
		double dx = Math.cos(perpAngle) * perpDist - Math.cos(angle) * forwardDist;
		double dz = Math.sin(perpAngle) * perpDist - Math.sin(angle) * forwardDist;
		return point.add(dx, dy, dz);
	}

	@Override
	public boolean shouldDropConnection() {
		return false;
	}

	@Override
	public boolean isMoving() {
		return true;
	}

	@Override
	public boolean update() {
		if (!hasNoConnections() && !matchesStack(entity.getHeldItemMainhand()) && !matchesStack(entity.getHeldItemOffhand())) {
			Iterator<Entry<UUID, Connection>> entries = connections.entrySet().iterator(); 
			while (entries.hasNext()) {
				Entry<UUID, Connection> entry = entries.next();
				entries.remove();
				Connection connection = entry.getValue();
				if (connection.getDestination().isLoaded(getWorld())) {
					connection.getDestination().get(getWorld()).removeConnection(entry.getKey());
				}
			}
		}
		return super.update();
	}

	public boolean matchesStack(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemConnection)) {
			return false;
		}
		if (!((ItemConnection) stack.getItem()).getConnectionType().isConnectionThis(getFirstConnection())) {
			return false;
		}
		if (stack.hasTag() && !NBTUtil.areNBTEquals(getFirstConnection().serializeLogic(), stack.getTag(), true)) {
			return false;
		}
		return true;
	}

	@Override
	public void resistSnap(Vec3d from) {
		double dist = getConnectionPoint().distanceTo(from);
		if (dist > Connection.MAX_LENGTH) {
			double dx = entity.posX - from.x;
			double dy = entity.posY - from.y;
			double dz = entity.posZ - from.z;
			double vectorX = dx / dist;
			double vectorY = dy / dist;
			double vectorZ = dz / dist;
			double factor = Math.min((dist - Connection.MAX_LENGTH) / Connection.PULL_RANGE, Connection.PULL_RANGE);
			Vec3d motion = entity.getMotion();
			double tangent = Math.cos(MathHelper.atan2(dy, Math.sqrt(dx * dx + dz * dz))) * Math.signum(motion.y);
			double speed = motion.length();
			double swing = Math.abs(speed) < 1e-6 ? 0 : (1 - Math.abs(motion.y / speed - tangent)) * 0.1;
			double mag = Math.sqrt(motion.x * motion.x + tangent * tangent + motion.z * motion.z);
			double arcX, arcY, arcZ;
			if (dy > 0 || Math.abs(mag) < 1e-6) {
				arcX = arcY = arcZ = 0;
			} else {
				arcX = motion.x / mag * swing;
				arcY = tangent / mag * swing;
				arcZ = motion.z / mag * swing;
			}
			entity.setMotion(
				motion.x + vectorX * -Math.abs(vectorX) * factor + arcX,
				motion.y + vectorY * -Math.abs(vectorY) * factor + arcY,
				motion.z + vectorZ * -Math.abs(vectorZ) * factor + arcZ
			);
			entity.fallDistance = 0;
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity) entity).connection.sendPacket(new SEntityVelocityPacket(entity));
			}
		}
	}

	@Override
	public FastenerAccessorPlayer createAccessor() {
		return new FastenerAccessorPlayer(this);
	}
}
