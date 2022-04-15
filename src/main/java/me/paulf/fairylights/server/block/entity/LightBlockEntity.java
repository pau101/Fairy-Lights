package me.paulf.fairylights.server.block.entity;

import com.mojang.math.Vector3d;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.FLMath;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class LightBlockEntity extends BlockEntity {
    private Light<?> light;

    private boolean on = true;

    public LightBlockEntity() {
        super(FLBlockEntities.LIGHT.get());
        this.light = new Light<>(0, Vector3d.field_186680_a, 0.0F, 0.0F, ItemStack.field_190927_a, SimpleLightVariant.FAIRY_LIGHT, 0.0F);
    }

    public Light<?> getLight() {
        return this.light;
    }

    public void setItemStack(final ItemStack stack) {
        this.light = new Light<>(0, Vector3d.field_186680_a, 0.0F, 0.0F, stack, LightVariant.get(stack).orElse(SimpleLightVariant.FAIRY_LIGHT), 0.0F);
        this.func_70296_d();
    }

    private void setOn(final boolean on) {
        this.on = on;
        this.light.power(on, true);
        this.func_70296_d();
    }

    public void interact(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        this.setOn(!this.on);
        world.func_175656_a(pos, state.func_206870_a(LightBlock.LIT, this.on));
        final SoundEvent lightSnd;
        final float pitch;
        if (this.on) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.get();
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.get();
            pitch = 0.5F;
        }
        this.field_145850_b.func_184133_a(null, pos, lightSnd, SoundCategory.BLOCKS, 1.0F, pitch);
    }

    public void animateTick() {
        final BlockState state = this.func_195044_w();
        final AttachFace face = state.func_177229_b(LightBlock.field_196366_M);
        final float rotation = state.func_177229_b(LightBlock.field_185512_D).func_185119_l();
        final MatrixStack matrix = new MatrixStack();
        matrix.translate(0.5F, 0.5F, 0.5F);
        matrix.rotate((float) Math.toRadians(180.0F - rotation), 0.0F, 1.0F, 0.0F);
        if (this.light.getVariant().isOrientable()) {
            if (face == AttachFace.WALL) {
                matrix.rotate(FLMath.HALF_PI, 1.0F, 0.0F, 0.0F);
            } else if (face == AttachFace.FLOOR) {
                matrix.rotate(-FLMath.PI, 1.0F, 0.0F, 0.0F);
            }
            matrix.translate(0.0F, 0.5F, 0.0F);
        } else {
            if (face == AttachFace.CEILING) {
                matrix.translate(0.0F, 0.25F, 0.0F);
            } else if (face == AttachFace.WALL) {
                matrix.translate(0.0F, 3.0F / 16.0F, 0.125F);
            } else {
                matrix.translate(0.0F, -(float) this.light.getVariant().getBounds().field_72338_b - 0.5F, 0.0F);
            }
        }
        this.light.getBehavior().animateTick(this.field_145850_b, Vector3d.func_237491_b_(this.func_174877_v()).func_178787_e(matrix.transform(Vector3d.field_186680_a)), this.light);
    }

    @Override
    public SUpdateTileEntityPacket func_189518_D_() {
        return new SUpdateTileEntityPacket(this.field_174879_c, 0, this.func_189517_E_());
    }

    @Override
    public CompoundNBT func_189517_E_() {
        return this.func_189515_b(new CompoundNBT());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
        this.func_230337_a_(this.field_145850_b.func_180495_p(pkt.func_179823_a()), pkt.func_148857_g());
    }

    @Override
    public CompoundNBT func_189515_b(final CompoundNBT compound) {
        super.func_189515_b(compound);
        compound.func_218657_a("item", this.light.getItem().func_77955_b(new CompoundNBT()));
        compound.func_74757_a("on", this.on);
        return compound;
    }

    @Override
    public void func_230337_a_(final BlockState state, final CompoundNBT compound) {
        super.func_230337_a_(state, compound);
        this.setItemStack(ItemStack.func_199557_a(compound.func_74775_l("item")));
        this.setOn(compound.func_74767_n("on"));
    }
}
