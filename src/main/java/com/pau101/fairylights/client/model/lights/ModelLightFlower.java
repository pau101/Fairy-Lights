package com.pau101.fairylights.client.model.lights;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.client.model.RotationOrder;
import com.pau101.fairylights.connection.Light;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ModelLightFlower extends ModelLight {
	private AdvancedModelRenderer[] peddles;

	private static final float PEDDLE_ANGLE = -MathUtils.PI / 6;

	private static final float[] MAGIC_ANGLES = MathUtils.toEuler(-1, 0, 1, PEDDLE_ANGLE);

	public ModelLightFlower() {
		amutachromicParts.setTextureOffset(12, 0);
		amutachromicParts.addBox(-1.5F, 0, -1.5F, 3, 2, 3);
		int peddleCount = 5;
		peddles = new AdvancedModelRenderer[peddleCount];
		for (int p = 0; p < peddleCount; p++) {
			float theta = p / (float) peddleCount * MathUtils.TAU;
			float x = MathHelper.cos(theta);
			float z = MathHelper.sin(theta);
			AdvancedModelRenderer peddleModel = new AdvancedModelRenderer(this, 24, 0);
			peddleModel.setRotationOrder(RotationOrder.YZX);
			peddleModel.addBox(0, 0, 0, 5, 1, 5);
			peddleModel.rotationPointY = 1;
			peddleModel.rotateAngleX = MAGIC_ANGLES[0];
			peddleModel.rotateAngleY = MAGIC_ANGLES[1] + theta;
			peddleModel.rotateAngleZ = MAGIC_ANGLES[2];
			colorableParts.addChild(peddleModel);
			peddles[p] = peddleModel;
		}
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}

	@Override
	public void render(World world, Light light, float scale, Vector3f color, int moonlight, int sunlight, float brightness, int index, float partialRenderTicks) {
		float randomTilt = MathUtils.modf(MathUtils.hash(index) * MathUtils.DEG_TO_RAD, MathUtils.TAU) / MathUtils.TAU * MathUtils.PI / 8 - MathUtils.PI / 16;
		colorableParts.rotateAngleZ += randomTilt;
		amutachromicParts.rotateAngleZ += randomTilt;
		super.render(world, light, scale, color, moonlight, sunlight, brightness, index, partialRenderTicks);
	}
}
