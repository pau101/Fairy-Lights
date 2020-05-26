package me.paulf.fairylights.server.fastener.connection;

import net.minecraft.util.*;
import net.minecraft.util.registry.*;

public final class FeatureType {
    private static final DefaultedRegistry<FeatureType> REGISTRY = new DefaultedRegistry<>("");

    public static final FeatureType UNKNOWN = create("");

    private static int nextFeatureId;

    private FeatureType() {}

    public int getId() {
        return REGISTRY.getId(this);
    }

    public static FeatureType create(final String name) {
        final FeatureType type = new FeatureType();
        REGISTRY.register(nextFeatureId++, new ResourceLocation(name), type);
        return type;
    }

    public static FeatureType fromId(final int id) {
        return REGISTRY.getByValue(id);
    }
}
