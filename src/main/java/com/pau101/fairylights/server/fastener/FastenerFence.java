package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorEntity;
import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorFence;
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
