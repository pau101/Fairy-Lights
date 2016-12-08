package com.pau101.fairylights.server.entity;

import java.io.IOException;

import javax.annotation.Nullable;

import com.google.common.base.Throwables;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public final class EntityFenceFastener extends EntityHanging implements IEntityAdditionalSpawnData {
	public EntityFenceFastener(World world) {
		super(world);
	}

	public EntityFenceFastener(World world, BlockPos pos) {
		super(world);
		setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int getWidthPixels() {
		return 9;
	}

	@Override
	public int getHeightPixels() {
		return 9;
	}

	@Override
	public float getEyeHeight() {
		/*
		 * Because this entity is inside of a block when
		 * EntityLivingBase#canEntityBeSeen performs its
		 * raytracing it will always return false during
		 * NetHandlerPlayServer#processUseEntity, making
		 * the player reach distance be limited at three
		 * blocks as opposed to the standard six blocks.
		 * EntityLivingBase#canEntityBeSeen will add the
		 * value given by getEyeHeight to the y position
		 * of the entity to calculate the end point from
		 * which to raytrace to. Returning one lets most
		 * interactions with a player succeed, typically
		 * for breaking the connection or creating a new
		 * connection. I hope you enjoy my line lengths.
		 */
		return 1;
	}

	@Override
	public float getBrightness(float delta) {
		BlockPos pos = new BlockPos(this);
		if (worldObj.isBlockLoaded(pos)) {
			return worldObj.getLightBrightness(pos);
		}
		return 0;
	}

	@Override
	public int getBrightnessForRender(float delta) {
		BlockPos pos = new BlockPos(this);
		if (worldObj.isBlockLoaded(pos)) {
			return worldObj.getCombinedLight(pos, 0);
		}
		return 0;
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public boolean onValidSurface() {
		return ItemConnection.isFence(worldObj.getBlockState(hangingPosition));
	}

	@Override
	public void setDead() {
		super.setDead();
		getFastener().remove();
	}

	@Override
	public void onBroken(@Nullable Entity breaker) {
		getFastener().dropItems(worldObj, hangingPosition);
		if (breaker != null) {
			worldObj.playEvent(2001, hangingPosition, Block.getStateId(FairyLights.fastener.getDefaultState()));	
		}
	}

	@Override
	public void playPlaceSound() {
		SoundType sound = FairyLights.fastener.getSoundType(FairyLights.fastener.getDefaultState(), worldObj, getHangingPosition(), null);
		playSound(sound.getPlaceSound(), (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(MathHelper.floor_double(x) + 0.5, MathHelper.floor_double(y) + 0.5, MathHelper.floor_double(z) + 0.5);
	}

	@Override
	public void updateFacingWithBoundingBox(EnumFacing facing) {}

	@Override
	protected void updateBoundingBox() {
		posX = hangingPosition.getX() + 0.5;
		posY = hangingPosition.getY() + 0.5;
		posZ = hangingPosition.getZ() + 0.5;
		final float w = 3 / 16F;
		final float h = 3 / 16F;
		setEntityBoundingBox(new AxisAlignedBB(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getFastener().getBounds().expandXyz(1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		Fastener<?> fastener = getFastener();
		if (!worldObj.isRemote && fastener.hasNoConnections()) {
			setDead();
			onBroken(null);
		} else if (fastener.update() && !worldObj.isRemote) {
			MessageUpdateFastenerEntity msg = new MessageUpdateFastenerEntity(this, fastener.serializeNBT());
			ServerProxy.sendToPlayersWatchingEntity(msg, worldObj, this);
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
		if (stack != null && stack.getItem() instanceof ItemConnection) {
			if (worldObj.isRemote) {
				player.swingArm(hand);
			} else {
				((ItemConnection) stack.getItem()).connect(stack, player, worldObj, getFastener());
			}
			return true;
		}
		return super.processInitialInteract(player, stack, hand);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("pos", NBTUtil.createPosTag(hangingPosition));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		hangingPosition = NBTUtil.getPosFromTag(compound.getCompoundTag("pos"));
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		Fastener<?> fastener = getFastener();
		try {
			CompressedStreamTools.write(fastener.serializeNBT(), new ByteBufOutputStream(buf));
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		Fastener<?> fastener = getFastener();
		try {
			fastener.deserializeNBT(CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(0x200000)));
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	private Fastener<?> getFastener() {
		return getCapability(CapabilityHandler.FASTENER_CAP, null);
	}

	public static EntityFenceFastener create(World world, BlockPos fence) {
		EntityFenceFastener fastener = new EntityFenceFastener(world, fence);
		fastener.forceSpawn = true;
		world.spawnEntityInWorld(fastener);
		fastener.playPlaceSound();
		return fastener;
	}

	@Nullable
	public static EntityFenceFastener find(World world, BlockPos pos) {
		EntityHanging entity = findHanging(world, pos);
		if (entity instanceof EntityFenceFastener) {
			return (EntityFenceFastener) entity;
		}
		return null;
	}

	@Nullable
	public static EntityHanging findHanging(World world, BlockPos pos) {
		for (EntityHanging e : world.getEntitiesWithinAABB(EntityHanging.class, new AxisAlignedBB(pos).expandXyz(2))) {
			if (e.getHangingPosition().equals(pos)) {
				return e;
			}
		}
		return null;
	}
}
