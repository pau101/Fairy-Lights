package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.util.Mth;

public final class JackOLanternLightModel extends LightModel {
    private static final float LEAF_ANGLE = Mth.PI / 12;

    private static final float[] LEAF_ANGLES = Mth.toEulerYZX(-1, 0, 1, LEAF_ANGLE);

    public JackOLanternLightModel() {
        final AdvancedRendererModel pumpkin = new AdvancedRendererModel(this, 28, 42);
        pumpkin.setRotationPoint(0, 0, 0);
        pumpkin.addBox(-3, 0, -3, 6, 6, 6, 0);
        pumpkin.setRotationAngles(Mth.PI, 0, 0);
        this.colorableParts.addChild(pumpkin);
        final AdvancedRendererModel leaf1 = new AdvancedRendererModel(this, 12, 18);
        leaf1.setRotationPoint(0.5F, 0, 0.5F);
        leaf1.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        leaf1.setRotationOrder(RotationOrder.YZX);
        leaf1.setRotationAngles(LEAF_ANGLES[0], LEAF_ANGLES[1], LEAF_ANGLES[2]);
        this.amutachromicParts.addChild(leaf1);
        final AdvancedRendererModel leaf2 = new AdvancedRendererModel(this, 12, 18);
        leaf2.setRotationPoint(-0.5F, 0, -0.5F);
        leaf2.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        leaf2.setRotationOrder(RotationOrder.YZX);
        leaf2.setRotationAngles(LEAF_ANGLES[0], LEAF_ANGLES[1] + Mth.PI, LEAF_ANGLES[2]);
        this.amutachromicParts.addChild(leaf2);
        final AdvancedRendererModel stem = new AdvancedRendererModel(this, 21, 41);
        stem.setRotationPoint(0, 2, 0);
        stem.addBox(-1, 0, -1, 2, 2, 2, 0);
        stem.setRotationAngles(Mth.PI, 0, 0);
        this.amutachromicParts.addChild(stem);
        final AdvancedRendererModel face = new AdvancedRendererModel(this, 56, 34);
        face.setRotationPoint(0, -3, -3.25F);
        face.addBox(-3, -3, 0, 6, 6, 0, 0);
        face.setRotationAngles(Mth.PI, 0, 0);
        this.amutachromicParts.addChild(face);
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }
}
