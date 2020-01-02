package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightBlockEntity extends TileEntity {
    private final Light light;

    private DyeColor color = DyeColor.YELLOW;

    public LightBlockEntity() {
        super(FLBlockEntities.LIGHT.orElseThrow(IllegalStateException::new));
        this.light = new Light(0, Vec3d.ZERO, Vec3d.ZERO, true);
    }

    public Light getLight() {
        return light;
    }

    public DyeColor getColor() {
        return color;
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
        compound.putByte("color", (byte) color.getId());
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
