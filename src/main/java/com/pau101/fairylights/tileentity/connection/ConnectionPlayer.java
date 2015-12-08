package com.pau101.fairylights.tileentity.connection;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.base.Charsets;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.player.PlayerData;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Point3i;

public class ConnectionPlayer extends Connection {
	private EntityPlayer player;

	private UUID entityUUID;

	public boolean forceRemove;

	private NBTTagCompound details;

	private int prevStretchStage;

	public ConnectionPlayer(TileEntityFairyLightsFastener fairyLightsFastener, World worldObj) {
		super(fairyLightsFastener, worldObj);
	}

	public ConnectionPlayer(TileEntityFairyLightsFastener fairyLightsFastener, World worldObj, EntityPlayer player, NBTTagCompound tagCompound) {
		super(fairyLightsFastener, worldObj, true, tagCompound);
		this.player = player;
		setPlayerUUID(player.getUniqueID());
		forceRemove = false;
	}

	@Override
	public Point3f getTo() {
		if (player == null) {
			return null;
		}
		Point3f point = new Point3f((float) player.posX, (float) player.posY, (float) player.posZ);
		point.x += MathHelper.cos((player.prevRenderYawOffset - 180) / 180 * (float) Math.PI) * 0.4F;
		point.z += MathHelper.sin((player.prevRenderYawOffset - 180) / 180 * (float) Math.PI) * 0.4F;
		point.y += FairyLights.proxy.getCatenaryOffset(player);
		return point;
	}

	@Override
	public int getToX() {
		Point3f to = getTo();
		if (to == null) {
			return 0;
		}
		return MathHelper.floor_float(to.x);
	}

	@Override
	public int getToY() {
		Point3f to = getTo();
		if (to == null) {
			return 0;
		}
		return MathHelper.floor_float(to.y);
	}

	@Override
	public int getToZ() {
		Point3f to = getTo();
		if (to == null) {
			return 0;
		}
		return MathHelper.floor_float(to.z);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		PlayerData data = PlayerData.getPlayerData(player);
		data.setUnknownLastClicked();
	}

	@Override
	public boolean shouldDisconnect() {
		return player != null && player.isDead || forceRemove;
	}

	public void setPlayerUUID(UUID entityUUID) {
		this.entityUUID = entityUUID;
	}

	public UUID getPlayerUUID() {
		return entityUUID;
	}

	@Override
	public void update(Point3f from) {
		shouldRecalculateCatenary = true;
		super.update(from);
		if (player == null) {
			List<EntityPlayer> nearEntities = worldObj.getEntitiesWithinAABB(
				EntityPlayer.class,
				AxisAlignedBB.getBoundingBox(fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord,
					fairyLightsFastener.xCoord + 1, fairyLightsFastener.yCoord + 1, fairyLightsFastener.zCoord + 1).expand(FairyLights.MAX_LENGTH,
					FairyLights.MAX_LENGTH, FairyLights.MAX_LENGTH));
			for (EntityPlayer entity : nearEntities) {
				// should do this differently? An online client has a
				// different player UUID for the player instance on the
				// server...
				if (entity.getUniqueID().equals(getPlayerUUID())
					|| UUID.nameUUIDFromBytes(("OfflinePlayer:" + entity.getGameProfile().getName()).getBytes(Charsets.UTF_8)).equals(getPlayerUUID())) {
					player = entity;
					PlayerData data = PlayerData.getPlayerData(entity);
					data.setLastClicked(fairyLightsFastener.xCoord, fairyLightsFastener.yCoord, fairyLightsFastener.zCoord);
					return;
				}
			}
		} else {
			PlayerData data = PlayerData.getPlayerData(player);
			Point3i point = data.getLastClicked();
			if (details == null) {
				details = new NBTTagCompound();
				writeDetailsToNBT(details);
			}
			if (!worldObj.isRemote) {
				double dist = distance(player, from);
				if (dist - 15 > 0) {
					int stage = (int) (dist - 14.9F);
					if (stage > prevStretchStage) {
						player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, FairyLights.MODID + ":cord.creak", 0.25F, 0.5F + stage / 8F);
					}
					prevStretchStage = stage;
				}
				if (dist > FairyLights.MAX_LENGTH + 5) {
					player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, FairyLights.MODID + ":cord.break", 0.75F, 0.8F);
					forceRemove = true;
					return;
				} else if (dist > FairyLights.MAX_LENGTH) {
					double vectorX = (player.posX - from.x) / dist;
					double vectorY = (player.posY - from.y) / dist;
					double vectorZ = (player.posZ - from.z) / dist;
					player.motionX += vectorX * Math.abs(vectorX) * -0.1;
					player.motionY += vectorY * Math.abs(vectorY) * -0.1;
					player.motionZ += vectorZ * Math.abs(vectorZ) * -0.1;
					player.fallDistance = 0;
					((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
				}
			}
			if (!(point.x == fairyLightsFastener.xCoord && point.y == fairyLightsFastener.yCoord && point.z == fairyLightsFastener.zCoord)
				|| player.getHeldItem() == null || player.getHeldItem().getItem() != Item.getItemFromBlock(FairyLights.fairyLightsFastener)
				|| player.getHeldItem().hasTagCompound() && !player.getHeldItem().getTagCompound().equals(details)) {
				forceRemove = true;
			}
		}
	}

	private float distance(Entity entity, Point3f point) {
		float x = (float) (entity.posX - point.x);
		float y = (float) (entity.posY - point.y);
		float z = (float) (entity.posZ - point.z);
		return MathHelper.sqrt_float(x * x + y * y + z * z);
	}

	private float distancePrev(Entity entity, Point3f point) {
		float x = (float) (entity.prevPosX - point.x);
		float y = (float) (entity.prevPosY - point.y);
		float z = (float) (entity.prevPosZ - point.z);
		return MathHelper.sqrt_float(x * x + y * y + z * z);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setLong("PlayerUUIDMost", getPlayerUUID().getMostSignificantBits());
		compound.setLong("PlayerUUIDLeast", getPlayerUUID().getLeastSignificantBits());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		setPlayerUUID(new UUID(compound.getLong("PlayerUUIDMost"), compound.getLong("PlayerUUIDLeast")));
	}
}
