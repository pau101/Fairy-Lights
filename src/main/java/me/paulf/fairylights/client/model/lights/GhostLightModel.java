package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.client.model.RotationOrder;
import me.paulf.fairylights.util.Mth;

import net.minecraft.util.math.MathHelper;

public final class GhostLightModel extends LightModel {
	private static final float FIN_ANGLE = -Mth.PI / 3;

	private static final float[] MAGIC_ANGLES = Mth.toEulerYZX(-1, 0, 1, FIN_ANGLE);

	public GhostLightModel() {
		AdvancedRendererModel bodyTop = new AdvancedRendererModel(this, 52, 48);
		bodyTop.setRotationPoint(0, 2, 0);
		bodyTop.addBox(-1.5F, 0, -1.5F, 3, 1, 3, 0);
		bodyTop.setRotationAngles(Mth.PI, 0, 0);
		colorableParts.addChild(bodyTop);
		AdvancedRendererModel littleFace = new AdvancedRendererModel(this, 40, 17);
		littleFace.setRotationPoint(0, -1, -2.25F);
		littleFace.addBox(-1.5F, -1.5F, 0, 3, 3, 0, 0);
		littleFace.setRotationAngles(Mth.PI, 0, 0);
		amutachromicParts.addChild(littleFace);
		AdvancedRendererModel body = new AdvancedRendererModel(this, 46, 40);
		body.setRotationPoint(0, 1, 0);
		body.addBox(-2, 0, -2, 4, 4, 4, 0);
		body.setRotationAngles(Mth.PI, 0, 0);
		colorableParts.addChild(body);
		final int finCount = 8;
		for (int i = 0; i < finCount; i++) {
			AdvancedRendererModel fin = new AdvancedRendererModel(this, 40, 21);
			float theta = i * Mth.TAU / finCount;
			fin.setRotationPoint(MathHelper.cos(-theta + Mth.PI / 4) * 1.1F, -2.75F, MathHelper.sin(-theta + Mth.PI / 4) * 1.1F);
			fin.addBox(0, 0, 0, 2, 1, 2, -0.1F);
			fin.setRotationOrder(RotationOrder.YZX);
			fin.setRotationAngles(MAGIC_ANGLES[0], MAGIC_ANGLES[1] + theta, MAGIC_ANGLES[2]);
			colorableParts.addChild(fin);
		}
	}

	@Override
	public boolean hasRandomRotation() {
		return true;
	}
}
