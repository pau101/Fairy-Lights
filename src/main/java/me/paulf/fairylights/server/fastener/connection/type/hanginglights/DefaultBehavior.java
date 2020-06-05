package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import java.util.Random;

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
    public void tick(final Random rng, final boolean powered) {
        this.value = powered ? 1.0F : 0.0F;
    }
}
