package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import net.minecraft.util.math.BlockPos;

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
        return this.entity.func_174857_n();
    }

    @Override
    public boolean isMoving() {
        return false;
    }
}
