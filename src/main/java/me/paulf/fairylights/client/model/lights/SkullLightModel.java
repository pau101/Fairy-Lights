package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.util.Mth;

public final class SkullLightModel extends LightModel {
	public SkullLightModel() {
		AdvancedRendererModel apertures = new AdvancedRendererModel(this, 12, 10);
		apertures.setRotationPoint(0, -3, -2.75F);
		apertures.addBox(-1.5F, -1, 0, 3, 2, 0, 0);
		apertures.setRotationAngles(Mth.PI, 0, 0);
		amutachromicParts.addChild(apertures);
		AdvancedRendererModel skull = new AdvancedRendererModel(this, 0, 54);
		skull.addBox(-2.5F, 0, -2.5F, 5, 4, 5, 0);
		skull.setRotationAngles(Mth.PI, 0, 0);
		colorableParts.addChild(skull);
		AdvancedRendererModel mandible = new AdvancedRendererModel(this, 40, 34);
		mandible.setRotationPoint(0, -3.5F, 0.3F);
		mandible.addBox(-2.5F, 0, -3, 5, 2, 3, -0.25F);
		mandible.setRotationAngles(Mth.PI / 16 + Mth.PI, Mth.PI, 0);
		colorableParts.addChild(mandible);
		AdvancedRendererModel maxilla = new AdvancedRendererModel(this, 46, 7);
		maxilla.setRotationPoint(0, -3.875F, -2.125F);
		maxilla.setRotationAngles(Mth.PI, 0, 0);
		maxilla.addBox(-1, 0, -0.5F, 2, 1, 1, -0.125F);
		colorableParts.addChild(maxilla);
		AdvancedRendererModel chain = new AdvancedRendererModel(this, 34, 18);
		chain.setRotationPoint(0, 2, 0);
		chain.addBox(-1, 0, -1, 2, 2, 2, 0);
		chain.setRotationAngles(Mth.PI, 0, 0);
		amutachromicParts.addChild(chain);
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}
}
