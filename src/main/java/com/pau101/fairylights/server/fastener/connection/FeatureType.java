package com.pau101.fairylights.server.fastener.connection;

import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;

public final class FeatureType {
	private static final RegistryNamespacedDefaultedByKey<String, FeatureType> REGISTRY = new RegistryNamespacedDefaultedByKey<>("");

	public static final FeatureType UNKNOWN = create("");

	private static int nextFeatureId;

	private FeatureType() {}

	public int getId() {
		return REGISTRY.getIDForObject(this);
	}

	public static FeatureType create(String name) {
		FeatureType type = new FeatureType();
		REGISTRY.register(nextFeatureId++, name, type);
		return type;
	}

	public static FeatureType fromId(int id) {
		return REGISTRY.getObjectById(id);
	}
}
