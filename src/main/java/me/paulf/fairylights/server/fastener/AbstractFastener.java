package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.AABBBuilder;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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
    private final Map<UUID, Connection> connections = new HashMap<>();

    protected AxisAlignedBB bounds = TileEntity.INFINITE_EXTENT_AABB;

    @Nullable
    private World world;

    private boolean dirty;

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
        final Iterator<Connection> it = this.connections.values().iterator();
        final Vec3d fromOffset = this.getConnectionPoint();
        boolean dirty = this.dirty;
        this.dirty = false;
        while (it.hasNext()) {
            final Connection connection = it.next();
            if (connection.update(fromOffset)) {
                dirty = true;
            }
            if (connection.isRemoved()) {
                dirty = true;
                it.remove();
            }
        }
        if (dirty) {
            this.calculateBoundingBox();
        }
        return dirty;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
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
    public boolean removeConnection(final Connection connection) {
        return this.removeConnection(connection.getUUID());
    }

    @Override
    public Connection reconnect(final Fastener<?> oldDestination, final Fastener<?> newDestination) {
        final Connection connection = this.getConnectionTo(oldDestination.createAccessor());
        if (connection != null) {
            if (connection.getFastener().equals(newDestination) || newDestination.hasConnectionWith(connection.getFastener())) {
                return null;
            }
            final UUID uuid = connection.getUUID();
            oldDestination.removeConnection(uuid);
            connection.setDestination(newDestination);
            connection.setDrop();
            newDestination.createConnection(this.world, uuid, this, connection.getType(), !connection.isOrigin(), connection.serializeLogic(), true);
            return connection;
        }
        return null;
    }

    @Override
    public Connection connectWith(final World world, final Fastener<?> destination, final ConnectionType<?> type, final CompoundNBT compound, final boolean drop) {
        final UUID uuid = MathHelper.getRandomUUID();
        final Connection connection = this.createConnection(world, uuid, destination, type, true, compound, drop);
        destination.createConnection(world, uuid, this, type, false, compound, drop);
        return connection;
    }

    @Override
    public Connection createConnection(final World world, final UUID uuid, final Fastener<?> destination, final ConnectionType<?> type, final boolean isOrigin, final CompoundNBT compound, final boolean drop) {
        final Connection c = type.create(world, this, uuid);
        c.deserialize(destination, isOrigin, compound, drop);
        this.connections.put(uuid, c);
        return c;
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
            connectionCompound.putString("type", RegistryObjects.getName(connection.getType()).toString());
            connectionCompound.putUniqueId("uuid", uuid);
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
            if (connectionCompound.hasUniqueId("uuid")) {
                uuid = connectionCompound.getUniqueId("uuid");
            } else {
                uuid = MathHelper.getRandomUUID();
            }
            nbtUUIDs.add(uuid);
            if (this.connections.containsKey(uuid)) {
                final Connection connection = this.connections.get(uuid);
                connection.deserialize(connectionCompound.getCompound("connection"));
            } else {
                final ConnectionType<?> type = FairyLights.CONNECTION_TYPES.getValue(ResourceLocation.tryCreate(connectionCompound.getString("type")));
                if (type != null) {
                    final Connection connection = type.create(this.world, this, uuid);
                    connection.deserialize(connectionCompound.getCompound("connection"));
                    this.connections.put(uuid, connection);
                }
            }
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
