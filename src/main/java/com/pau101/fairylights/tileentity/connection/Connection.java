package com.pau101.fairylights.tileentity.connection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.pau101.fairylights.connection.ConnectionLogic;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.connection.Light;
import com.pau101.fairylights.connection.PatternLightData;
import com.pau101.fairylights.eggs.Jingle;
import com.pau101.fairylights.eggs.JinglePlayer;
import com.pau101.fairylights.item.LightVariant;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public abstract class Connection {
	private TileEntityConnectionFastener fastener;

	protected World worldObj;

	private boolean isOrigin;

	private Catenary catenary;

	private Catenary prevCatenary;

	protected boolean shouldRecalculateCatenary;

	private BlockPos to;

	private BlockPos from;

	private boolean isDirty;

	private final ConnectionType type;

	private final ConnectionLogic logic;

	public Connection(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj) {
		this(type, fairyLightsFastener, worldObj, false, null);
	}

	public Connection(ConnectionType type, TileEntityConnectionFastener fastener, World worldObj, boolean isOrigin, NBTTagCompound compound) {
		this.type = type;
		this.fastener = fastener;
		setWorldObj(worldObj);
		this.isOrigin = isOrigin;
		shouldRecalculateCatenary = true;
		logic = type.createLogic(this);
		if (compound != null) {
			readDetailsFromNBT(compound);
		}
	}

	public Catenary getCatenary() {
		return catenary;
	}

	public Catenary getPrevCatenary() {
		return prevCatenary;
	}

	public final ConnectionType getType() {
		return type;
	}

	public final ConnectionLogic getLogic() {
		return logic;
	}

	public abstract Point3f getTo();

	public abstract BlockPos getToBlock();

	public void setWorldObj(World worldObj) {
		this.worldObj = worldObj;
	}

	public World getWorldObj() {
		return worldObj;
	}

	public boolean isOrigin() {
		return isOrigin;
	}

	public boolean shouldRecalculateCatenery() {
		return shouldRecalculateCatenary;
	}

	public TileEntityConnectionFastener getFastener() {
		return fastener;
	}

	public void onRemove() {}

	public abstract boolean shouldDisconnect();

	public void update(Point3f from) {
		prevCatenary = catenary;
		logic.onUpdate();
		if (shouldRecalculateCatenary) {
			Point3f to = getTo();
			if (to == null) {
				return;
			}
			to.sub(from);
			if (to.x == 0 && to.y == 0 && to.z == 0) {
				return;
			}
			this.from = fastener.getPos();
			this.to = getToBlock();
			catenary = logic.createCatenary(to);
			shouldRecalculateCatenary = false;
			logic.onRecalculateCatenary();
		}
		logic.onUpdateEnd();
	}

	public void writeDetailsToNBT(NBTTagCompound compound) {
		logic.writeToNBT(compound);
	}

	public void readDetailsFromNBT(NBTTagCompound compound) {
		logic.readFromNBT(compound);
	}

	public void readFromNBT(NBTTagCompound compound) {
		isOrigin = compound.getBoolean("isOrigin");
		isDirty = compound.getBoolean("isDirty");
		readDetailsFromNBT(compound);
	}

	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isOrigin", isOrigin);
		compound.setBoolean("isDirty", isDirty);
		writeDetailsToNBT(compound);
	}
}