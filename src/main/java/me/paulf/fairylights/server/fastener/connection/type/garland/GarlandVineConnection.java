package me.paulf.fairylights.server.fastener.connection.type.garland;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public final class GarlandVineConnection extends Connection {
    public GarlandVineConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound, final boolean drop) {
        super(world, fastener, uuid, destination, isOrigin, compound, drop);
    }

    public GarlandVineConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
        super(world, fastener, uuid);
    }

    @Override
    public float getRadius() {
        return 0.1875F;
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.GARLAND;
    }
}
