package me.paulf.fairylights.server.fastener.connection.type.hanginglights;

import java.util.Random;

public interface LightBehavior {
    void tick(final Random rng, final boolean powered);
}
