package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.Mth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LightBlockEntity extends TileEntity {
    private Light<?> light;

    private boolean on = true;

    public LightBlockEntity() {
        super(FLBlockEntities.LIGHT.get());
        this.light = new Light<>(0, Vector3d.ZERO, 0.0F, 0.0F, ItemStack.EMPTY, SimpleLightVariant.FAIRY_LIGHT, 0.0F);
    }

    public Light<?> getLight() {
        return this.light;
    }

    public void setItemStack(final ItemStack stack) {
        this.light = new Light<>(0, Vector3d.ZERO, 0.0F, 0.0F, stack, LightVariant.get(stack).orElse(SimpleLightVariant.FAIRY_LIGHT), 0.0F);
        this.markDirty();
    }

    private void setOn(final boolean on) {
        this.on = on;
        this.light.power(on, true);
        this.markDirty();
    }

    public void interact(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        this.setOn(!this.on);
        world.setBlockState(pos, state.with(LightBlock.LIT, this.on));
        final SoundEvent lightSnd;
        final float pitch;
        if (this.on) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.get();
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.get();
            pitch = 0.5F;
        }
        this.world.playSound(null, pos, lightSnd, SoundCategory.BLOCKS, 1.0F, pitch);
    }

    public void animateTick() {
        final BlockState state = this.getBlockState();
        final AttachFace face = state.get(LightBlock.FACE);
        final float rotation = state.get(LightBlock.HORIZONTAL_FACING).getHorizontalAngle();
        final MatrixStack matrix = new MatrixStack();
        matrix.translate(0.5F, 0.5F, 0.5F);
        matrix.rotate((float) Math.toRadians(180.0F - rotation), 0.0F, 1.0F, 0.0F);
        if (this.light.getVariant().isOrientable()) {
            if (face == AttachFace.WALL) {
                matrix.rotate(Mth.HALF_PI, 1.0F, 0.0F, 0.0F);
            } else if (face == AttachFace.FLOOR) {
                matrix.rotate(-Mth.PI, 1.0F, 0.0F, 0.0F);
            }
            matrix.translate(0.0F, 0.5F, 0.0F);
        } else {
            if (face == AttachFace.CEILING) {
                matrix.translate(0.0F, 0.25F, 0.0F);
            } else if (face == AttachFace.WALL) {
                matrix.translate(0.0F, 3.0F / 16.0F, 0.125F);
            } else {
                matrix.translate(0.0F, -(float) this.light.getVariant().getBounds().minY - 0.5F, 0.0F);
            }
        }
        this.light.getBehavior().animateTick(this.world, Vector3d.copy(this.getPos()).add(matrix.transform(Vector3d.ZERO)), this.light);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
        this.read(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT write(final CompoundNBT compound) {
        super.write(compound);
        compound.put("item", this.light.getItem().write(new CompoundNBT()));
        compound.putBoolean("on", this.on);
        return compound;
    }

    @Override
    public void read(final BlockState state, final CompoundNBT compound) {
        super.read(state, compound);
        this.setItemStack(ItemStack.read(compound.getCompound("item")));
        this.setOn(compound.getBoolean("on"));
    }
}
