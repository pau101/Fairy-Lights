package com.pau101.fairylights.server.fastener;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ConcurrentModificationException;
import java.util.Set;

public class CollectFastenersEvent extends Event {
	private final World world;

	private final AxisAlignedBB region;

	private final Set<Fastener<?>> fasteners;

	public CollectFastenersEvent(final World world, final AxisAlignedBB region, final Set<Fastener<?>> fasteners) {
		this.world = world;
		this.region = region;
		this.fasteners = fasteners;
	}

	public World getWorld() {
		return world;
	}

	public AxisAlignedBB getRegion() {
		return region;
	}

	public void accept(final Chunk chunk) {
		try {
			for (final TileEntity entity : chunk.getTileEntityMap().values()) {
				this.accept(entity);
			}
		} catch (final ConcurrentModificationException e) {
			// RenderChunk's may find an invalid block entity while building and trigger a remove not on main thread
		}
	}

	public void accept(final ICapabilityProvider provider) {
		final Fastener<?> fastener = provider.getCapability(CapabilityHandler.FASTENER_CAP, null);
		if (fastener != null) {
			this.accept(fastener);
		}
	}

	public void accept(final Fastener<?> fastener) {
		if (this.region.contains(fastener.getConnectionPoint())) {
			this.fasteners.add(fastener);
		}
	}
}
