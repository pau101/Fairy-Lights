package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class EntityFastener<E extends Entity> extends AbstractFastener<EntityFastenerAccessor<E>> {
    protected final E entity;

    public EntityFastener(final E entity) {
        this.entity = entity;
        this.setWorld(entity.level());
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
        return this.entity.blockPosition();
    }

    @Override
    public Vec3 getConnectionPoint() {
        return this.entity.position();
    }
}
