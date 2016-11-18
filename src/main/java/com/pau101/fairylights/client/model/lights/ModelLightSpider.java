package com.pau101.fairylights.client.model.lights;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.util.Mth;

public final class ModelLightSpider extends ModelLight {
	public ModelLightSpider() {
		AdvancedModelRenderer string = new AdvancedModelRenderer(this, 30, 6);
		string.addBox(-1, 0, -1, 2, 2, 2, 0);
		AdvancedModelRenderer abdomen = new AdvancedModelRenderer(this, 20, 54);
		abdomen.setRotationPoint(0, 0, 0);
		abdomen.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0);
		AdvancedModelRenderer pedicel = new AdvancedModelRenderer(this, 6, 0);
		pedicel.setRotationPoint(0, -6.0F, 0);
		pedicel.addBox(-1, 0, -1, 2, 1, 2, 0);
		AdvancedModelRenderer cephalothorax = new AdvancedModelRenderer(this, 40, 57);
		cephalothorax.setRotationPoint(0, -2.25F, 0);
		cephalothorax.addBox(-2, 0, -2, 4, 3, 4, 0);
		AdvancedModelRenderer cheliceraLeft = new AdvancedModelRenderer(this, 0, 0);
		cheliceraLeft.setRotationPoint(0, 0.3F, 0.6F);
		cheliceraLeft.addBox(-1, -1.5F, 0, 2, 2, 1, 0);
		cheliceraLeft.setRotationAngles(-0.2617993877991494F, 0, 0);
		AdvancedModelRenderer cheliceraRight = new AdvancedModelRenderer(this, 0, 0);
		cheliceraRight.setRotationPoint(0, 0.3F, -0.6F);
		cheliceraRight.addBox(-1, -1.5F, -1, 2, 2, 1, 0);
		cheliceraRight.setRotationAngles(0.2617993877991494F, 0, 0);
		pedicel.addChild(createLegs(0));
		pedicel.addChild(createLegs(1));
		cephalothorax.addChild(cheliceraLeft);
		cephalothorax.addChild(cheliceraRight);
		pedicel.addChild(cephalothorax);
		abdomen.addChild(pedicel);
		amutachromicParts.addChild(string);
		colorableParts.addChild(abdomen);
	}

	private AdvancedModelRenderer createLegs(int side) {
		AdvancedModelRenderer leg1 = new AdvancedModelRenderer(this, 21, 45);
		leg1.setRotationPoint(0, 0.6F, 1.1F);
		leg1.addBox(0, -0.5F, -0.5F, 5, 1, 1, 0);
		leg1.setRotationAngles(-0.13962634015954636F, -0.7853981633974483F, 1.5707963267948966F);
		AdvancedModelRenderer leg1Lower = new AdvancedModelRenderer(this, 21, 45);
		leg1Lower.setRotationPoint(4.59F, 0, -0.24F);
		leg1Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
		leg1Lower.setRotationAngles(0, -1.0471975511965976F, 0);
		AdvancedModelRenderer leg2 = new AdvancedModelRenderer(this, 21, 45);
		leg2.setRotationPoint(0, 0.6F, 1.1F);
		leg2.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
		leg2.setRotationAngles(0.13962634015954636F, -1.2217304763960306F, 1.5707963267948966F);
		AdvancedModelRenderer leg2Lower = new AdvancedModelRenderer(this, 21, 45);
		leg2Lower.setRotationPoint(3.65F, 0, -0.13F);
		leg2Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
		leg2Lower.setRotationAngles(0, -0.7853981633974483F, 0);
		AdvancedModelRenderer leg3 = new AdvancedModelRenderer(this, 21, 45);
		leg3.setRotationPoint(0, 0.3F, 1.1F);
		leg3.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
		leg3.setRotationAngles(-0.06981317007977318F, -1.8212510744560826F, 1.5707963267948966F);
		AdvancedModelRenderer leg3Lower = new AdvancedModelRenderer(this, 21, 45);
		leg3Lower.setRotationPoint(3.81F, 0, -0.05F);
		leg3Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
		leg3Lower.setRotationAngles(0, -0.4363323129985824F, 0);
		AdvancedModelRenderer leg4 = new AdvancedModelRenderer(this, 21, 45);
		leg4.setRotationPoint(0, -0.5F, 1.1F);
		leg4.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
		leg4.setRotationAngles(0.06981317007977318F, -2.1855012893472994F, 1.5707963267948966F);
		AdvancedModelRenderer leg4Lower = new AdvancedModelRenderer(this, 21, 45);
		leg4Lower.setRotationPoint(3.76F, 0, -0.06F);
		leg4Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
		leg4Lower.setRotationAngles(0, -0.5235987755982988F, 0);
		leg1.addChild(leg1Lower);
		leg2.addChild(leg2Lower);
		leg3.addChild(leg3Lower);
		leg4.addChild(leg4Lower);
		AdvancedModelRenderer legs = new AdvancedModelRenderer(this);
		legs.rotateAngleY = Mth.PI * side;
		legs.addChild(leg1);
		legs.addChild(leg2);
		legs.addChild(leg3);
		legs.addChild(leg4);
		return legs;
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
