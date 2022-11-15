package me.paulf.fairylights.server.collision;

import me.paulf.fairylights.server.feature.Feature;
import me.paulf.fairylights.server.feature.FeatureType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Intersection {
    private final Vec3 result;

    private final AABB hitBox;

    private final FeatureType featureType;

    private final Feature feature;

    public Intersection(final Vec3 result, final AABB hitBox, final FeatureType featureType, final Feature feature) {
        this.result = result;
        this.hitBox = hitBox;
        this.featureType = featureType;
        this.feature = feature;
    }

    public Vec3 getResult() {
        return this.result;
    }

    public AABB getHitBox() {
        return this.hitBox;
    }

    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public Feature getFeature() {
        return this.feature;
    }
}
