package me.paulf.fairylights.server.feature.light;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MeteorLightBehavior implements ColorLightBehavior {
    private final ColorLightBehavior color;

    private final TwinkleLogic logic = new TwinkleLogic(0.02F, 100);

    private boolean powered = true;

    public MeteorLightBehavior(final ColorLightBehavior color) {
        this.color = color;
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
        this.powered = powered;
        this.color.power(powered, now, light);
    }

    @Override
    public void tick(final World world, final Vector3d origin, final Light<?> light) {
        this.logic.tick(world.field_73012_v, this.powered);
        this.color.tick(world, origin, light);
    }

    public float getProgress(final float delta) {
        return this.logic.get(delta);
    }
}
