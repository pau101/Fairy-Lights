package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.*;

import javax.annotation.*;
import java.util.*;

public interface Fastener<F extends FastenerAccessor> extends ICapabilitySerializable<CompoundNBT> {
    @Override
    CompoundNBT serializeNBT();

    Map<UUID, Connection> getConnections();

    default Connection getFirstConnection() {
        return this.getConnections().values().stream().findFirst().orElse(null);
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
