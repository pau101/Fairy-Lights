package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.entity.EntityFenceFastener;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessorEntity;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessorFence;
import net.minecraft.util.math.BlockPos;

public final class FastenerFence extends FastenerEntity<EntityFenceFastener> {
	public FastenerFence(final EntityFenceFastener entity) {
		super(entity);
	}

	@Override
	public FastenerAccessorEntity<EntityFenceFastener> createAccessor() {
		return new FastenerAccessorFence(this);
	}

	@Override
	public BlockPos getPos() {
		return this.entity.getHangingPosition();
	}

	@Override
	public boolean isMoving() {
		return false;
	}
}
