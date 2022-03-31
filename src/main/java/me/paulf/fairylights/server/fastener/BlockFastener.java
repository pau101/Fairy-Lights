package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3d;

public final class BlockFastener extends AbstractFastener<BlockFastenerAccessor> {
    private final FastenerBlockEntity fastener;

    private final BlockView view;

    public BlockFastener(final FastenerBlockEntity fastener, final BlockView view) {
        this.fastener = fastener;
        this.view = view;
        this.bounds = new AxisAlignedBB(fastener.func_174877_v());
        this.setWorld(fastener.func_145831_w());
    }

    @Override
    public Direction getFacing() {
        return this.fastener.getFacing();
    }

    @Override
    public boolean isMoving() {
        return this.view.isMoving(this.getWorld(), this.fastener.func_174877_v());
    }

    @Override
    public BlockPos getPos() {
        return this.fastener.func_174877_v();
    }

    @Override
    public Vector3d getConnectionPoint() {
        return this.view.getPosition(this.getWorld(), this.fastener.func_174877_v(), Vector3d.func_237491_b_(this.getPos()).func_178787_e(this.fastener.getOffset()));
    }

    @Override
    public BlockFastenerAccessor createAccessor() {
        return new BlockFastenerAccessor(this);
    }
}
