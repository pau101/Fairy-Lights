package me.paulf.fairylights.server.feature.light;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DefaultBrightnessBehavior implements BrightLightBehavior {
    private float value = 1.0F;

    @Override
    public float getBrightness(final float delta) {
        return this.value;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.value = powered ? 1.0F : 0.0F;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {
    }
}
