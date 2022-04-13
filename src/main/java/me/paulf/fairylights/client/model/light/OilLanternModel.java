package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.feature.light.BrightnessLightBehavior;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.util.FLMath;

public class OilLanternModel extends LightModel<BrightnessLightBehavior> {
    public OilLanternModel() {
        this.unlit.field_78796_g = -FLMath.PI / 2.0F;
        this.unlit.func_78784_a(10, 6);
        this.unlit.func_228301_a_(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.func_78784_a(0, 8);
        this.unlit.func_228300_a_(-1.5F, -1, -1.5F, 3, 1, 3);
        this.unlit.func_78784_a(16, 18);
        this.unlit.func_228300_a_(-3, -10.5F, -3, 6, 2, 6);
        this.unlit.func_78784_a(0, 12);
        this.unlit.func_228300_a_(-1.5F, -9.5F, -1.5F, 3, 2, 3);
        this.unlit.func_78784_a(38, 7);
        this.unlit.func_228300_a_(-0.5F, -9, -3.5F, 1, 9, 1);
        this.unlit.func_78784_a(42, 7);
        this.unlit.func_228300_a_(-0.5F, -9, 2.5F, 1, 9, 1);
        this.unlit.func_78784_a(38, 0);
        this.unlit.func_228300_a_(-0.5F, -0.5F, -3, 1, 1, 6);
        this.lit.func_78784_a(63, 16);
        this.lit.func_228300_a_(-2, -7.5F, -2, 4, 6, 4);
        this.lit.func_78784_a(79, 17);
        this.lit.func_228300_a_(-1, -1.5F, -1, 2, 1, 2);
        this.lit.func_78784_a(79, 20);
        this.lit.func_228300_a_(-1.0F, -7.5F, -1.0F, 2.0F, 4.0F, 2.0F);
    }

    @Override
    public void animate(final Light<?> light, final BrightnessLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
    }
}
