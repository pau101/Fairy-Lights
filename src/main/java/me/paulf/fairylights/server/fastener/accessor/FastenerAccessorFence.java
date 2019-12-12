package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.entity.EntityFenceFastener;
import me.paulf.fairylights.server.fastener.FastenerEntity;
import me.paulf.fairylights.server.fastener.FastenerType;

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
