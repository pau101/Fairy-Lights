package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.BlockFastener;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class BlockEntityFastener extends TileEntity implements ITickableTileEntity {
	public BlockEntityFastener() {
		super(FLBlockEntities.FASTENER.orElseThrow(IllegalStateException::new));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getFastener().getBounds().grow(1);
	}

	public Vec3d getOffset() {
		return FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getOffset(getFacing(), 0.125F);
	}

	public Direction getFacing() {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != FLBlocks.FASTENER.orElseThrow(IllegalStateException::new)) {
			return Direction.UP;
		}
		return state.get(BlockFastener.FACING);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(pkt.getNbtCompound());
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		getFastener().setWorld(world);
	}

	@Override
	public void tick() {
		Fastener<?> fastener = getFastener();
		if (!world.isRemote && fastener.hasNoConnections()) {
			world.removeBlock(pos, false);
		} else if (!world.isRemote && fastener.update()) {
			markDirty();
			BlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
		}
	}

	@Override
	public void remove() {
		getFastener().remove();
		super.remove();
	}

	@Override
	public void onChunkUnloaded() {
		getFastener().remove();
	}

	private Fastener<?> getFastener() {
		// FIXME
		return getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
	}
}
