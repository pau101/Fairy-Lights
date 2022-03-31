package me.paulf.fairylights.server.block.entity;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.fastener.Fastener;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public final class FastenerBlockEntity extends TileEntity implements ITickableTileEntity {
    public FastenerBlockEntity() {
        super(FLBlockEntities.FASTENER.get());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getFastener().map(fastener -> fastener.getBounds().func_186662_g(1)).orElseGet(super::getRenderBoundingBox);
    }

    public Vector3d getOffset() {
        return FLBlocks.FASTENER.get().getOffset(this.getFacing(), 0.125F);
    }

    public Direction getFacing() {
        final BlockState state = this.field_145850_b.func_180495_p(this.field_174879_c);
        if (state.func_177230_c() != FLBlocks.FASTENER.get()) {
            return Direction.UP;
        }
        return state.func_177229_b(FastenerBlock.field_176387_N);
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
    public void func_226984_a_(final World world, final BlockPos pos) {
        super.func_226984_a_(world, pos);
        this.getFastener().ifPresent(fastener -> fastener.setWorld(world));
    }

    @Override
    public void func_73660_a() {
        this.getFastener().ifPresent(fastener -> {
            if (!this.field_145850_b.field_72995_K && fastener.hasNoConnections()) {
                this.field_145850_b.func_217377_a(this.field_174879_c, false);
            } else if (!this.field_145850_b.field_72995_K && fastener.update()) {
                this.func_70296_d();
                final BlockState state = this.field_145850_b.func_180495_p(this.field_174879_c);
                this.field_145850_b.func_184138_a(this.field_174879_c, state, state, 3);
            }
        });
    }

    @Override
    public void func_145843_s() {
        this.getFastener().ifPresent(Fastener::remove);
        super.func_145843_s();
    }

    private LazyOptional<Fastener<?>> getFastener() {
        return this.getCapability(CapabilityHandler.FASTENER_CAP);
    }
}
