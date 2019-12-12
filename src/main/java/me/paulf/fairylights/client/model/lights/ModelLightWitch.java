package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedModelRenderer;

public final class ModelLightWitch extends ModelLight {
	public ModelLightWitch() {
		AdvancedModelRenderer rim = new AdvancedModelRenderer(this, 58, 7);
		rim.setRotationPoint(0, -1, 0);
		rim.addBox(-4.0F, 0, -4.0F, 8, 1, 8, 0);
		AdvancedModelRenderer middleBottom = new AdvancedModelRenderer(this, 56, 58);
		middleBottom.setRotationPoint(0, -2, 0);
		middleBottom.addBox(-2, 0, -2, 4, 2, 4, 0);
		AdvancedModelRenderer beltPoke = new AdvancedModelRenderer(this, 66, 4);
		beltPoke.setRotationPoint(0.2F, 0.5F, -0.5F);
		beltPoke.addBox(0, 0, 0, 1, 1, 1, 0);
		AdvancedModelRenderer buckle = new AdvancedModelRenderer(this, 0, 27);
		buckle.setRotationPoint(1.9F, -0.6F, 0);
		buckle.addBox(0, 0, -1, 1, 2, 2, 0);
		buckle.setRotationAngles(0, 0, 0.2617993877991494F);
		AdvancedModelRenderer belt = new AdvancedModelRenderer(this, 62, 0);
		belt.setRotationPoint(0, -4.5F, 0);
		belt.addBox(-2.5F, 0, -2.5F, 5, 1, 5, 0);
		AdvancedModelRenderer middleTop = new AdvancedModelRenderer(this, 52, 52);
		middleTop.setRotationPoint(0, -3, 0);
		middleTop.addBox(-1.5F, 0, -1.5F, 3, 3, 3, 0);
		AdvancedModelRenderer tip = new AdvancedModelRenderer(this, 15, 54);
		tip.setRotationPoint(0, 0, 0);
		tip.addBox(-1, 0, -1, 2, 2, 2, 0);
		middleBottom.addChild(rim);
		middleTop.addChild(middleBottom);
		buckle.addChild(beltPoke);
		belt.addChild(buckle);
		tip.addChild(middleTop);
		colorableParts.addChild(tip);
		amutachromicParts.addChild(belt);
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
