package com.pau101.fairylights.client.model.lights;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.world.World;

import com.pau101.fairylights.client.model.AdvancedModelRenderer;
import com.pau101.fairylights.connection.Light;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ModelLightIcicle extends ModelLight {
	private AdvancedModelRenderer wireMiddle, wireBottom, wireEnd;

	private AdvancedModelRenderer light2, light3, light4;

	public ModelLightIcicle() {
		wireMiddle = new AdvancedModelRenderer(this, 29, 76);
		wireMiddle.setRotationPoint(0, 4, 0);
		wireMiddle.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
		wireMiddle.rotateAngleX = -0.2617993877991494F;
		wireMiddle.rotateAngleY = 0.5235987755982988F;
		AdvancedModelRenderer lightBase3 = new AdvancedModelRenderer(this, 33, 76);
		lightBase3.setRotationPoint(0, 2, 0.5F);
		lightBase3.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
		AdvancedModelRenderer lightBase2 = new AdvancedModelRenderer(this, 33, 76);
		lightBase2.setRotationPoint(0, 2, -1.5F);
		lightBase2.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
		AdvancedModelRenderer light1 = new AdvancedModelRenderer(this, 29, 72);
		light1.setRotationPoint(0, -2.405233653435833F, -1.170506183587062F);
		light1.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
		light1.rotateAngleX = -3.0543261909900767F;
		wireBottom = new AdvancedModelRenderer(this, 29, 76);
		wireBottom.setRotationPoint(0, 4, 0);
		wireBottom.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
		wireBottom.rotateAngleX = 0.4363323129985824F;
		wireBottom.rotateAngleY = 0.5235987755982988F;
		light2 = new AdvancedModelRenderer(this, 29, 72);
		light2.setRotationPoint(-1.7077077845361228F, -5.893569134597652F, 2.4972589475492635F);
		light2.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
		light2.rotateAngleX = 2.9804748914277273F;
		light2.rotateAngleY = -0.5214031733599432F;
		light2.rotateAngleZ = -0.050276985685263745F;
		AdvancedModelRenderer lightBase1 = new AdvancedModelRenderer(this, 33, 76);
		lightBase1.setRotationPoint(0, 2, 0.5F);
		lightBase1.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
		AdvancedModelRenderer wireTop = new AdvancedModelRenderer(this, 29, 76);
		wireTop.setRotationPoint(0, -0.5F, 0);
		wireTop.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
		wireTop.rotateAngleX = -3.0543261909900767F;
		light3 = new AdvancedModelRenderer(this, 29, 72);
		light3.setRotationPoint(0.7935216418735993F, -10.095277243516536F, -0.4609129179470893F);
		light3.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
		light3.rotateAngleX = -2.9807093751793796F;
		light3.rotateAngleY = -1.0339196228641108F;
		light3.rotateAngleZ = 0.10720187699072795F;
		AdvancedModelRenderer connector = new AdvancedModelRenderer(this, 77, 0);
		connector.setRotationPoint(0, 0, 0);
		connector.addBox(-1, -0.5F, -1, 2, 2, 2, 0);
		light4 = new AdvancedModelRenderer(this, 29, 72);
		light4.setRotationPoint(-2.4652688758772636F, -12.95165172579971F, -1.9323986317282649F);
		light4.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		light4.rotateAngleX = -0.7522647962292457F;
		light4.rotateAngleY = -1.3039482923151255F;
		light4.rotateAngleZ = -2.5903859234812483F;
		wireEnd = new AdvancedModelRenderer(this, 29, 76);
		wireEnd.setRotationPoint(0.0F, 4.0F, 0.0F);
		wireEnd.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
		wireEnd.rotateAngleX = -0.4363323129985824F;
		wireEnd.rotateAngleY = 0.7853981633974483F;
		AdvancedModelRenderer lightBase4 = new AdvancedModelRenderer(this, 33, 76);
		lightBase4.setRotationPoint(0.0F, 2.0F, -1.5F);
		lightBase4.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		wireTop.addChild(lightBase1);
		wireTop.addChild(wireMiddle);
		wireMiddle.addChild(lightBase2);
		wireMiddle.addChild(wireBottom);
		wireBottom.addChild(lightBase3);
		wireBottom.addChild(wireEnd);
		wireEnd.addChild(lightBase4);
		connector.addChild(wireTop);
		amutachromicParts.addChild(connector);
		colorableParts.addChild(light1);
		colorableParts.addChild(light2);
		colorableParts.addChild(light3);
		colorableParts.addChild(light4);
	}

	@Override
	public boolean shouldParallelCord() {
		return false;
	}

	@Override
	public boolean hasRandomRotatation() {
		return true;
	}

	@Override
	public void render(World world, Light light, float scale, Vector3f color, int moonlight, int sunlight, float brightness, int index, float partialRenderTicks) {
		int which = MathUtils.modi(MathUtils.hash(index), 4);
		if (which == 0) {
			which = index % 3;
		}
		wireMiddle.isHidden = which == 0;
		wireBottom.isHidden = which == 1;
		wireEnd.isHidden = which == 2;
		light2.isHidden = which < 1;
		light3.isHidden = which < 2;
		light4.isHidden = which < 3;
		super.render(world, light, scale, color, moonlight, sunlight, brightness, index, partialRenderTicks);
	}
}
