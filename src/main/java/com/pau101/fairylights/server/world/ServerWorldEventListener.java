package com.pau101.fairylights.server.world;

import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.item.ItemConnection;
import com.pau101.fairylights.util.WorldEventListener;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ServerWorldEventListener implements WorldEventListener {
	@Override
	public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		if (ItemConnection.isFence(oldState, null) && !ItemConnection.isFence(newState, world.getTileEntity(pos))) {
			EntityFenceFastener fastener = EntityFenceFastener.find(world, pos);
			if (fastener != null) {
				fastener.setDead();
				fastener.onBroken(null);
			}
		}
	}
}
