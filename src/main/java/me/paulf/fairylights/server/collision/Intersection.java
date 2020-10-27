package me.paulf.fairylights.server.collision;

import me.paulf.fairylights.server.feature.Feature;
import me.paulf.fairylights.server.feature.FeatureType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public final class Intersection {
    private final Vector3d result;

    private final AxisAlignedBB hitBox;

    private final FeatureType featureType;

    private final Feature feature;

    public Intersection(final Vector3d result, final AxisAlignedBB hitBox, final FeatureType featureType, final Feature feature) {
        this.result = result;
        this.hitBox = hitBox;
        this.featureType = featureType;
        this.feature = feature;
    }

    public Vector3d getResult() {
        return this.result;
    }

    public AxisAlignedBB getHitBox() {
        return this.hitBox;
    }

    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public Feature getFeature() {
        return this.feature;
    }
}
