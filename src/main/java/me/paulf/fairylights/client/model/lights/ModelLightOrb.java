package me.paulf.fairylights.client.model.lights;

public final class ModelLightOrb extends ModelLight {
	public ModelLightOrb() {
		amutachromicParts.setTextureOffset(30, 6);
		amutachromicParts.addBox(-1, -0.5F, -1, 2, 2, 2);
		colorableParts.setTextureOffset(0, 27);
		colorableParts.addBox(-3.5F, -7.5F, -3.5F, 7, 7, 7);
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
