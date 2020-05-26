package me.paulf.fairylights.server.fastener.connection.collision;

import me.paulf.fairylights.server.fastener.connection.*;
import net.minecraft.util.math.*;

public final class Intersection {
    private final Vec3d result;

    private final AxisAlignedBB hitBox;

    private final FeatureType featureType;

    private final Feature feature;

    public Intersection(final Vec3d result, final AxisAlignedBB hitBox, final FeatureType featureType, final Feature feature) {
        this.result = result;
        this.hitBox = hitBox;
        this.featureType = featureType;
        this.feature = feature;
    }

    public Vec3d getResult() {
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
