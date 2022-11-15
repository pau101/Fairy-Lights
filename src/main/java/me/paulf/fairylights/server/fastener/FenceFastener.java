package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import net.minecraft.core.BlockPos;

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
        return this.entity.getPos();
    }

    @Override
    public boolean isMoving() {
        return false;
    }
}
