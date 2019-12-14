package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.Segment;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.AABBBuilder;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public abstract class AbstractFastener<F extends FastenerAccessor> implements Fastener<F> {
	protected Map<UUID, Connection> connections = new HashMap<>();

	protected AxisAlignedBB bounds = TileEntity.INFINITE_EXTENT_AABB;

	@Nullable
	private World world;

	private boolean isDirty;

	@Override
	public Map<UUID, Connection> getConnections() {
		return connections;
	}

	@Override
	public AxisAlignedBB getBounds() {
		return bounds;
	}

	@Override
	public abstract BlockPos getPos();

	@Override
	public void setWorld(World world) {
		this.world = world;
		connections.values().forEach(c -> c.setWorld(world));
	}

	@Nullable
	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public boolean update() {
		Iterator<Connection> connectionIterator = connections.values().iterator();
		Vec3d fromOffset = getConnectionPoint();
		boolean catenaryChange = false, dataChange = isDirty;
		isDirty = false;
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			connection.update(fromOffset);
			catenaryChange |= connection.pollCateneryUpdate();
			dataChange |= connection.pollDataUpdate();
			if (connection.shouldDisconnect()) {
				catenaryChange = dataChange = true;
				connection.remove();
				connectionIterator.remove();
			}
		}
		if (catenaryChange) {
			calculateBoundingBox();
		}
		return dataChange;
	}

	@Override
	public void setDirty() {
		isDirty = true;
	}

	protected void calculateBoundingBox() {
		AABBBuilder builder = new AABBBuilder();
		for (Connection connection : connections.values()) {
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			Segment[] segments = catenary.getSegments();
			for (int i = 0; i < segments.length; i++) {
				Segment segment = segments[i];
				builder.include(segment.getStart().scale(0.0625));
			}
			builder.include(segments[segments.length - 1].getEnd().scale(0.0625));
		}
		bounds = builder.add(getConnectionPoint()).build();
	}

	@Override
	public boolean shouldDropConnection() {
		return true;
	}

	@Override
	public void dropItems(World world, BlockPos pos) {
		float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
		for (Connection connection : connections.values()) {
			if (connection.shouldDrop()) {
				ItemStack stack = connection.getItemStack();
				ItemEntity entityItem = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack);
				float scale = 0.05F;
				entityItem.setMotion(
					world.rand.nextGaussian() * scale,
					world.rand.nextGaussian() * scale + 0.2F,
					world.rand.nextGaussian() * scale
				);
				world.addEntity(entityItem);
			}
		}
	}

	@Override
	public void remove() {
		connections.values().forEach(Connection::remove);
	}

	@Override
	public boolean hasNoConnections() {
		return connections.isEmpty();
	}

	@Override
	public boolean hasConnectionWith(Fastener<?> fastener) {
		return getConnectionTo(fastener.createAccessor()) != null;
	}

	@Nullable
	@Override
	public Connection getConnectionTo(FastenerAccessor destination) {
		for (Connection connection : connections.values()) {
			if (connection.isDestination(destination)) {
				return connection;
			}
		}
		return null;
	}

	@Override
	public boolean removeConnection(UUID uuid) {
		return removeConnection(connections.get(uuid));
	}

	@Override
	public boolean removeConnection(Connection connection) {
		if (connection != null) {
			connection.forceRemove = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean removeConnectionImmediately(UUID uuid) {
		Connection connection = connections.remove(uuid);
		if (connection == null) {
			return false;
		}
		connection.remove();
		calculateBoundingBox();
		setDirty();
		return true;
	}

	@Override
	public boolean removeConnectionImmediately(Connection connection) {
		return removeConnectionImmediately(connection.getUUID());
	}

	@Override
	public Connection reconnect(Fastener<?> oldDestination, Fastener<?> newDestination) {
		for (Entry<UUID, Connection> entry : connections.entrySet()) {
			Connection connection = entry.getValue();
			if (!connection.getDestination().isLoaded(world)) {
				continue;
			}
			if (connection.getDestination().get(world).equals(oldDestination)) {
				if (connection.getFastener().equals(newDestination) || newDestination.hasConnectionWith(connection.getFastener())) {
					return null;
				}
				UUID uuid = entry.getKey();
				oldDestination.removeConnectionImmediately(uuid);
				connection.setDestination(newDestination);
				Connection other = newDestination.createConnection(world, uuid, this, connection.getType(), !connection.isOrigin(), connection.serializeLogic());
				newDestination.getConnections().put(uuid, other);
				return connection;
			}
		}
		return null;
	}

	@Override
	public Connection connectWith(World world, Fastener<?> destination, ConnectionType type, CompoundNBT compound) {
		UUID uuid = MathHelper.getRandomUUID();
		connections.put(uuid, createConnection(world, uuid, destination, type, true, compound));
		Connection c = destination.createConnection(world, uuid, this, type, false, compound);
		destination.getConnections().put(uuid, c);
		return c;
	}

	@Override
	public Connection createConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType type, boolean isOrigin, CompoundNBT compound) {
		return type.createConnection(world, this, uuid, destination, isOrigin, compound);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		ListNBT listConnections = new ListNBT();
		for (Entry<UUID, Connection> connectionEntry : connections.entrySet()) {
			UUID uuid = connectionEntry.getKey();
			Connection connection = connectionEntry.getValue();
			CompoundNBT connectionCompound = new CompoundNBT();
			connectionCompound.put("connection", connection.serialize());
			connectionCompound.putByte("type", (byte) connection.getType().ordinal());
			connectionCompound.put("uuid", NBTUtil.writeUniqueId(uuid));
			listConnections.add(connectionCompound);
		}
		compound.put("connections", listConnections);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		if (!compound.contains("connections", NBT.TAG_LIST)) {
			return;
		}
		ListNBT listConnections = compound.getList("connections", NBT.TAG_COMPOUND);
		List<UUID> nbtUUIDs = new ArrayList<>();
		for (int i = 0; i < listConnections.size(); i++) {
			CompoundNBT connectionCompound = listConnections.getCompound(i);
			UUID uuid;
			if (connectionCompound.contains("uuid", NBT.TAG_COMPOUND)) {
				uuid = NBTUtil.readUniqueId(connectionCompound.getCompound("uuid"));
			} else {
				uuid = MathHelper.getRandomUUID();
			}
			nbtUUIDs.add(uuid);
			Connection connection;
			if (connections.containsKey(uuid)) {
				connection = connections.get(uuid);
			} else {
				ConnectionType type = ConnectionType.from(connectionCompound.getByte("type"));
				connection = type.createConnection(world, this, uuid);
				connections.put(uuid, connection);
			}
			connection.deserialize(connectionCompound.getCompound("connection"));
		}
		Iterator<Entry<UUID, Connection>> connectionsIter = connections.entrySet().iterator();
		while (connectionsIter.hasNext()) {
			Entry<UUID, Connection> connection = connectionsIter.next();
			if (!nbtUUIDs.contains(connection.getKey())) {
				connectionsIter.remove();
				connection.getValue().remove();
			}
		}
	}

	private final LazyOptional<Fastener<?>> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		return capability == CapabilityHandler.FASTENER_CAP ? lazyOptional.cast() : LazyOptional.empty();
	}
}
