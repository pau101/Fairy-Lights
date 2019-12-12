package me.paulf.fairylights.server.fastener.connection;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;

public final class FeatureType {
	private static final DefaultedRegistry<FeatureType> REGISTRY = new DefaultedRegistry<>("");

	public static final FeatureType UNKNOWN = create("");

	private static int nextFeatureId;

	private FeatureType() {}

	public int getId() {
		return REGISTRY.getId(this);
	}

	public static FeatureType create(String name) {
		FeatureType type = new FeatureType();
		REGISTRY.register(nextFeatureId++, new ResourceLocation(name), type);
		return type;
	}

	public static FeatureType fromId(int id) {
		return REGISTRY.getByValue(id);
	}
}
