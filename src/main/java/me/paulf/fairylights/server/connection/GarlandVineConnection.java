package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.world.level.Level;

import java.util.UUID;

public final class GarlandVineConnection extends Connection {
    public GarlandVineConnection(final ConnectionType<? extends GarlandVineConnection> type, final Level world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
    }

    @Override
    public float getRadius() {
        return 2.5F / 16.0F;
    }
}
