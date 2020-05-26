package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.entity.*;
import me.paulf.fairylights.server.fastener.accessor.*;
import net.minecraft.util.math.*;

public final class FenceFastener extends EntityFastener<FenceFastenerEntity> {
    public FenceFastener(final FenceFastenerEntity entity) {
        super(entity);
    }

    @Override
    public EntityFastenerAccessor<FenceFastenerEntity> createAccessor() {
        return new FenceFastenerAccessor(this);
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
