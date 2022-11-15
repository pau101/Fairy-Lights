package me.paulf.fairylights.server.feature;

import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Pennant extends HangingFeature {
    private final int color;

    private final Item item;

    public Pennant(final int index, final Vec3 point, final float yaw, final float pitch, final int color, final Item item) {
        super(index, point, yaw, pitch, 0.0F, 0.0F);
        this.color = color;
        this.item = item;
    }

    public int getColor() {
        return this.color;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public AABB getBounds() {
        return new AABB(-0.22D, -0.5D, -0.02D, 0.22D, 0.0D, 0.02D);
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
