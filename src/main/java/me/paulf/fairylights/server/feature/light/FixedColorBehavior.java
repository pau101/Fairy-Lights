package me.paulf.fairylights.server.feature.light;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FixedColorBehavior implements ColorLightBehavior {
    private final float red;

    private final float green;

    private final float blue;

    public FixedColorBehavior(final float red, final float green, final float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public float getRed(final float delta) {
        return this.red;
    }

    @Override
    public float getGreen(final float delta) {
        return this.green;
    }

    @Override
    public float getBlue(final float delta) {
        return this.blue;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
    }
}
