package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.BrightLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.AxisAlignedBB;

public class IncandescentLightModel extends LightModel<BrightLightBehavior> {
    final ModelRenderer bulb;

    final ModelRenderer bulbGlow;

    final ModelRenderer filament;

    public IncandescentLightModel() {
        this.unlit.setTextureOffset(90, 10);
        this.unlit.addBox(-1.0F, -0.01F, -1.0F, 2.0F, 1.0F, 2.0F);
        this.bulb = new ModelRenderer(this, 98, 10);
        this.bulb.addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F);
        this.bulbGlow = new ModelRenderer(this, 98, 10);
        this.bulbGlow.addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.65F);
        this.filament = new ModelRenderer(this, 90, 13);
        this.filament.addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F);
    }

    @Override
    public AxisAlignedBB getBounds() {
        return super.getBounds(true);
    }

    @Override
    public void animate(final Light<BrightLightBehavior> light, final float delta) {
        super.animate(light, delta);
        this.brightness = light.getBehavior().getBrightness(delta);
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        super.render(matrix, builder, light, overlay, r, g, b, a);
        final int emissiveLight = this.getLight(light);
        final float cr = 0.23F, cg = 0.18F, cb = 0.14F;
        final float br = this.brightness;
        this.filament.render(matrix, builder, emissiveLight, overlay, r * (cr * (1.0F - br) + br), g * (cg * (1.0F - br) + br), b * (cb * (1.0F - br) + br), a);
    }

    @Override
    public void renderTranslucent(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        final float bi = this.brightness;
        final int emissiveLight = this.getLight(light);
        final float br = 1.0F, bg = 0.94F, bb = 0.79F;
        this.bulb.render(matrix, builder, emissiveLight, overlay, r * (br * bi + (1.0F - bi)), g * (bg * bi + (1.0F - bi)), b * (bb * bi + (1.0F - bi)), bi * 0.75F + 0.2F);
        final float cr = 1.0F, cg = 0.77F, cb = 0.25F;
        this.bulbGlow.render(matrix, builder, emissiveLight, overlay, r * cr, g * cg, b * cb, bi * 0.3F);
        super.renderTranslucent(matrix, builder, light, overlay, r, g, b, a);
    }
}
