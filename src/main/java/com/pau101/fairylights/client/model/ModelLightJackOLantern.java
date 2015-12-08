package com.pau101.fairylights.client.model;

import com.pau101.fairylights.util.MathUtils;

public class ModelLightJackOLantern extends ModelLight {
	private static final float LEAF_ANGLE = MathUtils.PI / 12;

	private static final float[] LEAF_ANGLES = MathUtils.toEuler(-1, 0, 1, LEAF_ANGLE);

	public ModelLightJackOLantern() {
		AdvancedModelRenderer pumpkin = new AdvancedModelRenderer(this, 28, 42);
		pumpkin.setRotationPoint(0, 0, 0);
		pumpkin.addBox(-3, 0, -3, 6, 6, 6, 0);
		pumpkin.setRotationAngles(MathUtils.PI, 0, 0);
		colorableParts.addChild(pumpkin);
		AdvancedModelRenderer leaf1 = new AdvancedModelRenderer(this, 12, 18);
		leaf1.setRotationPoint(0.5F, 0, 0.5F);
		leaf1.addBox(0, -0.5F, 0, 2, 1, 2, 0);
		leaf1.setRotationOrder(RotationOrder.YZX);
		leaf1.setRotationAngles(LEAF_ANGLES[0], LEAF_ANGLES[1], LEAF_ANGLES[2]);
		amutachromicParts.addChild(leaf1);
		AdvancedModelRenderer leaf2 = new AdvancedModelRenderer(this, 12, 18);
		leaf2.setRotationPoint(-0.5F, 0, -0.5F);
		leaf2.addBox(0, -0.5F, 0, 2, 1, 2, 0);
		leaf2.setRotationOrder(RotationOrder.YZX);
		leaf2.setRotationAngles(LEAF_ANGLES[0], LEAF_ANGLES[1] + MathUtils.PI, LEAF_ANGLES[2]);
		amutachromicParts.addChild(leaf2);
		AdvancedModelRenderer stem = new AdvancedModelRenderer(this, 21, 41);
		stem.setRotationPoint(0, 2, 0);
		stem.addBox(-1, 0, -1, 2, 2, 2, 0);
		stem.setRotationAngles(MathUtils.PI, 0, 0);
		amutachromicParts.addChild(stem);
		AdvancedModelRenderer face = new AdvancedModelRenderer(this, 56, 34);
		face.setRotationPoint(0, -3, -3.25F);
		face.addBox(-3, -3, 0, 6, 6, 0, 0);
		face.setRotationAngles(MathUtils.PI, 0, 0);
		amutachromicParts.addChild(face);
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
