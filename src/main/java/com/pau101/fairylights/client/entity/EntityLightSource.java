package com.pau101.fairylights.client.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/*
 * For OptiFine dynamic lights
 */
public final class EntityLightSource extends Entity {
	private static int nextLightId = Short.MIN_VALUE;

	public EntityLightSource(World world) {
		super(world);
		setEntityId(nextLightId--);
		noClip = true;
		addedToChunk = true;
		setSize(0.2F, 0.2F);
	}

	@Override
	protected void entityInit() {}

	@Override
	public boolean isBurning() {
		return true;
	}

	@Override
	public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        // Don't let the game add this entity to chunk entity lists
        chunkCoordX = MathHelper.floor_double(posX / 16);
        chunkCoordY = MathHelper.floor_double(posY / 16);
        chunkCoordZ = MathHelper.floor_double(posZ / 16);
		firstUpdate = false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}
}
