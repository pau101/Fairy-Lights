package me.paulf.fairylights.server.entity;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.item.ConnectionItem;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.io.IOException;

public final class FenceFastenerEntity extends HangingEntity implements IEntityAdditionalSpawnData {
    private int surfaceCheckTime;

    public FenceFastenerEntity(final EntityType<? extends FenceFastenerEntity> type, final World world) {
        super(type, world);
    }

    public FenceFastenerEntity(final World world) {
        this(FLEntities.FASTENER.get(), world);
    }

    public FenceFastenerEntity(final World world, final BlockPos pos) {
        this(world);
        this.func_70107_b(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
    }

    @Override
    public int func_82329_d() {
        return 9;
    }

    @Override
    public int func_82330_g() {
        return 9;
    }

    @Override
    public float func_213316_a(final Pose pose, final EntitySize size) {
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

    @SuppressWarnings("deprecation")
    @Override
    public float func_70013_c() {
        final BlockPos pos = this.func_233580_cy_();
        if (this.field_70170_p.func_195588_v(pos)) {
            return this.field_70170_p.func_205052_D(pos);
        }
        return 0;
    }

    @Override
    public boolean func_70112_a(final double distance) {
        return distance < 4096;
    }

    @Override
    public boolean func_180427_aV() {
        return true;
    }

    @Override
    public boolean func_70518_d() {
        return !this.field_70170_p.func_195588_v(this.field_174861_a) || ConnectionItem.isFence(this.field_70170_p.func_180495_p(this.field_174861_a));
    }

    @Override
    public void func_70106_y() {
        this.getFastener().ifPresent(Fastener::remove);
        super.func_70106_y();
    }

    // Copy from super but remove() moved to after onBroken()
    @Override
    public boolean func_70097_a(final DamageSource source, final float amount) {
        if (this.func_180431_b(source)) {
            return false;
        }
        if (!this.field_70170_p.field_72995_K && this.func_70089_S()) {
            this.func_70018_K();
            this.func_110128_b(source.func_76346_g());
            this.func_70106_y();
        }
        return true;
    }

    @Override
    public boolean func_184222_aU() {
        return false;
    }

    @Override
    public void func_110128_b(@Nullable final Entity breaker) {
        this.getFastener().ifPresent(fastener -> fastener.dropItems(this.field_70170_p, this.field_174861_a));
        if (breaker != null) {
            this.field_70170_p.func_217379_c(2001, this.field_174861_a, Block.func_196246_j(FLBlocks.FASTENER.get().func_176223_P()));
        }
    }

    @Override
    public void func_184523_o() {
        final SoundType sound = FLBlocks.FASTENER.get().getSoundType(FLBlocks.FASTENER.get().func_176223_P(), this.field_70170_p, this.func_174857_n(), null);
        this.func_184185_a(sound.func_185841_e(), (sound.func_185843_a() + 1) / 2, sound.func_185847_b() * 0.8F);
    }

    @Override
    public SoundCategory func_184176_by() {
        return SoundCategory.BLOCKS;
    }

    @Override
    public void func_70107_b(final double x, final double y, final double z) {
        super.func_70107_b(MathHelper.func_76128_c(x) + 0.5, MathHelper.func_76128_c(y) + 0.5, MathHelper.func_76128_c(z) + 0.5);
    }

    @Override
    public void func_174859_a(final Direction facing) {}

    @Override
    protected void func_174856_o() {
        final double posX = this.field_174861_a.func_177958_n() + 0.5;
        final double posY = this.field_174861_a.func_177956_o() + 0.5;
        final double posZ = this.field_174861_a.func_177952_p() + 0.5;
        this.func_226288_n_(posX, posY, posZ);
        final float w = 3 / 16F;
        final float h = 3 / 16F;
        this.func_174826_a(new AxisAlignedBB(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
    }

    @Override
    public AxisAlignedBB func_184177_bl() {
        return this.getFastener().map(fastener -> fastener.getBounds().func_186662_g(1)).orElseGet(super::func_184177_bl);
    }

    @Override
    public void func_70071_h_() {
        this.getFastener().ifPresent(fastener -> {
            if (!this.field_70170_p.field_72995_K && (fastener.hasNoConnections() || this.checkSurface())) {
                this.func_110128_b(null);
                this.func_70106_y();
            } else if (fastener.update() && !this.field_70170_p.field_72995_K) {
                final UpdateEntityFastenerMessage msg = new UpdateEntityFastenerMessage(this, fastener.serializeNBT());
                ServerProxy.sendToPlayersWatchingEntity(msg, this);
            }
        });
    }

    private boolean checkSurface() {
        if (this.surfaceCheckTime++ == 100) {
            this.surfaceCheckTime = 0;
            return !this.func_70518_d();
        }
        return false;
    }

    @Override
    public ActionResultType func_184230_a(final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.func_184586_b(hand);
        if (stack.func_77973_b() instanceof ConnectionItem) {
            if (this.field_70170_p.field_72995_K) {
                player.func_184609_a(hand);
            } else {
                this.getFastener().ifPresent(fastener -> ((ConnectionItem) stack.func_77973_b()).connect(stack, player, this.field_70170_p, fastener));
            }
            return ActionResultType.SUCCESS;
        }
        return super.func_184230_a(player, hand);
    }

    @Override
    public void func_213281_b(final CompoundNBT compound) {
        compound.func_218657_a("pos", NBTUtil.func_186859_a(this.field_174861_a));
    }

    @Override
    public void func_70037_a(final CompoundNBT compound) {
        this.field_174861_a = NBTUtil.func_186861_c(compound.func_74775_l("pos"));
    }

    @Override
    public void writeSpawnData(final PacketBuffer buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                CompressedStreamTools.func_74800_a(fastener.serializeNBT(), new ByteBufOutputStream(buf));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void readSpawnData(final PacketBuffer buf) {
        this.getFastener().ifPresent(fastener -> {
            try {
                fastener.deserializeNBT(CompressedStreamTools.func_152456_a(new ByteBufInputStream(buf), new NBTSizeTracker(0x200000)));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public IPacket<?> func_213297_N() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }

    public static FenceFastenerEntity create(final World world, final BlockPos fence) {
        final FenceFastenerEntity fastener = new FenceFastenerEntity(world, fence);
        fastener.field_98038_p = true;
        world.func_217376_c(fastener);
        fastener.func_184523_o();
        return fastener;
    }

    @Nullable
    public static FenceFastenerEntity find(final World world, final BlockPos pos) {
        final HangingEntity entity = findHanging(world, pos);
        if (entity instanceof FenceFastenerEntity) {
            return (FenceFastenerEntity) entity;
        }
        return null;
    }

    @Nullable
    public static HangingEntity findHanging(final World world, final BlockPos pos) {
        for (final HangingEntity e : world.func_217357_a(HangingEntity.class, new AxisAlignedBB(pos).func_186662_g(2))) {
            if (e.func_174857_n().equals(pos)) {
                return e;
            }
        }
        return null;
    }
}
