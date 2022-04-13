package me.paulf.fairylights.server.fastener;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.math.Vector3d;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<CompoundTag> {
    @Override
    CompoundTag serializeNBT();

    Optional<Connection> get(final UUID id);

    List<Connection> getOwnConnections();

    List<Connection> getAllConnections();

    default Optional<Connection> getFirstConnection() {
        return this.getAllConnections().stream().findFirst();
    }

    AABB getBounds();

    Vector3d getConnectionPoint();

    BlockPos getPos();

    Direction getFacing();

    void setWorld(Level world);

    @Nullable
    Level getWorld();

    F createAccessor();

    boolean isMoving();

    default void resistSnap(final Vector3d from) {}

    boolean update();

    void setDirty();

    void dropItems(Level world, BlockPos pos);

    void remove();

    boolean hasNoConnections();

    boolean hasConnectionWith(Fastener<?> fastener);

    @Nullable
    Connection getConnectionTo(FastenerAccessor destination);

    boolean removeConnection(UUID uuid);

    boolean removeConnection(Connection connection);

    boolean reconnect(final Level world, Connection connection, Fastener<?> newDestination);

    Connection connect(Level world, Fastener<?> destination, ConnectionType<?> type, CompoundTag compound, final boolean drop);

    Connection createOutgoingConnection(Level world, UUID uuid, Fastener<?> destination, ConnectionType<?> type, CompoundTag compound, final boolean drop);

    void createIncomingConnection(Level world, UUID uuid, Fastener<?> destination, ConnectionType<?> type);
}
