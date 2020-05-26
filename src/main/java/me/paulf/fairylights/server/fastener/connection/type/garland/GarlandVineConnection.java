package me.paulf.fairylights.server.fastener.connection.type.garland;

import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

import java.util.*;

public final class GarlandVineConnection extends Connection {
    public GarlandVineConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
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
