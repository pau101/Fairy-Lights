package com.pau101.fairylights.server.fastener.accessor;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.FastenerEntity;
import com.pau101.fairylights.server.fastener.FastenerType;

public final class FastenerAccessorFence extends FastenerAccessorEntity<EntityFenceFastener> {
	public FastenerAccessorFence() {
		super(EntityFenceFastener.class);
	}

	public FastenerAccessorFence(FastenerEntity<EntityFenceFastener> fastener) {
		super(EntityFenceFastener.class, fastener);
	}

	@Override
	public FastenerType getType() {
		return FastenerType.FENCE;
	}
}
