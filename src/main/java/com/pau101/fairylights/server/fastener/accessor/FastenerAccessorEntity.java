package com.pau101.fairylights.server.fastener.accessor;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerEntity;
import com.pau101.fairylights.server.fastener.connection.type.Connection;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FastenerAccessorEntity<E extends Entity> implements FastenerAccessor {
	private Class<? extends E> entityClass;

	private UUID uuid;

	@Nullable
	private E entity;

	public FastenerAccessorEntity(Class<? extends E> entityClass) {
		this(entityClass, (UUID) null);
	}

	public FastenerAccessorEntity(Class<? extends E> entityClass, FastenerEntity<E> fastener) {
		this(entityClass, fastener.getEntity().getUniqueID());
		entity = fastener.getEntity();
	}

	public FastenerAccessorEntity(Class<? extends E> entityClass, UUID uuid) {
		this.entityClass = entityClass;
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return uuid;
	}

	public E getEntity() {
		return entity;
	}

	public boolean hasEntity() {
		return entity != null;
	}

	protected boolean equalsUUID(Entity entity) {
		return uuid.equals(entity.getUniqueID());
	}

	@Override
	public Fastener<?> get(World world) {
		return entity.getCapability(CapabilityHandler.FASTENER_CAP, null);
	}

	@Override
	public boolean isLoaded(World world) {
		return entity != null;
	}

	@Override
	public boolean exists(World world) {
		return entity == null || !entity.isDead;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof FastenerAccessorEntity<?>) {
			return uuid.equals(((FastenerAccessorEntity<?>) obj).uuid);
		}
		return false;
	}

	@Override
	public void update(World world, BlockPos pos) {
		if (entity == null) {
			AxisAlignedBB aabb = new AxisAlignedBB(pos).expandXyz(Connection.MAX_LENGTH);
			List<E> nearEntities = world.getEntitiesWithinAABB(entityClass, aabb);
			for (E entity : nearEntities) {
				if (equalsUUID(entity)) {
					this.entity = entity;
					break;
				}
			}
		}
	}

	@Override
	public NBTTagCompound serialize() {
		return NBTUtil.createUUIDTag(uuid);
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		uuid = NBTUtil.getUUIDFromTag(nbt);
		entity = null;
	}
}
