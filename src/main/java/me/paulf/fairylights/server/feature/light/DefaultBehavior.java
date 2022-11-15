package me.paulf.fairylights.server.feature.light;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DefaultBehavior extends FixedColorBehavior implements StandardLightBehavior {
    private float value = 1.0F;

    public DefaultBehavior(final float red, final float green, final float blue) {
        super(red, green, blue);
    }

    @Override
    public float getBrightness(final float delta) {
        return this.value;
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.value = powered ? 1.0F : 0.0F;
    }

    @Override
    public void tick(final Level world, final Vec3 origin, final Light<?> light) {}
}
