package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

public interface BrightLightBehavior extends LightBehavior {
    float getBrightness(final float delta);
}
