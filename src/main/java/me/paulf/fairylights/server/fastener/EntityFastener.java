package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3d;

public abstract class EntityFastener<E extends Entity> extends AbstractFastener<EntityFastenerAccessor<E>> {
    protected final E entity;

    public EntityFastener(final E entity) {
        this.entity = entity;
        this.setWorld(entity.field_70170_p);
    }

    @Override
    public Direction getFacing() {
        return Direction.UP;
    }

    public E getEntity() {
        return this.entity;
    }

    @Override
    public BlockPos getPos() {
        return this.entity.func_233580_cy_();
    }

    @Override
    public Vector3d getConnectionPoint() {
        return this.entity.func_213303_ch();
    }
}
