package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
    public void power(final boolean powered) {
        this.value = powered ? 1.0F : 0.0F;
    }

    @Override
    public void tick(final World world, final Vec3d origin, final Light<?> light) {}
}
