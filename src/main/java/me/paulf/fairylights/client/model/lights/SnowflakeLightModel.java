package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;

public final class SnowflakeLightModel extends LightModel {
	public SnowflakeLightModel() {
		AdvancedRendererModel connector = new AdvancedRendererModel(this, 90, 40);
		connector.addBox(-1, 0.2F, -1, 2, 1, 2);
		amutachromicParts.addChild(connector);
		AdvancedRendererModel branch = new AdvancedRendererModel(this, 18, 72);
		branch.add3DTexture(0, 0, 0, 11, 13);
		float size = 0.75F;
		branch.setRotationPoint(-5.5F * size, -12.8F * size, 0.5F * size);
		branch.scaleX = branch.scaleY = branch.scaleZ = size;
		colorableParts.addChild(branch);
		colorableParts.glowExpandAmount = 0.2F;
	}

	@Override
	public boolean hasRandomRotation() {
		return true;
	}
}
