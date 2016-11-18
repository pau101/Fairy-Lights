package com.pau101.fairylights.server.fastener;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import com.pau101.fairylights.server.fastener.accessor.FastenerAccessor;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<NBTTagCompound> {
	@Override
	NBTTagCompound serializeNBT();

	Map<UUID, Connection> getConnections();

	default Connection getFirstConnection() {
		Iterator<Connection> connections = getConnections().values().iterator();
		if (connections.hasNext()) {
			return connections.next();
		}
		return null;
	}

	AxisAlignedBB getBounds();

	Vec3d getConnectionPoint();

	Vec3d getOffsetPoint();

	BlockPos getPos();

	Vec3d getAbsolutePos();

	EnumFacing getFacing();

	void setWorld(World world);

	@Nullable
	World getWorld();

	F createAccessor();

	boolean isDynamic();

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

	Connection connectWith(World world, Fastener<?> destination, ConnectionType type, NBTTagCompound compound);

	Connection createConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType type, boolean isOrigin, NBTTagCompound compound);
}
