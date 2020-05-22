package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
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
        return this.connections;
    }

    @Override
    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    @Override
    public abstract BlockPos getPos();

    @Override
    public void setWorld(final World world) {
        this.world = world;
        this.connections.values().forEach(c -> c.setWorld(world));
    }

    @Nullable
    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean update() {
        final Iterator<Connection> connectionIterator = this.connections.values().iterator();
        final Vec3d fromOffset = this.getConnectionPoint();
        boolean catenaryChange = false, dataChange = this.isDirty;
        this.isDirty = false;
        while (connectionIterator.hasNext()) {
            final Connection connection = connectionIterator.next();
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
            this.calculateBoundingBox();
        }
        return dataChange;
    }

    @Override
    public void setDirty() {
        this.isDirty = true;
    }

    protected void calculateBoundingBox() {
        final AABBBuilder builder = new AABBBuilder();
        for (final Connection connection : this.connections.values()) {
            final Catenary catenary = connection.getCatenary();
            if (catenary == null) {
                continue;
            }
            final Catenary.SegmentIterator it = catenary.iterator();
            while (it.next()) {
                builder.include(it.getX(0.0F), it.getY(0.0F), it.getZ(0.0F));
                if (!it.hasNext()) {
                    builder.include(it.getX(1.0F), it.getY(1.0F), it.getZ(1.0F));
                }
            }
        }
        this.bounds = builder.add(this.getConnectionPoint()).build();
    }

    @Override
    public boolean shouldDropConnection() {
        return true;
    }

    @Override
    public void dropItems(final World world, final BlockPos pos) {
        final float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
        final float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
        final float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
        for (final Connection connection : this.connections.values()) {
            if (connection.shouldDrop()) {
                final ItemStack stack = connection.getItemStack();
                final ItemEntity entityItem = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack);
                final float scale = 0.05F;
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
        this.connections.values().forEach(Connection::remove);
    }

    @Override
    public boolean hasNoConnections() {
        return this.connections.isEmpty();
    }

    @Override
    public boolean hasConnectionWith(final Fastener<?> fastener) {
        return this.getConnectionTo(fastener.createAccessor()) != null;
    }

    @Nullable
    @Override
    public Connection getConnectionTo(final FastenerAccessor destination) {
        for (final Connection connection : this.connections.values()) {
            if (connection.isDestination(destination)) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public boolean removeConnection(final UUID uuid) {
        return this.removeConnection(this.connections.get(uuid));
    }

    @Override
    public boolean removeConnection(final Connection connection) {
        if (connection != null) {
            connection.forceRemove = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeConnectionImmediately(final UUID uuid) {
        final Connection connection = this.connections.remove(uuid);
        if (connection == null) {
            return false;
        }
        connection.remove();
        this.calculateBoundingBox();
        this.setDirty();
        return true;
    }

    @Override
    public boolean removeConnectionImmediately(final Connection connection) {
        return this.removeConnectionImmediately(connection.getUUID());
    }

    @Override
    public Connection reconnect(final Fastener<?> oldDestination, final Fastener<?> newDestination) {
        for (final Entry<UUID, Connection> entry : this.connections.entrySet()) {
            final Connection connection = entry.getValue();
            if (!connection.getDestination().isLoaded(this.world)) {
                continue;
            }
            if (connection.getDestination().get(this.world).equals(oldDestination)) {
                if (connection.getFastener().equals(newDestination) || newDestination.hasConnectionWith(connection.getFastener())) {
                    return null;
                }
                final UUID uuid = entry.getKey();
                oldDestination.removeConnectionImmediately(uuid);
                connection.setDestination(newDestination);
                final Connection other = newDestination.createConnection(this.world, uuid, this, connection.getType(), !connection.isOrigin(), connection.serializeLogic());
                newDestination.getConnections().put(uuid, other);
                return connection;
            }
        }
        return null;
    }

    @Override
    public Connection connectWith(final World world, final Fastener<?> destination, final ConnectionType type, final CompoundNBT compound) {
        final UUID uuid = MathHelper.getRandomUUID();
        this.connections.put(uuid, this.createConnection(world, uuid, destination, type, true, compound));
        final Connection c = destination.createConnection(world, uuid, this, type, false, compound);
        destination.getConnections().put(uuid, c);
        return c;
    }

    @Override
    public Connection createConnection(final World world, final UUID uuid, final Fastener<?> destination, final ConnectionType type, final boolean isOrigin, final CompoundNBT compound) {
        return type.createConnection(world, this, uuid, destination, isOrigin, compound);
    }

    @Override
    public CompoundNBT serializeNBT() {
        final CompoundNBT compound = new CompoundNBT();
        final ListNBT listConnections = new ListNBT();
        for (final Entry<UUID, Connection> connectionEntry : this.connections.entrySet()) {
            final UUID uuid = connectionEntry.getKey();
            final Connection connection = connectionEntry.getValue();
            final CompoundNBT connectionCompound = new CompoundNBT();
            connectionCompound.put("connection", connection.serialize());
            connectionCompound.putByte("type", (byte) connection.getType().ordinal());
            connectionCompound.put("uuid", NBTUtil.writeUniqueId(uuid));
            listConnections.add(connectionCompound);
        }
        compound.put("connections", listConnections);
        return compound;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound) {
        if (!compound.contains("connections", NBT.TAG_LIST)) {
            return;
        }
        final ListNBT listConnections = compound.getList("connections", NBT.TAG_COMPOUND);
        final List<UUID> nbtUUIDs = new ArrayList<>();
        for (int i = 0; i < listConnections.size(); i++) {
            final CompoundNBT connectionCompound = listConnections.getCompound(i);
            final UUID uuid;
            if (connectionCompound.contains("uuid", NBT.TAG_COMPOUND)) {
                uuid = NBTUtil.readUniqueId(connectionCompound.getCompound("uuid"));
            } else {
                uuid = MathHelper.getRandomUUID();
            }
            nbtUUIDs.add(uuid);
            final Connection connection;
            if (this.connections.containsKey(uuid)) {
                connection = this.connections.get(uuid);
            } else {
                final ConnectionType type = ConnectionType.from(connectionCompound.getByte("type"));
                connection = type.createConnection(this.world, this, uuid);
                this.connections.put(uuid, connection);
            }
            connection.deserialize(connectionCompound.getCompound("connection"));
        }
        final Iterator<Entry<UUID, Connection>> connectionsIter = this.connections.entrySet().iterator();
        while (connectionsIter.hasNext()) {
            final Entry<UUID, Connection> connection = connectionsIter.next();
            if (!nbtUUIDs.contains(connection.getKey())) {
                connectionsIter.remove();
                connection.getValue().remove();
            }
        }
    }

    private final LazyOptional<Fastener<?>> lazyOptional = LazyOptional.of(() -> this);

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final Direction facing) {
        return capability == CapabilityHandler.FASTENER_CAP ? this.lazyOptional.cast() : LazyOptional.empty();
    }
}
