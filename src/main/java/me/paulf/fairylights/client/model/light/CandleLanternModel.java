package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.feature.light.BrightLightBehavior;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CandleLanternModel extends LightModel<BrightLightBehavior> {
    public CandleLanternModel() {
        this.unlit.setTextureOffset(21, 0);
        this.unlit.addBox(-1, 0.5F, -1, 2, 1, 2, -0.05F);
        this.unlit.setTextureOffset(0, 3);
        this.unlit.addBox(-1.5F, -1.5F, -1.5F, 3, 2, 3);
        this.unlit.setTextureOffset(43, 15);
        this.unlit.addBox(-2.5F, -1.75F, -2.5F, 5, 1, 5);
        this.unlit.setTextureOffset(23, 27);
        this.unlit.addBox(-3, -2.5F, -3, 6, 1, 6);
        this.unlit.setTextureOffset(43, 21);
        this.unlit.addBox(-2.5F, -8.5F, -2.5F, 5, 1, 5);
        for (int i = 0; i < 4; i++) {
            final ModelRenderer frame = new ModelRenderer(this, 4 * i + 47, 27);
            frame.addBox(-0.5F, 0, -0.5F, 1, 6, 1);
            frame.setRotationPoint(2.1F * ((i & 2) == 0 ? 1 : -1), -8F, 2.1F * ((i + 1 & 2) == 0 ? 1 : -1));
            frame.rotateAngleX = 5 * Mth.DEG_TO_RAD;
            frame.rotateAngleY = (90 * i + 45) * Mth.DEG_TO_RAD;
            this.unlit.addChild(frame);
        }
        this.lit.setTextureOffset(63, 26);
        this.lit.addBox(-2, -7.5F, -2, 4, 5, 4);
        this.lit.setTextureOffset(79, 28);
        this.lit.addBox(-1, -7.5F, -1, 2, 2, 2);
        this.lit.setTextureOffset(81, 26);
        this.lit.addBox(-1, -5.5F, 0, 2, 2, 0);
    }

    @Override
    public void animate(final Light<?> light, final BrightLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
    }
}
