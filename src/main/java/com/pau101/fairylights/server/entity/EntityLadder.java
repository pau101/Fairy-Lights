package com.pau101.fairylights.server.entity;

import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.sound.FLSounds;
import com.pau101.fairylights.util.Mth;
import com.pau101.fairylights.util.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EntityLadder extends LivingEntity implements IEntityAdditionalSpawnData {
	private static final byte PUNCH_ID = 32;

	private long lastPunchTime;

	public EntityLadder(EntityType<? extends EntityLadder> type, World world) {
		super(type, world);
	}

	public EntityLadder(World world) {
		this(FLEntities.LADDER.orElseThrow(IllegalStateException::new), world);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(FLItems.LADDER.orElseThrow(IllegalStateException::new));
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return Collections.emptyList();
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slot, ItemStack stack) {}

	@Override
	public HandSide getPrimaryHand() {
		return HandSide.RIGHT;
	}

	@Override
	protected SoundEvent getFallSound(int distance) {
		return FLSounds.LADDER_FALL.orElseThrow(IllegalStateException::new);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damage) {
		return FLSounds.LADDER_HIT.orElseThrow(IllegalStateException::new);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return FLSounds.LADDER_BREAK.orElseThrow(IllegalStateException::new);
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
		MatrixStack mat = new MatrixStack();
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
				bounds.add(new AxisAlignedBB(posX + p.x - bhm, posY + p.y - bvm, posZ + p.z - bhm, posX + p.x + bhm, posY + p.y + bvm, posZ + p.z + bhm));
			}
		}
		final float tym = 43.5F / 16;
		final float thm = 4F / 16;
		bounds.add(new AxisAlignedBB(posX - thm, posY + tym - bvm, posZ - thm, posX + thm, posY + tym + bvm, posZ + thm));
		return bounds;
	}

	@Override
	public void onStruckByLightning(LightningBoltEntity bolt) {}

	@Override
	public void addVelocity(double x, double y, double z) {}

	@Override
	public void applyEntityCollision(Entity entity) {}

	@Override
	protected void collideWithNearbyEntities() {}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (world.isRemote || removed || isInvulnerableTo(source)) {
			return false;
		}
		if (DamageSource.OUT_OF_WORLD == source) {
			remove();
		} else if (source.isExplosion()) {
			playBreakSound();
			remove();
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
				remove();
			} else {
				long time = world.getGameTime();
				if (time - lastPunchTime > 5) {
					world.setEntityState(this, PUNCH_ID);
					lastPunchTime = time;
				} else {
					dropIt();
					playParticles();
					remove();
				}
			}
		}
		return false;
	}

	private boolean isPlayerDamage(DamageSource source) {
		if ("player".equals(source.getDamageType())) {
			Entity e = source.getTrueSource();
			return !(e instanceof PlayerEntity) || ((PlayerEntity) e).abilities.allowEdit;
		}
		return false;
	}

	private void dropIt() {
		Block.spawnAsEntity(world, new BlockPos(this), new ItemStack(FLItems.LADDER.orElseThrow(IllegalStateException::new)));
	}

	private void playBreakSound() {
		world.playSound(null, posX, posY, posZ, getDeathSound(), getSoundCategory(), 1, 1);
	}

	private void playParticles() {
		if (world instanceof ServerWorld) {
			((ServerWorld) world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), posX, posY + getHeight() / 1.5, posZ, 18, getWidth() / 4, getHeight() / 4, getWidth() / 4, 0.05);
		}
	}

	private void damage(float amount) {
		float newHealth = getHealth() - amount;
		if (newHealth <= 0.5F) {
			dropIt();
			remove();
		} else {
			setHealth(newHealth);
		}
	}

	@Override
	public void handleStatusUpdate(byte id) {
		if (id == PUNCH_ID) {
			if (world.isRemote) {
				world.playSound(posX, posY, posZ, FLSounds.LADDER_HIT.orElseThrow(IllegalStateException::new), getSoundCategory(), 0.3F, 1, false);
				lastPunchTime = world.getGameTime();
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public void onKillCommand() {
		remove();
	}

	@Override
	public void tick() {
		super.tick();
		renderYawOffset = rotationYawHead = rotationYaw;
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		prevRenderYawOffset = prevRotationYaw = renderYawOffset = rotationYawHead = rotationYaw;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
