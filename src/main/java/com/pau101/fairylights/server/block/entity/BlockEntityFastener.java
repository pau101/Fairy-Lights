package com.pau101.fairylights.server.block.entity;

import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.block.FLBlocks;
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
		return getFastener().getBounds().grow(1);
	}

	public EnumFacing getFacing() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != FLBlocks.FASTENER) {
			return EnumFacing.UP;
		}
		return state.getValue(BlockFastener.FACING);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		getFastener().setWorld(world);
	}

	@Override
	public void update() {
		Fastener<?> fastener = getFastener();
		if (!world.isRemote && fastener.hasNoConnections()) {
			world.setBlockToAir(pos);
		} else if (fastener.update()) {
			markDirty();	
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
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
