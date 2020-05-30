package me.paulf.fairylights.server.fastener.accessor;

import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.PlayerFastener;
import net.minecraft.entity.player.PlayerEntity;

public final class PlayerFastenerAccessor extends EntityFastenerAccessor<PlayerEntity> {
    public PlayerFastenerAccessor() {
        super(PlayerEntity.class);
    }

    public PlayerFastenerAccessor(final PlayerFastener fastener) {
        super(PlayerEntity.class, fastener);
    }

    @Override
    public FastenerType getType() {
        return FastenerType.PLAYER;
    }
}
