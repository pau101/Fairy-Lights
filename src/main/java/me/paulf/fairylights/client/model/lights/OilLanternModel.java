package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.util.Mth;

public final class OilLanternModel extends LightModel {
	public OilLanternModel() {
		amutachromicParts.setTextureOffset(10, 6);
		amutachromicParts.addBox(-1, -0.5F, -1, 2, 2, 2); // point

		amutachromicParts.setTextureOffset(0, 8);
		amutachromicParts.addBox(-1.5F, -1, -1.5F, 3, 1, 3); // top

		amutachromicParts.setTextureOffset(16, 18);
		amutachromicParts.addBox(-3, -10.5F, -3, 6, 2, 6); // base

		amutachromicParts.setTextureOffset(0, 12);
		amutachromicParts.addBox(-1.5F, -9.5F, -1.5F, 3, 2, 3); // connection

		amutachromicParts.setTextureOffset(38, 7);
		amutachromicParts.addBox(-0.5F, -9, -3.5F, 1, 9, 1); // support 1

		amutachromicParts.setTextureOffset(42, 7);
		amutachromicParts.addBox(-0.5F, -9, 2.5F, 1, 9, 1); // support 2

		amutachromicParts.setTextureOffset(38, 0);
		amutachromicParts.addBox(-0.5F, -0.5F, -3, 1, 1, 6); // rod

		colorableParts.setTextureOffset(0, 17);
		colorableParts.addBox(-2, -7.5F, -2, 4, 6, 4); // glass

		colorableParts.setTextureOffset(6, 0);
		colorableParts.addBox(-1, -1.5F, -1, 2, 1, 2); // glass top
	}

	@Override
	public void setRotationAngles(final double x, final double y, final double z) {
		super.setRotationAngles(x, y - Mth.HALF_PI, z);
	}

	@Override
	public boolean hasRandomRotation() {
		return true;
	}
}
