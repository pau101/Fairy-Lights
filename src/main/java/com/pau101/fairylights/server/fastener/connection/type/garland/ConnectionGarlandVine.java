package com.pau101.fairylights.server.fastener.connection.type.garland;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.util.CubicBezier;

public final class ConnectionGarlandVine extends Connection {
	private static final CubicBezier LENGTH_FUNC = new CubicBezier(0.25F, 0.36F, 0.55F, 0.55F);

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

	@Override
	public Catenary createCatenary(Vec3d to) {
		return Catenary.from(to, LENGTH_FUNC);
	}

	@Override
	public NBTTagCompound serializeLogic() {
		return new NBTTagCompound();
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {}
}
