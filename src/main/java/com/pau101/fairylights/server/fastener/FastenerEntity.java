package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.server.fastener.accessor.FastenerAccessorEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class FastenerEntity<E extends Entity> extends FastenerDefault<FastenerAccessorEntity<E>> {
	protected final E entity;

	public FastenerEntity(E entity) {
		this.entity = entity;
		bounds = new AxisAlignedBB(entity.getPosition());
		setWorld(entity.worldObj);
	}

	@Override
	public EnumFacing getFacing() {
		return EnumFacing.UP;
	}

	public E getEntity() {
		return entity;
	}

	@Override
	public Vec3d getOffsetPoint() {
		return Vec3d.ZERO;
	}

	@Override
	public BlockPos getPos() {
		return new BlockPos(getAbsolutePos());
	}

	@Override
	public Vec3d getAbsolutePos() {
		return new Vec3d(entity.posX, entity.posY, entity.posZ);
	}

	@Override
	public String toString() {
		return entity.getName();
	}
}
