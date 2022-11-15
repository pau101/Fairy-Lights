package me.paulf.fairylights.server.feature.light;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CompositeBehavior implements StandardLightBehavior {
    private final BrightnessLightBehavior brightness;

    private final ColorLightBehavior color;

    public CompositeBehavior(final BrightnessLightBehavior brightness, final ColorLightBehavior color) {
        this.brightness = brightness;
        this.color = color;
    }

    @Override
    public float getBrightness(final float delta) {
        return this.brightness.getBrightness(delta);
    }

    @Override
    public float getRed(final float delta) {
        return this.color.getRed(delta);
    }

    @Override
    public float getGreen(final float delta) {
        return this.color.getGreen(delta);
    }

    @Override
    public float getBlue(final float delta) {
        return this.color.getBlue(delta);
    }

    @Override
    public void power(final boolean powered, final boolean now, final Light<?> light) {
        this.brightness.power(powered, now, light);
        this.color.power(powered, now, light);
    }

    @Override
    public void tick(final Level world, final Vec3 origin, final Light<?> light) {
        this.brightness.tick(world, origin, light);
        this.color.tick(world, origin, light);
    }
}
