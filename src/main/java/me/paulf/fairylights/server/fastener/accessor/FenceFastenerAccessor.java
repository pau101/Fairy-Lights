package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.entity.*;
import me.paulf.fairylights.server.fastener.*;

public final class FenceFastenerAccessor extends EntityFastenerAccessor<FenceFastenerEntity> {
    public FenceFastenerAccessor() {
        super(FenceFastenerEntity.class);
    }

    public FenceFastenerAccessor(final EntityFastener<FenceFastenerEntity> fastener) {
        super(FenceFastenerEntity.class, fastener);
    }

    @Override
    public FastenerType getType() {
        return FastenerType.FENCE;
    }
}
