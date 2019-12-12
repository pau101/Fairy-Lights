package me.paulf.fairylights.server.fastener.connection.collision;

import me.paulf.fairylights.server.fastener.connection.Feature;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public final class Intersection {
	private final Vec3d result;

	private final AxisAlignedBB hitBox;

	private final FeatureType featureType;

	private final Feature feature;

	public Intersection(Vec3d result, AxisAlignedBB hitBox, FeatureType featureType, Feature feature) {
		this.result = result;
		this.hitBox = hitBox;
		this.featureType = featureType;
		this.feature = feature;
	}

	public Vec3d getResult() {
		return result;
	}

	public AxisAlignedBB getHitBox() {
		return hitBox;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public Feature getFeature() {
		return feature;
	}
}
