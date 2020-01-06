package me.paulf.fairylights.server.entity;

import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
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

public final class LadderEntity extends LivingEntity implements IEntityAdditionalSpawnData {
    private static final byte PUNCH_ID = 32;

    private long lastPunchTime;

    public LadderEntity(final EntityType<? extends LadderEntity> type, final World world) {
        super(type, world);
    }

    public LadderEntity(final World world) {
        this(FLEntities.LADDER.orElseThrow(IllegalStateException::new), world);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(final RayTraceResult target) {
        return new ItemStack(FLItems.LADDER.orElseThrow(IllegalStateException::new));
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemStackFromSlot(final EquipmentSlotType slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(final EquipmentSlotType slot, final ItemStack stack) {}

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    @Override
    protected SoundEvent getFallSound(final int distance) {
        return FLSounds.LADDER_FALL.orElseThrow(IllegalStateException::new);
    }

    @Override
    protected SoundEvent getHurtSound(final DamageSource damage) {
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
        final MatrixStack mat = new MatrixStack();
        mat.rotate(-this.rotationYaw * Mth.DEG_TO_RAD + Mth.PI, 0, 1, 0);
        final List<AxisAlignedBB> bounds = new ArrayList<>();
        final float[][] steps = {
            // steps
            {7.3F, -12}, {16.3F, -9.7F}, {25.4F, -7.3F}, {34.3F, -4.9F},
            // third rod so back is partially solid
            {25.9F, 7.3F}};
        final float bs = 3.67F;
        final float bhm = bs / 16 / 3;
        final float bvm = 1 / 16F;
        for (final float[] step : steps) {
            for (int b = -1; b <= 1; b++) {
                final Vec3d p = mat.transform(new Vec3d(b * bs, step[0], step[1])).scale(1 / 16F);
                bounds.add(new AxisAlignedBB(this.posX + p.x - bhm, this.posY + p.y - bvm, this.posZ + p.z - bhm, this.posX + p.x + bhm, this.posY + p.y + bvm, this.posZ + p.z + bhm));
            }
        }
        final float tym = 43.5F / 16;
        final float thm = 4F / 16;
        bounds.add(new AxisAlignedBB(this.posX - thm, this.posY + tym - bvm, this.posZ - thm, this.posX + thm, this.posY + tym + bvm, this.posZ + thm));
        return bounds;
    }

    @Override
    public void onStruckByLightning(final LightningBoltEntity bolt) {}

    @Override
    public void addVelocity(final double x, final double y, final double z) {}

    @Override
    public void applyEntityCollision(final Entity entity) {}

    @Override
    protected void collideWithNearbyEntities() {}

    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.world.isRemote || this.removed || this.isInvulnerableTo(source)) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD == source) {
            this.remove();
        } else if (source.isExplosion()) {
            this.playBreakSound();
            this.remove();
        } else if (DamageSource.IN_FIRE == source) {
            if (this.isBurning()) {
                this.damage(0.15F);
            } else {
                this.setFire(5);
            }
        } else if (DamageSource.ON_FIRE == source && this.getHealth() > 0.5F) {
            this.damage(4);
        } else if (this.isPlayerDamage(source)) {
            if (source.isCreativePlayer()) {
                this.playBreakSound();
                this.playParticles();
                this.remove();
            } else {
                final long time = this.world.getGameTime();
                if (time - this.lastPunchTime > 5) {
                    this.world.setEntityState(this, PUNCH_ID);
                    this.lastPunchTime = time;
                } else {
                    this.dropIt();
                    this.playParticles();
                    this.remove();
                }
            }
        }
        return false;
    }

    private boolean isPlayerDamage(final DamageSource source) {
        if ("player".equals(source.getDamageType())) {
            final Entity e = source.getTrueSource();
            return !(e instanceof PlayerEntity) || ((PlayerEntity) e).abilities.allowEdit;
        }
        return false;
    }

    private void dropIt() {
        Block.spawnAsEntity(this.world, new BlockPos(this), new ItemStack(FLItems.LADDER.orElseThrow(IllegalStateException::new)));
    }

    private void playBreakSound() {
        this.world.playSound(null, this.posX, this.posY, this.posZ, this.getDeathSound(), this.getSoundCategory(), 1, 1);
    }

    private void playParticles() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld) this.world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.posX, this.posY + this.getHeight() / 1.5, this.posZ, 18, this.getWidth() / 4, this.getHeight() / 4, this.getWidth() / 4, 0.05);
        }
    }

    private void damage(final float amount) {
        final float newHealth = this.getHealth() - amount;
        if (newHealth <= 0.5F) {
            this.dropIt();
            this.remove();
        } else {
            this.setHealth(newHealth);
        }
    }

    @Override
    public void handleStatusUpdate(final byte id) {
        if (id == PUNCH_ID) {
            if (this.world.isRemote) {
                this.world.playSound(this.posX, this.posY, this.posZ, FLSounds.LADDER_HIT.orElseThrow(IllegalStateException::new), this.getSoundCategory(), 0.3F, 1, false);
                this.lastPunchTime = this.world.getGameTime();
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void onKillCommand() {
        this.remove();
    }

    @Override
    public void tick() {
        super.tick();
        this.renderYawOffset = this.rotationYawHead = this.rotationYaw;
    }

    @Override
    public void writeSpawnData(final PacketBuffer buf) {}

    @Override
    public void readSpawnData(final PacketBuffer buf) {
        this.prevRenderYawOffset = this.prevRotationYaw = this.renderYawOffset = this.rotationYawHead = this.rotationYaw;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
