package me.paulf.fairylights.client.model.light;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.MeteorLightBehavior;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class MeteorLightModel extends LightModel<MeteorLightBehavior> {
    private final BulbBuilder[] lights;

    private final ModelRenderer connector;

    private final ModelRenderer cap;

    private float stage;

    public MeteorLightModel() {
        this.connector = new ModelRenderer(this, 77, 0);
        this.connector.addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.addChild(this.connector);
        this.cap = new ModelRenderer(this, 77, 0);
        this.cap.addBox(-1, -25.45F + 0.05F, -1, 2, 1, 2, 0);
        this.unlit.addChild(this.cap);
        final int lightCount = 12;
        this.lights = new BulbBuilder[lightCount];
        final float rodScale = 0.8F;
        final BulbBuilder bulb = this.createBulb();
        for (int i = 0; i < lightCount; i++) {
            final BulbBuilder light = bulb.createChild(37, 72, (m, u, v) -> new ModelRenderer(m, u, v) {
                @Override
                public void translateRotate(final MatrixStack stack) {
                    super.translateRotate(stack);
                    stack.scale(rodScale, 1.0F, rodScale);
                }
            });
            light.addBox(-1, -i * 2 - 2.5F + 0.05F, -1, 2, 2, 2, MathHelper.sin(i * Mth.PI / lightCount) * 0.1F);
            this.lights[i] = light;
        }
    }

    @Override
    public void animate(final Light<?> light, final MeteorLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.red = behavior.getRed(delta);
        this.green = behavior.getGreen(delta);
        this.blue = behavior.getBlue(delta);
        this.stage = behavior.getProgress(delta) * 3.0F - 1.0F;
    }

    private float computeBrightness(final float t) {
        return MathHelper.clamp(t - this.stage > 0.0F ? 1.0F - Math.abs(t - this.stage) * 4.0F : 1.0F - Math.abs(t - this.stage), 0.0F, 1.0F);
    }

    @Override
    public void render(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        for (int i = 0; i < this.lights.length; i++) {
            this.brightness = this.computeBrightness((float) i / this.lights.length);
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].setVisible(i == n);
            }
            this.connector.showModel = i == 0;
            this.cap.showModel = i == this.lights.length - 1;
            super.render(matrix, builder, light, overlay, r, g, b, a);
        }
    }

    @Override
    public void renderTranslucent(final MatrixStack matrix, final IVertexBuilder builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        for (int i = 0; i < this.lights.length; i++) {
            this.brightness = this.computeBrightness((float) i / this.lights.length);
            for (int n = 0; n < this.lights.length; n++) {
                this.lights[n].setVisible(i == n);
            }
            super.renderTranslucent(matrix, builder, light, overlay, r, g, b, a);
        }
    }
}
