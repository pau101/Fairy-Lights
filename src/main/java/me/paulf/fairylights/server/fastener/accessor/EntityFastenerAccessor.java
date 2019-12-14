package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.EntityFastener;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class EntityFastenerAccessor<E extends Entity> implements FastenerAccessor {
	private Class<? extends E> entityClass;

	private UUID uuid;

	@Nullable
	private E entity;

	public EntityFastenerAccessor(Class<? extends E> entityClass) {
		this(entityClass, (UUID) null);
	}

	public EntityFastenerAccessor(Class<? extends E> entityClass, EntityFastener<E> fastener) {
		this(entityClass, fastener.getEntity().getUniqueID());
		entity = fastener.getEntity();
	}

	public EntityFastenerAccessor(Class<? extends E> entityClass, UUID uuid) {
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
		// FIXME
		return entity.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
	}

	@Override
	public boolean isLoaded(World world) {
		return entity != null && entity.isAlive();
	}

	@Override
	public boolean exists(World world) {
		return entity == null || entity.isAlive();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EntityFastenerAccessor<?>) {
			return uuid.equals(((EntityFastenerAccessor<?>) obj).uuid);
		}
		return false;
	}

	@Override
	public void update(World world, BlockPos pos) {
		if (entity == null) {
			AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(Connection.MAX_LENGTH);
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
	public CompoundNBT serialize() {
		return NBTUtil.writeUniqueId(uuid);
	}

	@Override
	public void deserialize(CompoundNBT nbt) {
		uuid = NBTUtil.readUniqueId(nbt);
		entity = null;
	}
}
