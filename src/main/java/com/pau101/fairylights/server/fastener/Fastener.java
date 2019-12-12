package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<CompoundNBT> {
	@Override
	CompoundNBT serializeNBT();

	Map<UUID, Connection> getConnections();

	default Connection getFirstConnection() {
		return getConnections().values().stream().findFirst().orElse(null);
	}

	AxisAlignedBB getBounds();

	Vec3d getConnectionPoint();

	BlockPos getPos();

	Direction getFacing();

	void setWorld(World world);

	@Nullable
	World getWorld();

	F createAccessor();

	boolean isMoving();

	default void resistSnap(Vec3d from) {}

	boolean update();

	void setDirty();

	boolean shouldDropConnection();

	void dropItems(World world, BlockPos pos);

	void remove();

	boolean hasNoConnections();

	boolean hasConnectionWith(Fastener<?> fastener);

	@Nullable
	Connection getConnectionTo(FastenerAccessor destination);

	boolean removeConnection(UUID uuid);

	boolean removeConnection(Connection connection);

	boolean removeConnectionImmediately(UUID uuid);

	boolean removeConnectionImmediately(Connection connection);

	@Nullable
	Connection reconnect(Fastener<?> oldDestination, Fastener<?> newDestination);

	Connection connectWith(World world, Fastener<?> destination, ConnectionType type, CompoundNBT compound);

	Connection createConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType type, boolean isOrigin, CompoundNBT compound);
}
