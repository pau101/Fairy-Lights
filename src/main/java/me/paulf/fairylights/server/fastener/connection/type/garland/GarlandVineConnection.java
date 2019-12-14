package me.paulf.fairylights.server.fastener.connection.type.garland;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public final class GarlandVineConnection extends Connection {
	public GarlandVineConnection(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, CompoundNBT compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public GarlandVineConnection(World world, Fastener<?> fastener, UUID uuid) {
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
