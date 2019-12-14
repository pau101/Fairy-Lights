package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.EntityFastener;
import me.paulf.fairylights.server.fastener.FastenerType;

public final class FenceFastenerAccessor extends EntityFastenerAccessor<FenceFastenerEntity> {
	public FenceFastenerAccessor() {
		super(FenceFastenerEntity.class);
	}

	public FenceFastenerAccessor(EntityFastener<FenceFastenerEntity> fastener) {
		super(FenceFastenerEntity.class, fastener);
	}

	@Override
	public FastenerType getType() {
		return FastenerType.FENCE;
	}
}
