package com.pau101.fairylights.client.model.lights;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.util.MathUtils;

public class ModelLightPaper extends ModelLight {
	public ModelLightPaper() {
		amutachromicParts.setTextureOffset(34, 18);
		amutachromicParts.addBox(-1, -0.5F, -1, 2, 2, 2); // point

		amutachromicParts.setTextureOffset(21, 26);
		amutachromicParts.addBox(-1, -9.5F, -1, 2, 3, 2); // base

		amutachromicParts.setTextureOffset(58, 0);
		amutachromicParts.addBox(-0.5F, -14.5F, -0.5F, 1, 5, 1); // string

		for (int i = 0; i < 8; i++) {
			boolean straight = (i & 1) == 0;
			AdvancedModelRenderer hSupport = new AdvancedModelRenderer(this, 28, 34);
			hSupport.addBox(0, 0, -0.5F, straight ? 4 : 5, 7, 1);
			hSupport.rotateAngleY = 45 * i * MathUtils.DEG_TO_RAD;
			hSupport.rotationPointY = -7;
			amutachromicParts.addChild(hSupport);
		}

		colorableParts.setTextureOffset(0, 41);
		colorableParts.addBox(-3.5F, -6.5F, -3.5F, 7, 6, 7); // orb
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
