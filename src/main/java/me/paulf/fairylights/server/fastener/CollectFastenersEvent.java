package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.capability.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.eventbus.api.*;

import java.util.*;

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
        return this.world;
    }

    public AxisAlignedBB getRegion() {
        return this.region;
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
        provider.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(this::accept);
    }

    public void accept(final Fastener<?> fastener) {
        if (this.region.contains(fastener.getConnectionPoint())) {
            this.fasteners.add(fastener);
        }
    }
}
