package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.*;
import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.connection.type.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.*;
import java.util.*;

public abstract class EntityFastenerAccessor<E extends Entity> implements FastenerAccessor {
    private final Class<? extends E> entityClass;

    private UUID uuid;

    @Nullable
    private E entity;

    public EntityFastenerAccessor(final Class<? extends E> entityClass) {
        this(entityClass, (UUID) null);
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final EntityFastener<E> fastener) {
        this(entityClass, fastener.getEntity().getUniqueID());
        this.entity = fastener.getEntity();
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final UUID uuid) {
        this.entityClass = entityClass;
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public E getEntity() {
        return this.entity;
    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    protected boolean equalsUUID(final Entity entity) {
        return this.uuid.equals(entity.getUniqueID());
    }

    @Override
    public Fastener<?> get(final World world) {
        // FIXME
        return this.entity.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
    }

    @Override
    public boolean isLoaded(final World world) {
        return this.entity != null && this.entity.isAlive();
    }

    @Override
    public boolean exists(final World world) {
        return this.entity == null || this.entity.isAlive();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EntityFastenerAccessor<?>) {
            return this.uuid.equals(((EntityFastenerAccessor<?>) obj).uuid);
        }
        return false;
    }

    @Override
    public void update(final World world, final BlockPos pos) {
        if (this.entity == null) {
            final AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(Connection.MAX_LENGTH);
            final List<E> nearEntities = world.getEntitiesWithinAABB(this.entityClass, aabb);
            for (final E entity : nearEntities) {
                if (this.equalsUUID(entity)) {
                    this.entity = entity;
                    break;
                }
            }
        }
    }

    @Override
    public CompoundNBT serialize() {
        return NBTUtil.writeUniqueId(this.uuid);
    }

    @Override
    public void deserialize(final CompoundNBT nbt) {
        this.uuid = NBTUtil.readUniqueId(nbt);
        this.entity = null;
    }
}
