package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.server.sound.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class LightBlockEntity extends TileEntity {
    private final Light light;

    private DyeColor color = DyeColor.YELLOW;

    public LightBlockEntity() {
        super(FLBlockEntities.LIGHT.orElseThrow(IllegalStateException::new));
        this.light = new Light(0, Vec3d.ZERO, 0.0F, 0.0F, true);
    }

    public Light getLight() {
        return this.light;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(final DyeColor color) {
        this.color = color;
        this.light.setColor(LightItem.getColorValue(color));
        this.markDirty();
    }

    private void setOn(final boolean on) {
        this.light.setOn(on);
        this.markDirty();
    }

    public void interact(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        this.setOn(!this.light.isOn());
        world.setBlockState(pos, state.with(LightBlock.LIT, this.light.isOn()));
        final SoundEvent lightSnd;
        final float pitch;
        if (this.light.isOn()) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.orElseThrow(IllegalStateException::new);
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.orElseThrow(IllegalStateException::new);
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
        compound.putByte("color", (byte) this.color.getId());
        compound.putBoolean("on", this.light.isOn());
        return compound;
    }

    @Override
    public void read(final CompoundNBT compound) {
        super.read(compound);
        this.setColor(DyeColor.byId(compound.getByte("color")));
        this.setOn(compound.getBoolean("on"));
    }
}
