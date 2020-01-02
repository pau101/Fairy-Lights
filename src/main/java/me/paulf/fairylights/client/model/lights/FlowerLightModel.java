package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.util.Mth;

public final class FlowerLightModel extends LightModel {
	private static final float PEDDLE_ANGLE = -Mth.PI / 6;

	private static final float[] MAGIC_ANGLES = Mth.toEulerYZX(-1, 0, 1, PEDDLE_ANGLE);

	public FlowerLightModel() {
		amutachromicLitParts.setTextureOffset(12, 0);
		amutachromicLitParts.addBox(-1.5F, -1, -1.5F, 3, 3, 3);
		int peddleCount = 5;
		for (int p = 0; p < peddleCount; p++) {
			float theta = p * Mth.TAU / peddleCount;
			AdvancedRendererModel peddleModel = new AdvancedRendererModel(this, 24, 0);
			peddleModel.setRotationOrder(RotationOrder.YZX);
			peddleModel.addBox(0, 0, 0, 5, 1, 5);
			peddleModel.rotationPointY = 1;
			peddleModel.rotateAngleX = MAGIC_ANGLES[0];
			peddleModel.rotateAngleY = MAGIC_ANGLES[1] + theta;
			peddleModel.rotateAngleZ = MAGIC_ANGLES[2];
			colorableParts.addChild(peddleModel);
		}
	}

	@Override
	public boolean hasRandomRotation() {
		return true;
	}

	@Override
	public void prepare(final int index) {
		super.prepare(index);
		if (hasRandomRotation()) {
			float randomTilt = Mth.mod(Mth.hash(index) * Mth.DEG_TO_RAD, Mth.TAU) / Mth.TAU * Mth.PI / 8 - Mth.PI / 16;
			colorableParts.rotateAngleZ += randomTilt;
			amutachromicParts.rotateAngleZ += randomTilt;
		}
	}
}
