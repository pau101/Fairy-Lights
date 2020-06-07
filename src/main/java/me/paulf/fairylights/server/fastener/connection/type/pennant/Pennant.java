package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.server.fastener.connection.type.HangingFeature;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class Pennant extends HangingFeature {
    private int color;

    private Item item = Items.AIR;

    public Pennant(final int index, final Vec3d point, final float yaw, final float pitch) {
        super(index, point, yaw, pitch, 0.0F, 0.0F);
    }

    public void setColor(final int color) {
        this.color = color;
    }

    public void setItem(final Item item) {
        this.item = item;
    }

    public int getColor() {
        return this.color;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public AxisAlignedBB getBounds() {
        return new AxisAlignedBB(-0.22D, -0.5D, -0.22D, 0.22D, 0.0D, 0.22D);
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
