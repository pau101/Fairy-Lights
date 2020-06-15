package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<CompoundNBT> {
    @Override
    CompoundNBT serializeNBT();

    Optional<Connection> get(final UUID id);

    List<Connection> getOwnConnections();

    List<Connection> getAllConnections();

    default Optional<Connection> getFirstConnection() {
        return this.getAllConnections().stream().findFirst();
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

    default void resistSnap(final Vec3d from) {}

    boolean update();

    void setDirty();

    void dropItems(World world, BlockPos pos);

    void remove();

    boolean hasNoConnections();

    boolean hasConnectionWith(Fastener<?> fastener);

    @Nullable
    Connection getConnectionTo(FastenerAccessor destination);

    boolean removeConnection(UUID uuid);

    boolean removeConnection(Connection connection);

    boolean reconnect(final World world, Connection connection, Fastener<?> newDestination);

    Connection connect(World world, Fastener<?> destination, ConnectionType<?> type, CompoundNBT compound, final boolean drop);

    Connection createOutgoingConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType<?> type, CompoundNBT compound, final boolean drop);

    void createIncomingConnection(World world, UUID uuid, Fastener<?> destination, ConnectionType<?> type);
}
