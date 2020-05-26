package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.server.fastener.connection.type.*;
import net.minecraft.util.math.*;

public class Pennant extends HangingFeature<Pennant> {
    private int color;

    public Pennant(final int index, final Vec3d point, final float yaw, final float pitch) {
        super(index, point, yaw, pitch, 0.0F);
    }

    public void setColor(final int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public double getWidth() {
        return 0.4F;
    }

    @Override
    public double getHeight() {
        return 0.65F;
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }
}
