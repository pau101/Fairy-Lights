package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.item.ColorLightItem;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public final class GarlandTinselConnection extends Connection {
    private int color;

    public GarlandTinselConnection(final ConnectionType<? extends GarlandTinselConnection> type, final World world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
        this.color = ColorLightItem.getColor(DyeColor.LIGHT_GRAY);
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public float getRadius() {
        return 0.125F;
    }

    @Override
    public CompoundNBT serializeLogic() {
        return ColorLightItem.setColor(super.serializeLogic(), this.color);
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.color = ColorLightItem.getColor(compound);
    }
}
