package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.util.matrix.Matrix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RegularBlockView implements BlockView {
	@Override
	public boolean isMoving(final World world, final BlockPos source) {
		return false;
	}

	@Override
	public Vec3d getPosition(final World world, final BlockPos source, final Vec3d pos) {
		return pos;
	}

	@Override
	public void unrotate(final World world, final BlockPos source, final Matrix matrix, final float delta) {}
}
