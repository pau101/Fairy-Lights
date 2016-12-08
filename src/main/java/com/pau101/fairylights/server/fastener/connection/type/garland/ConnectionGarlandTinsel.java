package com.pau101.fairylights.server.fastener.connection.type.garland;

import java.util.UUID;

import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.item.ItemLight;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ConnectionGarlandTinsel extends Connection {
	private EnumDyeColor color;

	public ConnectionGarlandTinsel(World world, Fastener<?> fastener, UUID uuid, Fastener<?> destination, boolean isOrigin, NBTTagCompound compound) {
		super(world, fastener, uuid, destination, isOrigin, compound);
	}

	public ConnectionGarlandTinsel(World world, Fastener<?> fastener, UUID uuid) {
		super(world, fastener, uuid);
		color = EnumDyeColor.SILVER;
	}

	public int getColor() {
		return ItemLight.getColorValue(color);
	}

	@Override
	public float getRadius() {
		return 0.125F;
	}

	@Override
	public ConnectionType getType() {
		return ConnectionType.TINSEL;
	}

	@Override
	public Catenary createCatenary(Vec3d to) {
		return Catenary.from(to, false);
	}

	@Override
	public NBTTagCompound serializeLogic() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setByte("color", (byte) color.getDyeDamage());
		return compound;
	}

	@Override
	public void deserializeLogic(NBTTagCompound compound) {
		color = EnumDyeColor.byDyeDamage(compound.getByte("color"));
	}
}
