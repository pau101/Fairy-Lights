package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.fastener.accessor.EntityFastenerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class EntityFastener<E extends Entity> extends AbstractFastener<EntityFastenerAccessor<E>> {
	protected final E entity;

	public EntityFastener(E entity) {
		this.entity = entity;
		bounds = new AxisAlignedBB(entity.getPosition());
		setWorld(entity.world);
	}

	@Override
	public Direction getFacing() {
		return Direction.UP;
	}

	public E getEntity() {
		return entity;
	}

	@Override
	public BlockPos getPos() {
		return new BlockPos(entity);
	}

	@Override
	public Vec3d getConnectionPoint() {
		return new Vec3d(entity.posX, entity.posY, entity.posZ);
	}
}
