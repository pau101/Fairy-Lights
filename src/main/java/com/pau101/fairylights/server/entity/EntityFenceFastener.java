package com.pau101.fairylights.server.entity;

import com.google.common.base.Throwables;
import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.block.FLBlocks;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.io.IOException;

public final class EntityFenceFastener extends HangingEntity implements IEntityAdditionalSpawnData {
	private int surfaceCheckTime;

	public EntityFenceFastener(EntityType<? extends EntityFenceFastener> type, World world) {
		super(type, world);
	}

	public EntityFenceFastener(World world) {
		this(FLEntities.FASTENER.orElseThrow(IllegalStateException::new), world);
	}

	public EntityFenceFastener(World world, BlockPos pos) {
		this(world);
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
	public float getEyeHeight(Pose pose, EntitySize size) {
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
	public float getBrightness() {
		BlockPos pos = new BlockPos(this);
		if (world.isBlockLoaded(pos)) {
			return world.getBrightness(pos);
		}
		return 0;
	}

	@Override
	public int getBrightnessForRender() {
		BlockPos pos = new BlockPos(this);
		if (world.isBlockLoaded(pos)) {
			return world.getCombinedLight(pos, 0);
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
		return ItemConnection.isFence(world.getBlockState(hangingPosition));
	}

	@Override
	public void remove() {
		getFastener().remove();
		super.remove();
	}

	@Override
	public void onBroken(@Nullable Entity breaker) {
		getFastener().dropItems(world, hangingPosition);
		if (breaker != null) {
			world.playEvent(2001, hangingPosition, Block.getStateId(FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getDefaultState()));
		}
	}

	@Override
	public void playPlaceSound() {
		SoundType sound = FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getSoundType(FLBlocks.FASTENER.orElseThrow(IllegalStateException::new).getDefaultState(), world, getHangingPosition(), null);
		playSound(sound.getPlaceSound(), (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.BLOCKS;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(MathHelper.floor(x) + 0.5, MathHelper.floor(y) + 0.5, MathHelper.floor(z) + 0.5);
	}

	@Override
	public void updateFacingWithBoundingBox(Direction facing) {}

	@Override
	protected void updateBoundingBox() {
		posX = hangingPosition.getX() + 0.5;
		posY = hangingPosition.getY() + 0.5;
		posZ = hangingPosition.getZ() + 0.5;
		final float w = 3 / 16F;
		final float h = 3 / 16F;
		setBoundingBox(new AxisAlignedBB(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getFastener().getBounds().grow(1);
	}

	@Override
	public void tick() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		Fastener<?> fastener = getFastener();
		if (!world.isRemote && (fastener.hasNoConnections() || checkSurface())) {
			onBroken(null);
			remove();
		} else if (fastener.update() && !world.isRemote) {
			MessageUpdateFastenerEntity msg = new MessageUpdateFastenerEntity(this, fastener.serializeNBT());
			ServerProxy.sendToPlayersWatchingEntity(msg, world, this);
		}
	}

	private boolean checkSurface() {
		if (surfaceCheckTime++ == 100) {
			surfaceCheckTime = 0;
			return !onValidSurface();
		}
		return false;
	}

	@Override
	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() instanceof ItemConnection) {
			if (world.isRemote) {
				player.swingArm(hand);
			} else {
				((ItemConnection) stack.getItem()).connect(stack, player, world, getFastener());
			}
			return true;
		}
		return super.processInitialInteract(player, hand);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		compound.put("pos", NBTUtil.writeBlockPos(hangingPosition));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		hangingPosition = NBTUtil.readBlockPos(compound.getCompound("pos"));
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		Fastener<?> fastener = getFastener();
		try {
			CompressedStreamTools.write(fastener.serializeNBT(), new ByteBufOutputStream(buf));
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		Fastener<?> fastener = getFastener();
		try {
			fastener.deserializeNBT(CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(0x200000)));
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	private Fastener<?> getFastener() {
		// FIXME
		return getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
	}

	public static EntityFenceFastener create(World world, BlockPos fence) {
		EntityFenceFastener fastener = new EntityFenceFastener(world, fence);
		fastener.forceSpawn = true;
		world.addEntity(fastener);
		fastener.playPlaceSound();
		return fastener;
	}

	@Nullable
	public static EntityFenceFastener find(World world, BlockPos pos) {
		HangingEntity entity = findHanging(world, pos);
		if (entity instanceof EntityFenceFastener) {
			return (EntityFenceFastener) entity;
		}
		return null;
	}

	@Nullable
	public static HangingEntity findHanging(World world, BlockPos pos) {
		for (HangingEntity e : world.getEntitiesWithinAABB(HangingEntity.class, new AxisAlignedBB(pos).grow(2))) {
			if (e.getHangingPosition().equals(pos)) {
				return e;
			}
		}
		return null;
	}
}
