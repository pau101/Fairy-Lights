package me.paulf.fairylights.client.model;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import me.paulf.fairylights.server.entity.*;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.model.*;

public final class LadderModel extends EntityModel<LadderEntity> {
    private final ModelRenderer root;

    public LadderModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.root = new ModelRenderer(this);
        final ModelRenderer step1 = new ModelRenderer(this, 36, 30);
        step1.setRotationPoint(0.0F, 15.7F, -12.0F);
        step1.addBox(-6.5F, 0.0F, -2.0F, 13, 2, 4, 0.0F);
        this.root.addChild(step1);
        final ModelRenderer step4 = new ModelRenderer(this, 36, 12);
        step4.setRotationPoint(0.0F, -11.3F, -4.9F);
        step4.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
        this.root.addChild(step4);
        final ModelRenderer rod2 = new ModelRenderer(this, 16, 52);
        rod2.setRotationPoint(0.0F, 6.7F, 9.5F);
        rod2.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(rod2, 0.2617993877991494F, 0.0F, 0.0F);
        this.root.addChild(rod2);
        final ModelRenderer spreaderLeft = new ModelRenderer(this, 0, 48);
        spreaderLeft.setRotationPoint(-6.4F, 0.2F, -6.7F);
        spreaderLeft.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
        this.root.addChild(spreaderLeft);
        final ModelRenderer topCap = new ModelRenderer(this, 36, 0);
        topCap.setRotationPoint(0.0F, -20.5F, 0.0F);
        topCap.addBox(-8.0F, 0.0F, -5.0F, 16, 2, 10, 0.0F);
        this.root.addChild(topCap);
        final ModelRenderer rod1 = new ModelRenderer(this, 16, 54);
        rod1.setRotationPoint(0.0F, 15.7F, 11.9F);
        rod1.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(rod1, 0.2617993877991494F, 0.0F, 0.0F);
        this.root.addChild(rod1);
        final ModelRenderer step3 = new ModelRenderer(this, 36, 18);
        step3.setRotationPoint(0.0F, -2.3F, -7.3F);
        step3.addBox(-5.5F, 0.0F, -2.0F, 11, 2, 4, 0.0F);
        this.root.addChild(step3);
        final ModelRenderer step2 = new ModelRenderer(this, 36, 24);
        step2.setRotationPoint(0.0F, 6.7F, -9.7F);
        step2.addBox(-6.0F, 0.0F, -1.9F, 12, 2, 4, 0.0F);
        this.root.addChild(step2);
        final ModelRenderer railRearLeft = new ModelRenderer(this, 20, 0);
        railRearLeft.setRotationPoint(-7.25F, 24.0F, 14.0F);
        railRearLeft.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
        setRotateAngle(railRearLeft, 0.2617993877991494F, -0.12217304763960307F, 0.0F);
        this.root.addChild(railRearLeft);
        final ModelRenderer railFrontRight = new ModelRenderer(this, 10, 0);
        railFrontRight.setRotationPoint(7.5F, 24.0F, -14.0F);
        railFrontRight.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
        setRotateAngle(railFrontRight, -0.2617993877991494F, -0.12217304763960307F, 0.0F);
        this.root.addChild(railFrontRight);
        final ModelRenderer railFrontLeft = new ModelRenderer(this, 0, 0);
        railFrontLeft.setRotationPoint(-7.5F, 24.0F, -14.0F);
        railFrontLeft.addBox(-1.0F, -45.0F, -1.5F, 2, 45, 3, 0.0F);
        setRotateAngle(railFrontLeft, -0.2617993877991494F, 0.12217304763960307F, 0.0F);
        this.root.addChild(railFrontLeft);
        final ModelRenderer railRearRight = new ModelRenderer(this, 28, 0);
        railRearRight.setRotationPoint(7.25F, 24.0F, 14.0F);
        railRearRight.addBox(-1.0F, -45.0F, -1.0F, 2, 45, 2, 0.0F);
        setRotateAngle(railRearRight, 0.2617993877991494F, 0.12217304763960307F, 0.0F);
        this.root.addChild(railRearRight);
        final ModelRenderer rod3 = new ModelRenderer(this, 16, 50);
        rod3.setRotationPoint(0.0F, -2.3F, 7.1F);
        rod3.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(rod3, 0.2617993877991494F, 0.0F, 0.0F);
        this.root.addChild(rod3);
        final ModelRenderer rod4 = new ModelRenderer(this, 16, 48);
        rod4.setRotationPoint(0.0F, -11.3F, 4.6F);
        rod4.addBox(-6.5F, -0.5F, -0.5F, 13, 1, 1, 0.0F);
        setRotateAngle(rod4, 0.2617993877991494F, 0.0F, 0.0F);
        this.root.addChild(rod4);
        final ModelRenderer spreaderRight = new ModelRenderer(this, 30, 48);
        spreaderRight.setRotationPoint(6.4F, 0.0F, -6.7F);
        spreaderRight.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 14, 0.0F);
        this.root.addChild(spreaderRight);
    }

    @Override
    public void setRotationAngles(final LadderEntity ladder, final float limbSwing, final float limbSwingAmount, final float ticks, final float yaw, final float pitch) {
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.root.render(matrix, builder, light, overlay, r, g, b, a);
    }

    private static void setRotateAngle(final ModelRenderer renderer, final float x, final float y, final float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
