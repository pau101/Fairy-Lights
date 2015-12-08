package com.pau101.fairylights.client.model.lights;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.util.MathUtils;

public class ModelLightOrnate extends ModelLight {
	public ModelLightOrnate() {
		amutachromicParts.setTextureOffset(21, 0);
		amutachromicParts.addBox(-1, 0.5F, -1, 2, 1, 2); // point

		amutachromicParts.setTextureOffset(0, 3);
		amutachromicParts.addBox(-1.5F, -1.5F, -1.5F, 3, 2, 3); // capUpper

		amutachromicParts.setTextureOffset(43, 15);
		amutachromicParts.addBox(-2.5F, -1.75F, -2.5F, 5, 1, 5); // ogee

		amutachromicParts.setTextureOffset(23, 27);
		amutachromicParts.addBox(-3, -2.5F, -3, 6, 1, 6); // cap

		amutachromicParts.setTextureOffset(43, 21);
		amutachromicParts.addBox(-2.5F, -8.5F, -2.5F, 5, 1, 5);

		for (int i = 0; i < 4; i++) {
			AdvancedModelRenderer frame = new AdvancedModelRenderer(this, 4 * i + 47, 27);
			frame.addBox(-0.5F, 0, -0.5F, 1, 6, 1);
			frame.setRotationPoint(2.1F * ((i & 2) == 0 ? 1 : -1), -8F, 2.1F * ((i + 1 & 2) == 0 ? 1 : -1));
			frame.rotateAngleX = 5 * MathUtils.DEG_TO_RAD;
			frame.rotateAngleY = (90 * i + 45) * MathUtils.DEG_TO_RAD;
			amutachromicParts.addChild(frame);
		}

		colorableParts.setTextureOffset(48, 6);
		colorableParts.addBox(-2, -7.5F, -2, 4, 5, 4); // glass
	}

	@Override
	public boolean shouldParallelCord() {
		return false;
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
