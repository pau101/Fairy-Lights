package com.pau101.fairylights.server.block.entity;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockEntityFastener extends TileEntity implements ITickable {
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getFastener().getBounds().expandXyz(1);
	}

	public EnumFacing getFacing() {
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != FairyLights.fastener) {
			return EnumFacing.UP;
		}
		return state.getValue(BlockFastener.FACING);
	}

	@Override
	public BlockFastener getBlockType() {
		return (BlockFastener) super.getBlockType();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(super.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		IBlockState state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		getFastener().setWorld(world);
	}

	@Override
	public void update() {
		Fastener<?> fastener = getFastener();
		if (!worldObj.isRemote && fastener.hasNoConnections()) {
			worldObj.setBlockToAir(pos);
		} else if (fastener.update()) {
			markDirty();	
			IBlockState state = worldObj.getBlockState(pos);
			worldObj.notifyBlockUpdate(pos, state, state, 3);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		getFastener().remove();
	}

	@Override
	public void onChunkUnload() {
		getFastener().remove();
	}

	private Fastener<?> getFastener() {
		return getCapability(CapabilityHandler.FASTENER_CAP, null);
	}
}
