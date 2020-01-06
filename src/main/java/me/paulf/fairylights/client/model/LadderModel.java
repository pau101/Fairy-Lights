package me.paulf.fairylights.client.model;

import me.paulf.fairylights.server.entity.LadderEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;

public final class LadderModel extends EntityModel<LadderEntity> {
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

    public LadderModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.step1 = new RendererModel(this, 36, 30);
        this.step1.setRotationPoint(0.0F, 15.7F, -12.0F);
        this.step1.addBox(-6.5F, 0.0F, -2.0F, 13, 2, 4, 0.0F);
        this.step4 = new RendererModel(this, 36, 12);
        this.step4.setRotationPoint(0.0F, -11.3F, -4.9F);
        this.step4.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
        this.rod2 = new RendererModel(this, 16, 52);
        this.rod2.setRotationPoint(0.0F, 6.7F, 9.5F);
        this.rod2.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(this.rod2, 0.2617993877991494F, 0.0F, 0.0F);
        this.spreaderLeft = new RendererModel(this, 0, 48);
        this.spreaderLeft.setRotationPoint(-6.4F, 0.2F, -6.7F);
        this.spreaderLeft.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
        this.topCap = new RendererModel(this, 36, 0);
        this.topCap.setRotationPoint(0.0F, -20.5F, 0.0F);
        this.topCap.addBox(-8.0F, 0.0F, -5.0F, 16, 2, 10, 0.0F);
        this.rod1 = new RendererModel(this, 16, 54);
        this.rod1.setRotationPoint(0.0F, 15.7F, 11.9F);
        this.rod1.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(this.rod1, 0.2617993877991494F, 0.0F, 0.0F);
        this.step3 = new RendererModel(this, 36, 18);
        this.step3.setRotationPoint(0.0F, -2.3F, -7.3F);
        this.step3.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
        this.step2 = new RendererModel(this, 36, 24);
        this.step2.setRotationPoint(0.0F, 6.7F, -9.7F);
        this.step2.addBox(-6.0F, 0.0F, -1.9F, 12, 2, 4, 0.0F);
        this.railRearLeft = new RendererModel(this, 20, 0);
        this.railRearLeft.setRotationPoint(-7.25F, 24.0F, 14.0F);
        this.railRearLeft.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
        setRotateAngle(this.railRearLeft, 0.2617993877991494F, -0.12217304763960307F, 0.0F);
        this.railFrontRight = new RendererModel(this, 10, 0);
        this.railFrontRight.setRotationPoint(7.5F, 24.0F, -14.0F);
        this.railFrontRight.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
        setRotateAngle(this.railFrontRight, -0.2617993877991494F, -0.12217304763960307F, 0.0F);
        this.railFrontLeft = new RendererModel(this, 0, 0);
        this.railFrontLeft.setRotationPoint(-7.5F, 24.0F, -14.0F);
        this.railFrontLeft.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
        setRotateAngle(this.railFrontLeft, -0.2617993877991494F, 0.12217304763960307F, 0.0F);
        this.railRearRight = new RendererModel(this, 28, 0);
        this.railRearRight.setRotationPoint(7.25F, 24.0F, 14.0F);
        this.railRearRight.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
        setRotateAngle(this.railRearRight, 0.2617993877991494F, 0.12217304763960307F, 0.0F);
        this.rod3 = new RendererModel(this, 16, 50);
        this.rod3.setRotationPoint(0.0F, -2.3F, 7.1F);
        this.rod3.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(this.rod3, 0.2617993877991494F, 0.0F, 0.0F);
        this.rod4 = new RendererModel(this, 16, 48);
        this.rod4.setRotationPoint(0.0F, -11.3F, 4.6F);
        this.rod4.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(this.rod4, 0.2617993877991494F, 0.0F, 0.0F);
        this.spreaderRight = new RendererModel(this, 30, 48);
        this.spreaderRight.setRotationPoint(6.4F, 0.0F, -6.7F);
        this.spreaderRight.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
    }

    @Override
    public void render(final LadderEntity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        this.step1.render(f5);
        this.step4.render(f5);
        this.rod2.render(f5);
        this.spreaderLeft.render(f5);
        this.topCap.render(f5);
        this.rod1.render(f5);
        this.step3.render(f5);
        this.step2.render(f5);
        this.railRearLeft.render(f5);
        this.railFrontRight.render(f5);
        this.railFrontLeft.render(f5);
        this.railRearRight.render(f5);
        this.rod3.render(f5);
        this.rod4.render(f5);
        this.spreaderRight.render(f5);
    }

    private static void setRotateAngle(final RendererModel renderer, final float x, final float y, final float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
