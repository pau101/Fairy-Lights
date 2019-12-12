package me.paulf.fairylights.client.model;

import me.paulf.fairylights.server.entity.EntityLadder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;

public final class ModelLadder extends EntityModel<EntityLadder> {
	private final RendererModel railFrontLeft;

	private final RendererModel railFrontRight;

	private final RendererModel railRearLeft;

	private final RendererModel railRearRight;

	private final RendererModel topCap;

	private final RendererModel step1;

	private final RendererModel step2;

	private final RendererModel step3;

	private final RendererModel step4;

	private final RendererModel spreaderLeft;

	private final RendererModel spreaderRight;

	private final RendererModel rod1;

	private final RendererModel rod2;

	private final RendererModel rod3;

	private final RendererModel rod4;

	public ModelLadder() {
		textureWidth = 128;
		textureHeight = 64;
		step1 = new RendererModel(this, 36, 30);
		step1.setRotationPoint(0.0F, 15.7F, -12.0F);
		step1.addBox(-6.5F, 0.0F, -2.0F, 13, 2, 4, 0.0F);
		step4 = new RendererModel(this, 36, 12);
		step4.setRotationPoint(0.0F, -11.3F, -4.9F);
		step4.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
		rod2 = new RendererModel(this, 16, 52);
		rod2.setRotationPoint(0.0F, 6.7F, 9.5F);
		rod2.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
		setRotateAngle(rod2, 0.2617993877991494F, 0.0F, 0.0F);
		spreaderLeft = new RendererModel(this, 0, 48);
		spreaderLeft.setRotationPoint(-6.4F, 0.2F, -6.7F);
		spreaderLeft.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
		topCap = new RendererModel(this, 36, 0);
		topCap.setRotationPoint(0.0F, -20.5F, 0.0F);
		topCap.addBox(-8.0F, 0.0F, -5.0F, 16, 2, 10, 0.0F);
		rod1 = new RendererModel(this, 16, 54);
		rod1.setRotationPoint(0.0F, 15.7F, 11.9F);
		rod1.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
		setRotateAngle(rod1, 0.2617993877991494F, 0.0F, 0.0F);
		step3 = new RendererModel(this, 36, 18);
		step3.setRotationPoint(0.0F, -2.3F, -7.3F);
		step3.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
		step2 = new RendererModel(this, 36, 24);
		step2.setRotationPoint(0.0F, 6.7F, -9.7F);
		step2.addBox(-6.0F, 0.0F, -1.9F, 12, 2, 4, 0.0F);
		railRearLeft = new RendererModel(this, 20, 0);
		railRearLeft.setRotationPoint(-7.25F, 24.0F, 14.0F);
		railRearLeft.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
		setRotateAngle(railRearLeft, 0.2617993877991494F, -0.12217304763960307F, 0.0F);
		railFrontRight = new RendererModel(this, 10, 0);
		railFrontRight.setRotationPoint(7.5F, 24.0F, -14.0F);
		railFrontRight.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
		setRotateAngle(railFrontRight, -0.2617993877991494F, -0.12217304763960307F, 0.0F);
		railFrontLeft = new RendererModel(this, 0, 0);
		railFrontLeft.setRotationPoint(-7.5F, 24.0F, -14.0F);
		railFrontLeft.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
		setRotateAngle(railFrontLeft, -0.2617993877991494F, 0.12217304763960307F, 0.0F);
		railRearRight = new RendererModel(this, 28, 0);
		railRearRight.setRotationPoint(7.25F, 24.0F, 14.0F);
		railRearRight.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
		setRotateAngle(railRearRight, 0.2617993877991494F, 0.12217304763960307F, 0.0F);
		rod3 = new RendererModel(this, 16, 50);
		rod3.setRotationPoint(0.0F, -2.3F, 7.1F);
		rod3.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
		setRotateAngle(rod3, 0.2617993877991494F, 0.0F, 0.0F);
		rod4 = new RendererModel(this, 16, 48);
		rod4.setRotationPoint(0.0F, -11.3F, 4.6F);
		rod4.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
		setRotateAngle(rod4, 0.2617993877991494F, 0.0F, 0.0F);
		spreaderRight = new RendererModel(this, 30, 48);
		spreaderRight.setRotationPoint(6.4F, 0.0F, -6.7F);
		spreaderRight.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
	}

	@Override
	public void render(EntityLadder entity, float f, float f1, float f2, float f3, float f4, float f5) {
		step1.render(f5);
		step4.render(f5);
		rod2.render(f5);
		spreaderLeft.render(f5);
		topCap.render(f5);
		rod1.render(f5);
		step3.render(f5);
		step2.render(f5);
		railRearLeft.render(f5);
		railFrontRight.render(f5);
		railFrontLeft.render(f5);
		railRearRight.render(f5);
		rod3.render(f5);
		rod4.render(f5);
		spreaderRight.render(f5);
	}

	private static void setRotateAngle(RendererModel renderer, float x, float y, float z) {
		renderer.rotateAngleX = x;
		renderer.rotateAngleY = y;
		renderer.rotateAngleZ = z;
	}
}
