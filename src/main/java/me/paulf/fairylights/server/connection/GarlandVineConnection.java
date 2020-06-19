package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.world.World;

import java.util.UUID;

public final class GarlandVineConnection extends Connection {
    public GarlandVineConnection(final ConnectionType<? extends GarlandVineConnection> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
    }

    @Override
    public float getRadius() {
        return 2.5F / 16.0F;
    }
}
