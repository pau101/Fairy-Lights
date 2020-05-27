package me.paulf.fairylights.server.fastener.connection.type.garland;

import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.item.LightItem;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public final class GarlandTinselConnection extends Connection {
    private DyeColor color;

    public GarlandTinselConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
    }

    public GarlandTinselConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
        super(world, fastener, uuid);
        this.color = DyeColor.LIGHT_GRAY;
    }

    public int getColor() {
        return LightItem.getColorValue(this.color);
    }

    @Override
    public float getRadius() {
        return 0.125F;
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.TINSEL;
    }

    @Override
    public CompoundNBT serializeLogic() {
        final CompoundNBT compound = super.serializeLogic();
        compound.putByte("color", (byte) this.color.getId());
        return compound;
    }

    @Override
    public void deserializeLogic(final CompoundNBT compound) {
        super.deserializeLogic(compound);
        this.color = DyeColor.byId(compound.getByte("color"));
    }
}
