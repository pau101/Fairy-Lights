package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.PlayerFastener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public final class PlayerFastenerAccessor extends EntityFastenerAccessor<PlayerEntity> {
    public PlayerFastenerAccessor() {
        super(PlayerEntity.class);
    }

    public PlayerFastenerAccessor(final PlayerFastener fastener) {
        super(PlayerEntity.class, fastener);
    }

    @Override
    protected boolean equalsUUID(final Entity entity) {
        if (super.equalsUUID(entity)) {
            return true;
        }
        if (entity instanceof PlayerEntity) {
            return PlayerEntity.getUUID(((PlayerEntity) entity).getGameProfile()).equals(this.getUUID());
        }
        return false;
    }

    @Override
    public FastenerType getType() {
        return FastenerType.PLAYER;
    }
}
