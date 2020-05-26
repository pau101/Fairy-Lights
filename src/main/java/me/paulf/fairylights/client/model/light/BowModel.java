package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;

public class BowModel extends Model {
    private final ModelRenderer root;

    public BowModel() {
        super(RenderType::getEntityCutout);
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.root = new ModelRenderer(this, 6, 72);
        this.root.setRotationPoint(0.0F, 0.5F, -3.25F);
        this.root.addBox(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);
        final ModelRenderer bone = new ModelRenderer(this, 0, 77);
        bone.setRotationPoint(-1.0F, 1.0F, 0.0F);
        setRotationAngle(bone, 0.0F, 0.1745F, -0.5236F);
        bone.addBox(-5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, 0.0F, false);
        this.root.addChild(bone);
        final ModelRenderer bone2 = new ModelRenderer(this, 0, 77);
        bone2.setRotationPoint(1.0F, 1.0F, 0.0F);
        setRotationAngle(bone2, 0.0F, -0.1745F, 0.5236F);
        bone2.addBox(0.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, 0.0F, true);
        this.root.addChild(bone2);
        final ModelRenderer bone3 = new ModelRenderer(this, 0, 72);
        bone3.setRotationPoint(0.0F, -1.0F, 0.0F);
        setRotationAngle(bone3, 0.0873F, 0.0873F, -0.1745F);
        bone3.addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        this.root.addChild(bone3);
        final ModelRenderer bone4 = new ModelRenderer(this, 0, 72);
        bone4.setRotationPoint(0.0F, -1.0F, 0.0F);
        setRotationAngle(bone4, 0.0873F, -0.0873F, 0.1745F);
        bone4.addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, true);
        this.root.addChild(bone4);
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.root.render(matrix, builder, light, overlay, r, g, b, a);
    }

    private static void setRotationAngle(final ModelRenderer renderer, final float x, final float y, final float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
