package com.pau101.fairylights.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.google.common.base.Charsets;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.tileentity.connection.ConnectionFastener;
import com.pau101.fairylights.tileentity.connection.ConnectionPlayer;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.Segment;
import com.pau101.fairylights.util.vectormath.Point3f;

public class TileEntityConnectionFastener extends TileEntity {
	private Map<UUID, Connection> connections;

	private Point3f min, max;

	private Point3f connectionPoint = null;

	public TileEntityConnectionFastener() {
		connections = new HashMap<UUID, Connection>();
		min = new Point3f();
		max = new Point3f(1, 1, 1);
	}

	private void calculateBoundingBox() {
		min = new Point3f();
		max = new Point3f(1, 1, 1);
		for (Connection connection : connections.values()) {
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			Segment[] segments = catenary.getSegments();
			for (int i = 0; i < segments.length; i++) {
				Segment segment = segments[i];
				Point3f vertex = segment.getVertex();
				float x = vertex.x / 16, y = vertex.y / 16, z = vertex.z / 16;
				min.x = x < min.x ? x : min.x;
				min.y = y < min.y ? y : min.y;
				min.z = z < min.z ? z : min.z;
				max.x = x > max.x ? x : max.x;
				max.y = y > max.y ? y : max.y;
				max.z = z > max.z ? z : max.z;
			}
			Point3f vertex = segments[segments.length - 1].pointAt(1);
			float x = vertex.x / 16, y = vertex.y / 16, z = vertex.z / 16;
			min.x = x < min.x ? x : min.x;
			min.y = y < min.y ? y : min.y;
			min.z = z < min.z ? z : min.z;
			max.x = x > max.x ? x : max.x;
			max.y = y > max.y ? y : max.y;
			max.z = z > max.z ? z : max.z;
		}
	}

	public boolean hasConnectionWith(TileEntityConnectionFastener fastener) {
		for (Connection connection : connections.values()) {
			if (connection.getToX() == fastener.xCoord && connection.getToY() == fastener.yCoord && connection.getToZ() == fastener.zCoord) {
				return true;
			}
		}
		return false;
	}

	public void connectWith(EntityPlayer entity, ConnectionType type, NBTTagCompound tagCompound) {
		connections.put(UUID.randomUUID(), new ConnectionPlayer(type, this, worldObj, entity, tagCompound));
	}

	public void connectWith(TileEntityConnectionFastener fairyLightsFastener, ConnectionType type, NBTTagCompound tagCompound) {
		connections.put(UUID.randomUUID(), new ConnectionFastener(type, this, worldObj, fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord, true, tagCompound));
		fairyLightsFastener.connections.put(UUID.randomUUID(), new ConnectionFastener(type, this, worldObj, xCoord, yCoord, zCoord, false, tagCompound));
	}

	public Point3f getConnectionPoint() {
		if (connectionPoint == null) {
			connectionPoint = ((BlockConnectionFastener) getBlockType()).getOffsetForData(getBlockMetadata(), 0.125f).add(xCoord, yCoord, zCoord);
		}
		return connectionPoint;
	}

	public Set<Entry<UUID, Connection>> getConnectionEntrySet() {
		return connections.entrySet();
	}

	public Collection<Connection> getConnections() {
		return connections.values();
	}

	public Connection getConnection(UUID uuid) {
		return connections.get(uuid);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tagCompound);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getBoundingBox();
	}

	public AxisAlignedBB getBoundingBox() {
		return AxisAlignedBB.getBoundingBox(min.x / 16, min.y / 16, min.z / 16, max.x / 16, max.y / 16, max.z / 16).expand(10, 10, 10).offset(xCoord, yCoord, zCoord);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void removeConnection(Entity entity) {
		for (Connection connection : connections.values()) {
			if (connection instanceof ConnectionPlayer) {
				if (entity.getUniqueID().equals(((ConnectionPlayer) connection).getPlayerUUID()) || entity instanceof EntityPlayer && UUID.nameUUIDFromBytes(("OfflinePlayer:" + ((EntityPlayer) entity).getGameProfile().getName()).getBytes(Charsets.UTF_8)).equals(((ConnectionPlayer) connection).getPlayerUUID())) {
					((ConnectionPlayer) connection).forceRemove = true;
				}
			}
		}
	}

	@Override
	public void setWorldObj(World worldObj) {
		super.setWorldObj(worldObj);
		for (Connection connection : connections.values()) {
			connection.setWorldObj(worldObj);
		}
	}

	@Override
	public void updateEntity() {
		if (!(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockConnectionFastener)) {
			return;
		}
		if (!worldObj.isRemote) {
			if (connections.size() == 0) {
				if (getBlockType() == FairyLights.fastenerFence) {
					worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.fence);
				} else {
					worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				}
				return;
			}
		}
		Iterator<Connection> connectionIterator = connections.values().iterator();
		Point3f fromOffset = ((BlockConnectionFastener) getBlockType()).getOffsetForData(worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 0.125f).add(xCoord, yCoord, zCoord);
		boolean update = false, playerUpdateBoundingBox = false;
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			update |= connection.shouldRecalculateCatenery();
			connection.update(fromOffset);
			if (worldObj.isRemote && connection instanceof ConnectionPlayer) {
				playerUpdateBoundingBox = true;
			}
			if (connection.shouldDisconnect()) {
				update = true;
				connection.onRemove();
				connectionIterator.remove();
			}
		}
		if (update || playerUpdateBoundingBox) {
			calculateBoundingBox();
		}
		if (update) {
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList listConnections = new NBTTagList();
		for (Map.Entry<UUID, Connection> connectionEntry : connections.entrySet()) {
			UUID uuid = connectionEntry.getKey();
			Connection connection = connectionEntry.getValue();
			NBTTagCompound connectionCompound = new NBTTagCompound();
			connection.writeToNBT(connectionCompound);
			connectionCompound.setInteger("type", connection.getType().ordinal());
			connectionCompound.setLong("UUIDMost", uuid.getMostSignificantBits());
			connectionCompound.setLong("UUIDLeast", uuid.getLeastSignificantBits());
			listConnections.appendTag(connectionCompound);
		}
		compound.setTag("connections", listConnections);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("connections", 9)) {
			NBTTagList listConnections = compound.getTagList("connections", 10);
			List<UUID> nbtUUIDs = new ArrayList<UUID>();
			for (int i = 0; i < listConnections.tagCount(); i++) {
				NBTTagCompound connectionCompound = listConnections.getCompoundTagAt(i);
				UUID uuid;
				if (connectionCompound.hasKey("UUIDMost", 4) && connectionCompound.hasKey("UUIDLeast", 4)) {
					uuid = new UUID(connectionCompound.getLong("UUIDMost"), connectionCompound.getLong("UUIDLeast"));
				} else {
					uuid = UUID.randomUUID();
				}
				nbtUUIDs.add(uuid);
				if (connections.containsKey(uuid)) {
					continue;
				}
				Connection connection;
				ConnectionType type = ConnectionType.from(connectionCompound.getInteger("type"));
				if (connectionCompound.hasKey("PlayerUUIDMost", 4) && connectionCompound.hasKey("PlayerUUIDLeast", 4)) {
					connection = new ConnectionPlayer(type, this, worldObj);
				} else if (connectionCompound.hasKey("x", 99) && connectionCompound.hasKey("y", 99) && connectionCompound.hasKey("z", 99)) {
					connection = new ConnectionFastener(type, this, worldObj);
				} else {
					continue;
				}
				connection.readFromNBT(connectionCompound);
				connections.put(uuid, connection);
			}
			Iterator<UUID> currentUUIDsIterator = connections.keySet().iterator();
			while (currentUUIDsIterator.hasNext()) {
				UUID uuid = currentUUIDsIterator.next();
				if (!nbtUUIDs.contains(uuid)) {
					currentUUIDsIterator.remove();
				}
			}
		}
	}
}
