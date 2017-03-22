package com.pau101.fairylights.server.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.Utils;
import com.pau101.fairylights.util.matrix.Matrix;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public final class EntityLadder extends EntityLivingBase implements IEntityAdditionalSpawnData {
	private static final byte PUNCH_ID = 32;

	private long lastPunchTime;

	public EntityLadder(World world) {
		super(world);
		setSize(1.15F, 2.8F);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(FairyLights.ladder);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slot, ItemStack stack) {}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.RIGHT;
	}

	@Override
	protected SoundEvent getFallSound(int distance) {
		return FLSounds.LADDER_FALL;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return FLSounds.LADDER_HIT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return FLSounds.LADDER_BREAK;
	}

	@Override
	public boolean attackable() {
		return false;
	}

	@Override
	public boolean canBeHitWithPotion() {
		return false;
	}

	public List<AxisAlignedBB> getCollisionSurfaces() {
		Matrix mat = new Matrix();
		mat.rotate(-rotationYaw * Mth.DEG_TO_RAD + Mth.PI, 0, 1, 0);
		List<AxisAlignedBB> bounds = new ArrayList<>();
		final float[][] steps = {
			// steps
			{ 7.3F, -12 }, { 16.3F, -9.7F }, { 25.4F, -7.3F }, { 34.3F, -4.9F },
			// third rod so back is partially solid
			{ 25.9F, 7.3F } };
		final float bs = 3.67F;
		final float bhm = bs / 16 / 3;
		final float bvm = 1 / 16F;
		for (float[] step : steps) {
			for (int b = -1; b <= 1; b++) {
				Vec3d p = mat.transform(new Vec3d(b * bs, step[0], step[1])).scale(1 / 16F);
				bounds.add(new AxisAlignedBB(posX + p.xCoord - bhm, posY + p.yCoord - bvm, posZ + p.zCoord - bhm, posX + p.xCoord + bhm, posY + p.yCoord + bvm, posZ + p.zCoord + bhm));
			}
		}
		final float tym = 43.5F / 16;
		final float thm = 4F / 16;
		bounds.add(new AxisAlignedBB(posX - thm, posY + tym - bvm, posZ - thm, posX + thm, posY + tym + bvm, posZ + thm));
		return bounds;
	}

	@Override
	public String getName() {
		return Utils.getEntityName(this);
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt bolt) {}

	@Override
	public void addVelocity(double x, double y, double z) {}

	@Override
	public void applyEntityCollision(Entity entity) {}

	@Override
	protected void collideWithNearbyEntities() {}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (world.isRemote || isDead || isEntityInvulnerable(source)) {
			return false;
		}
		if (DamageSource.OUT_OF_WORLD == source) {
			setDead();
		} else if (source.isExplosion()) {
			playBreakSound();
			setDead();
		} else if (DamageSource.IN_FIRE == source) {
			if (isBurning()) {
				damage(0.15F);
			} else {
				setFire(5);
			}
		} else if (DamageSource.ON_FIRE == source && getHealth() > 0.5F) {
			damage(4);
		} else if (isPlayerDamage(source)) {
			if (source.isCreativePlayer()) {
				playBreakSound();
				playParticles();
				setDead();
			} else {
				long time = world.getTotalWorldTime();
				if (time - lastPunchTime > 5) {
					world.setEntityState(this, PUNCH_ID);
					lastPunchTime = time;
				} else {
					dropIt();
					playParticles();
					setDead();
				}
			}
		}
		return false;
	}

	private boolean isPlayerDamage(DamageSource source) {
		if ("player".equals(source.getDamageType())) {
			Entity e = source.getEntity();
			return !(e instanceof EntityPlayer) || ((EntityPlayer) e).capabilities.allowEdit;
		}
		return false;
	}

	private void dropIt() {
		Block.spawnAsEntity(world, new BlockPos(this), new ItemStack(FairyLights.ladder));
	}

	private void playBreakSound() {
		world.playSound(null, posX, posY, posZ, getDeathSound(), getSoundCategory(), 1, 1);
	}

	private void playParticles() {
		if (world instanceof WorldServer) {
			((WorldServer) world).spawnParticle(EnumParticleTypes.BLOCK_DUST, posX, posY + height / 1.5, posZ, 18, width / 4, height / 4, width / 4, 0.05, Block.getStateId(Blocks.PLANKS.getDefaultState()));
		}
	}

	private void damage(float amount) {
		float newHealth = getHealth() - amount;
		if (newHealth <= 0.5F) {
			dropIt();
			setDead();
		} else {
			setHealth(newHealth);
		}
	}

	@Override
	public void handleStatusUpdate(byte id) {
		if (id == PUNCH_ID) {
			if (world.isRemote) {
				world.playSound(posX, posY, posZ, getHurtSound(), getSoundCategory(), 0.3F, 1, false);
				lastPunchTime = world.getTotalWorldTime();
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public void onKillCommand() {
		setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		renderYawOffset = rotationYawHead = rotationYaw;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {}

	@Override
	public void readSpawnData(ByteBuf buf) {
		prevRenderYawOffset = prevRotationYaw = renderYawOffset = rotationYawHead = rotationYaw;
	}
}
