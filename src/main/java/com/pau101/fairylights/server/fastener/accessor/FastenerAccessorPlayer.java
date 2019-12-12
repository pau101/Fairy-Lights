package com.pau101.fairylights.server.fastener.accessor;

import com.pau101.fairylights.server.fastener.FastenerPlayer;
import com.pau101.fairylights.server.fastener.FastenerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public final class FastenerAccessorPlayer extends FastenerAccessorEntity<PlayerEntity> {
	public FastenerAccessorPlayer() {
		super(PlayerEntity.class);
	}

	public FastenerAccessorPlayer(FastenerPlayer fastener) {
		super(PlayerEntity.class, fastener);
	}

	@Override
	protected boolean equalsUUID(Entity entity) {
		if (super.equalsUUID(entity)) {
			return true;
		}
		if (entity instanceof PlayerEntity) {
			return PlayerEntity.getUUID(((PlayerEntity) entity).getGameProfile()).equals(getUUID());
		}
		return false;
	}

	@Override
	public FastenerType getType() {
		return FastenerType.PLAYER;
	}
}
