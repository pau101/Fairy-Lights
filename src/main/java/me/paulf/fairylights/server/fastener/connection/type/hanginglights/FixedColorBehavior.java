package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

public abstract class FixedColorBehavior implements ColorLightBehavior {
    private final float red;

    private final float green;

    private final float blue;

    protected FixedColorBehavior(final float red, final float green, final float blue) {
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
}
