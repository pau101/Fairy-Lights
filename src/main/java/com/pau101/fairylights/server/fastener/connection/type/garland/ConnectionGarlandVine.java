package com.pau101.fairylights.server.fastener.connection.type.garland;

import java.util.UUID;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class ConnectionGarlandVine extends Connection {
	public ConnectionGarlandVine(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public ConnectionGarlandVine(World world, Fastener<?> fastener, UUID uuid) {
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
