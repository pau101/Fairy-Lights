package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.EntityFastener;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
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
    private Vector3d pos;

    public EntityFastenerAccessor(final Class<? extends E> entityClass) {
        this(entityClass, (UUID) null);
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final EntityFastener<E> fastener) {
        this(entityClass, fastener.getEntity().func_110124_au());
        this.entity = fastener.getEntity();
        this.pos = this.entity.func_213303_ch();
    }

    public EntityFastenerAccessor(final Class<? extends E> entityClass, final UUID uuid) {
        this.entityClass = entityClass;
        this.uuid = uuid;
    }

    @Override
    public LazyOptional<Fastener<?>> get(final World world, final boolean load) {
        if (this.entity == null) {
            if (world instanceof ServerWorld) {
                final Entity e = ((ServerWorld) world).func_217461_a(this.uuid);
                if (this.entityClass.isInstance(e)) {
                    this.entity = this.entityClass.cast(e);
                }
            } else if (this.pos != null) {
                for (final E entity : world.func_225317_b(this.entityClass, new AxisAlignedBB(this.pos.func_178786_a(1.0D, 1.0D, 1.0D), this.pos.func_72441_c(1.0D, 1.0D, 1.0D)))) {
                    if (this.uuid.equals(entity.func_110124_au())) {
                        this.entity = entity;
                        break;
                    }
                }
            }
        }
        if (this.entity != null && this.entity.field_70170_p == world) {
            this.pos = this.entity.func_213303_ch();
            return this.entity.getCapability(CapabilityHandler.FASTENER_CAP);
        }
        return LazyOptional.empty();
    }

    @Override
    public boolean isGone(final World world) {
        return !world.field_72995_K && this.entity != null && (!this.entity.getCapability(CapabilityHandler.FASTENER_CAP).isPresent() || this.entity.field_70170_p != world);
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
        tag.func_186854_a("UUID", this.uuid);
        if (this.pos != null) {
            final ListNBT pos = new ListNBT();
            pos.add(DoubleNBT.func_229684_a_(this.pos.field_72450_a));
            pos.add(DoubleNBT.func_229684_a_(this.pos.field_72448_b));
            pos.add(DoubleNBT.func_229684_a_(this.pos.field_72449_c));
            tag.func_218657_a("Pos", pos);
        }
        return tag;
    }

    @Override
    public void deserialize(final CompoundNBT tag) {
        this.uuid = tag.func_186857_a("UUID");
        if (tag.func_150297_b("Pos", Constants.NBT.TAG_LIST)) {
            final ListNBT pos = tag.func_150295_c("Pos", Constants.NBT.TAG_DOUBLE);
            this.pos = new Vector3d(pos.func_150309_d(0), pos.func_150309_d(1), pos.func_150309_d(2));
        } else {
            this.pos = null;
        }
        this.entity = null;
    }
}
