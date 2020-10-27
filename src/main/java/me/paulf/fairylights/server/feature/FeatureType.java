package me.paulf.fairylights.server.feature;

import com.mojang.serialization.Lifecycle;
import me.paulf.fairylights.FairyLights;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public final class FeatureType {
    private static final DefaultedRegistry<FeatureType> REGISTRY = new DefaultedRegistry<>(
        "default",
        RegistryKey.getOrCreateRootKey(new ResourceLocation(FairyLights.ID, "feature")),
        Lifecycle.experimental()
    );

    public static final FeatureType DEFAULT = register("default");

    private FeatureType() {}

    public int getId() {
        return REGISTRY.getId(this);
    }

    public static FeatureType register(final String name) {
        return Registry.register(REGISTRY, new ResourceLocation(name), new FeatureType());
    }

    public static FeatureType fromId(final int id) {
        return REGISTRY.getByValue(id);
    }
}
