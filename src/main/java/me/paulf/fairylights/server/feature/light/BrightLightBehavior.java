package me.paulf.fairylights.server.feature.light;

public interface BrightLightBehavior extends LightBehavior {
    float getBrightness(final float delta);
}
