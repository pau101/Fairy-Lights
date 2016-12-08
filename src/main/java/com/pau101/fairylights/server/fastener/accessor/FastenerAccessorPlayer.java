package com.pau101.fairylights.server.fastener.accessor;

import com.pau101.fairylights.server.fastener.FastenerPlayer;
import com.pau101.fairylights.server.fastener.FastenerType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class FastenerAccessorPlayer extends FastenerAccessorEntity<EntityPlayer> {
	public FastenerAccessorPlayer() {
		super(EntityPlayer.class);
	}

	public FastenerAccessorPlayer(FastenerPlayer fastener) {
		super(EntityPlayer.class, fastener);
	}

	@Override
	protected boolean equalsUUID(Entity entity) {
		if (super.equalsUUID(entity)) {
			return true;
		}
		if (entity instanceof EntityPlayer) {
			return EntityPlayer.getUUID(((EntityPlayer) entity).getGameProfile()).equals(getUUID());
		}
		return false;
	}

	@Override
	public FastenerType getType() {
		return FastenerType.PLAYER;
	}
}
