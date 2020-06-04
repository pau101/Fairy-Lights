package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import java.util.Random;

public class ConstantBehavior implements LightBehavior {
    private static final ConstantBehavior ON = new ConstantBehavior(1.0F);

    private static final ConstantBehavior OFF = new ConstantBehavior(0.0F);

    private final float value;

    public ConstantBehavior(final float value) {
        this.value = value;
    }

    @Override
    public float get(final float delta) {
        return this.value;
    }

    @Override
    public void tick(final Random rng) {
    }

    public static ConstantBehavior on() {
        return ON;
    }

    public static ConstantBehavior off() {
        return OFF;
    }
}
