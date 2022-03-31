package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class BowModel extends Model {
    private final ModelRenderer root;

    public BowModel() {
        super(RenderType::func_228638_b_);
        this.field_78090_t = 128;
        this.field_78089_u = 128;
        this.root = new ModelRenderer(this, 6, 72);
        this.root.func_78793_a(0.0F, 0.5F, -3.25F);
        this.root.func_228303_a_(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);
        final ModelRenderer bone = new ModelRenderer(this, 0, 77);
        bone.func_78793_a(-1.0F, 1.0F, 0.0F);
        setRotationAngle(bone, 0.0F, 0.1745F, -0.5236F);
        bone.func_228303_a_(-5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, 0.0F, false);
        this.root.func_78792_a(bone);
        final ModelRenderer bone2 = new ModelRenderer(this, 0, 77);
        bone2.func_78793_a(1.0F, 1.0F, 0.0F);
        setRotationAngle(bone2, 0.0F, -0.1745F, 0.5236F);
        bone2.func_228303_a_(0.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, 0.0F, true);
        this.root.func_78792_a(bone2);
        final ModelRenderer bone3 = new ModelRenderer(this, 0, 72);
        bone3.func_78793_a(0.0F, -1.0F, 0.0F);
        setRotationAngle(bone3, 0.0873F, 0.0873F, -0.1745F);
        bone3.func_228303_a_(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        this.root.func_78792_a(bone3);
        final ModelRenderer bone4 = new ModelRenderer(this, 0, 72);
        bone4.func_78793_a(0.0F, -1.0F, 0.0F);
        setRotationAngle(bone4, 0.0873F, -0.0873F, 0.1745F);
        bone4.func_228303_a_(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, 0.0F, true);
        this.root.func_78792_a(bone4);
    }

    @Override
    public void func_225598_a_(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        this.root.func_228309_a_(matrix, builder, light, overlay, r, g, b, a);
    }

    private static void setRotationAngle(final ModelRenderer renderer, final float x, final float y, final float z) {
        renderer.field_78795_f = x;
        renderer.field_78796_g = y;
        renderer.field_78808_h = z;
    }
}
