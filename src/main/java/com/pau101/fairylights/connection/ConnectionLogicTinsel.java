package com.pau101.fairylights.connection;

import net.minecraft.nbt.NBTTagCompound;

import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.mc.EnumDyeColor;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ConnectionLogicTinsel extends ConnectionLogic {
	private EnumDyeColor color;

	public ConnectionLogicTinsel(Connection connection) {
		super(connection);
		color = EnumDyeColor.SILVER;
	}

	public int getColor() {
		return color.getMapColor().colorValue;
	}

	@Override
	public Catenary createCatenary(Point3f to) {
		return Catenary.from(new Vector3f(to), false);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger("color", color.getDyeDamage());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
	}
}
