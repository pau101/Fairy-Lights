package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface BlockView {
    boolean isMoving(final World world, final BlockPos source);

    Vector3d getPosition(final World world, final BlockPos source, final Vector3d pos);

    void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta);
}
