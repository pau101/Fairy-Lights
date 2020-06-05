package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import java.util.Random;

public class MeteorLightBehavior implements ColorLightBehavior {
    private final float red;

    private final float green;

    private final float blue;

    private final TwinkleLogic logic = new TwinkleLogic(0.02F, 100);

    public MeteorLightBehavior(final float red, final float green, final float blue) {
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
    public void tick(final Random rng, final boolean powered) {
        this.logic.tick(rng, powered);
    }

    public float getProgress(final float delta) {
        return this.logic.get(delta);
    }
}
