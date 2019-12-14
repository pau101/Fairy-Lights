package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BlockFastener extends AbstractFastener {
	private final FastenerBlockEntity fastener;

	private final BlockView view;

	public BlockFastener(final FastenerBlockEntity fastener, final BlockView view) {
		this.fastener = fastener;
		this.view = view;
		this.bounds = new AxisAlignedBB(fastener.getPos());
		this.setWorld(fastener.getWorld());
	}

	@Override
	public Direction getFacing() {
		return this.fastener.getFacing();
	}

	@Override
	public boolean isMoving() {
		return this.view.isMoving(this.getWorld(), this.fastener.getPos());
	}

	@Override
	public BlockPos getPos() {
		return this.fastener.getPos();
	}

	@Override
	public Vec3d getConnectionPoint() {
		return this.view.getPosition(this.getWorld(), this.fastener.getPos(), new Vec3d(this.getPos()).add(this.fastener.getOffset()));
	}

	@Override
	public BlockFastenerAccessor createAccessor() {
		return new BlockFastenerAccessor(this);
	}
}
