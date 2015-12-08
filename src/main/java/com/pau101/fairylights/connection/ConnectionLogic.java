package com.pau101.fairylights.connection;

import net.minecraft.nbt.NBTTagCompound;

import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.vectormath.Point3f;

public abstract class ConnectionLogic {
	private final Connection connection;

	public ConnectionLogic(Connection connection) {
		this.connection = connection;
	}

	public final Connection getConnection() {
		return connection;
	}

	public void onUpdate() {}

	public void onUpdateEnd() {}

	public void onRecalculateCatenary() {}

	public abstract Catenary createCatenary(Point3f to);

	public void writeToNBT(NBTTagCompound compound) {}

	public void readFromNBT(NBTTagCompound compound) {}
}
