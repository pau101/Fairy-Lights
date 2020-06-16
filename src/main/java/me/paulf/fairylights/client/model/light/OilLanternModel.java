package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.fastener.connection.type.hanginglights.BrightLightBehavior;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;

public class OilLanternModel extends LightModel<BrightLightBehavior> {
    public OilLanternModel() {
        this.unlit.rotateAngleY = -Mth.PI / 2.0F;
        this.unlit.setTextureOffset(10, 6);
        this.unlit.addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.setTextureOffset(0, 8);
        this.unlit.addBox(-1.5F, -1, -1.5F, 3, 1, 3);
        this.unlit.setTextureOffset(16, 18);
        this.unlit.addBox(-3, -10.5F, -3, 6, 2, 6);
        this.unlit.setTextureOffset(0, 12);
        this.unlit.addBox(-1.5F, -9.5F, -1.5F, 3, 2, 3);
        this.unlit.setTextureOffset(38, 7);
        this.unlit.addBox(-0.5F, -9, -3.5F, 1, 9, 1);
        this.unlit.setTextureOffset(42, 7);
        this.unlit.addBox(-0.5F, -9, 2.5F, 1, 9, 1);
        this.unlit.setTextureOffset(38, 0);
        this.unlit.addBox(-0.5F, -0.5F, -3, 1, 1, 6);
        this.lit.setTextureOffset(63, 16);
        this.lit.addBox(-2, -7.5F, -2, 4, 6, 4);
        this.lit.setTextureOffset(79, 17);
        this.lit.addBox(-1, -1.5F, -1, 2, 1, 2);
        this.lit.setTextureOffset(79, 20);
        this.lit.addBox(-1.0F, -7.5F, -1.0F, 2.0F, 4.0F, 2.0F);
    }

    @Override
    public void animate(final Light<?> light, final BrightLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
    }
}
