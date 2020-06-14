package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.EntityFastener;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntityFastenerAccessor<E extends Entity> implements FastenerAccessor {
    private final Class<? extends E> entityClass;

    private UUID uuid;

    @Nullable
    private E entity;

    @Nullable
    private Vec3d pos;

    public EntityFastenerAccessor(final Class<? extends E> entityClass) {
        this(entityClass, (UUID) null);
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final EntityFastener<E> fastener) {
        this(entityClass, fastener.getEntity().getUniqueID());
        this.entity = fastener.getEntity();
        this.pos = this.entity.getPositionVec();
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final UUID uuid) {
        this.entityClass = entityClass;
        this.uuid = uuid;
    }

    @Override
    public LazyOptional<Fastener<?>> get(final World world, final boolean load) {
        if (this.entity == null) {
            if (world instanceof ServerWorld) {
                final Entity e = ((ServerWorld) world).getEntityByUuid(this.uuid);
                if (this.entityClass.isInstance(e)) {
                    this.entity = this.entityClass.cast(e);
                }
            } else if (this.pos != null) {
                for (final E entity : world.getLoadedEntitiesWithinAABB(this.entityClass, new AxisAlignedBB(this.pos.subtract(1.0D, 1.0D, 1.0D), this.pos.add(1.0D, 1.0D, 1.0D)))) {
                    if (this.uuid.equals(entity.getUniqueID())) {
                        this.entity = entity;
                        break;
                    }
                }
            }
        }
        if (this.entity != null) {
            this.pos = this.entity.getPositionVec();
            return this.entity.getCapability(CapabilityHandler.FASTENER_CAP);
        }
        return LazyOptional.empty();
    }

    @Override
    public boolean exists(final World world) {
        return this.entity == null || this.entity.getCapability(CapabilityHandler.FASTENER_CAP).isPresent();
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
    public CompoundNBT serialize() {
        final CompoundNBT tag = new CompoundNBT();
        tag.putUniqueId("UUID", this.uuid);
        if (this.pos != null) {
            final ListNBT pos = new ListNBT();
            pos.add(DoubleNBT.valueOf(this.pos.x));
            pos.add(DoubleNBT.valueOf(this.pos.y));
            pos.add(DoubleNBT.valueOf(this.pos.z));
            tag.put("Pos", pos);
        }
        return tag;
    }

    @Override
    public void deserialize(final CompoundNBT tag) {
        this.uuid = tag.getUniqueId("UUID");
        if (tag.contains("Pos", Constants.NBT.TAG_LIST)) {
            final ListNBT pos = tag.getList("Pos", Constants.NBT.TAG_DOUBLE);
            this.pos = new Vec3d(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        } else {
            this.pos = null;
        }
        this.entity = null;
    }
}
