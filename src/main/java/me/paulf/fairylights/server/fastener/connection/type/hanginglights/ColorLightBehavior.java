package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

public interface ColorLightBehavior extends LightBehavior {
    float getRed(final float delta);

    float getGreen(final float delta);

    float getBlue(final float delta);
}
