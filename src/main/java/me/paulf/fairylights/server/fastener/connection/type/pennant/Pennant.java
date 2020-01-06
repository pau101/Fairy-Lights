package me.paulf.fairylights.server.fastener.connection.type.pennant;

import me.paulf.fairylights.server.fastener.connection.type.HangingFeatureConnection;
import net.minecraft.util.math.Vec3d;

public class Pennant extends HangingFeatureConnection.HangingFeature<Pennant> {
    private int color;

    public Pennant(final int index, final Vec3d point, final Vec3d rotation) {
        super(index, point, rotation);
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
