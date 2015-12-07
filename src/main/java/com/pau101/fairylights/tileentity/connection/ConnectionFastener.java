package com.pau101.fairylights.tileentity.connection;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.block.BlockConnectionFastenerFence;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.util.vectormath.Point3f;

public class ConnectionFastener extends Connection {
	private BlockPos toPos;

	public ConnectionFastener(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj) {
		super(type, fairyLightsFastener, worldObj);
	}

	public ConnectionFastener(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj, BlockPos pos, boolean isOrigin, NBTTagCompound compound) {
		super(type, fairyLightsFastener, worldObj, isOrigin, compound);
		toPos = pos;
	}

	@Override
	public Point3f getTo() {
		Block toBlock = worldObj.getBlockState(toPos).getBlock();
		if (!(toBlock instanceof BlockConnectionFastener)) {
			return null;
		}
		Point3f point = ((BlockConnectionFastener) toBlock).getOffsetForData(toBlock instanceof BlockConnectionFastenerFence ? null : (EnumFacing) worldObj.getBlockState(toPos).getValue(BlockConnectionFastener.FACING_PROP), 1 / 8f);
		point.x += toPos.getX();
		point.y += toPos.getY();
		point.z += toPos.getZ();
		return point;
	}

	@Override
	public BlockPos getToBlock() {
		return toPos;
	}

	@Override
	public boolean shouldDisconnect() {
		return worldObj.isBlockLoaded(toPos, false) && !(worldObj.getTileEntity(toPos) instanceof TileEntityConnectionFastener);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("x", toPos.getX());
		compound.setInteger("y", toPos.getY());
		compound.setInteger("z", toPos.getZ());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		toPos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
	}

}