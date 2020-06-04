package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import java.util.Random;

public interface LightBehavior {
    float get(final float delta);

    void tick(final Random rng);

    default void inherit(final LightBehavior parent) {}
}
