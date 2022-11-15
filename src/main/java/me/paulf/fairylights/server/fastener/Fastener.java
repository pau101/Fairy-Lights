package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    Vec3 getConnectionPoint();

    BlockPos getPos();

    Direction getFacing();

    void setWorld(Level world);

    @Nullable
    Level getWorld();

    F createAccessor();

    boolean isMoving();

    default void resistSnap(final Vec3 from) {}

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
