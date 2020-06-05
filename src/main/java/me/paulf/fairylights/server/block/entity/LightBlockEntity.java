package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class LightBlockEntity extends TileEntity {
    private Light<?> light;

    private boolean on;

    public LightBlockEntity() {
        super(FLBlockEntities.LIGHT.get());
        this.light = new Light<>(0, Vec3d.ZERO, 0.0F, 0.0F, ItemStack.EMPTY, SimpleLightVariant.FAIRY);
    }

    public Light<?> getLight() {
        return this.light;
    }

    public void setItemStack(final ItemStack stack) {
        this.light = new Light<>(0, Vec3d.ZERO, 0.0F, 0.0F, stack, LightVariant.get(stack).orElse(SimpleLightVariant.FAIRY));
        this.markDirty();
    }

    private void setOn(final boolean on) {
        this.on = on;
        this.light.tick(new Random(0), on); // FIXME
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

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public CompoundNBT write(final CompoundNBT compound) {
        super.write(compound);
        compound.put("item", this.light.getItem().write(new CompoundNBT()));
        compound.putBoolean("on", this.on);
        return compound;
    }

    @Override
    public void read(final CompoundNBT compound) {
        super.read(compound);
        this.setItemStack(ItemStack.read(compound.getCompound("item")));
        this.setOn(compound.getBoolean("on"));
    }
}
