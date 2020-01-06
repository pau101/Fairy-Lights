package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface BlockView {
    boolean isMoving(final World world, final BlockPos source);

    Vec3d getPosition(final World world, final BlockPos source, final Vec3d pos);

    void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta);
}
