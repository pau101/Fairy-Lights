package com.pau101.fairylights.server.fastener.connection.collision;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

import com.pau101.fairylights.server.fastener.connection.Feature;
import com.pau101.fairylights.server.fastener.connection.FeatureType;

public final class Intersection {
	private final RayTraceResult result;

	private final AxisAlignedBB hitBox;

	private final FeatureType featureType;

	private final Feature feature;

	public Intersection(RayTraceResult result, AxisAlignedBB hitBox, FeatureType featureType, Feature feature) {
		this.result = result;
		this.hitBox = hitBox;
		this.featureType = featureType;
		this.feature = feature;
	}

	public RayTraceResult getResult() {
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
