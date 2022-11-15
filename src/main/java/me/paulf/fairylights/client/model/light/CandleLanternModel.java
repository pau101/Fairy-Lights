package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.server.feature.light.BrightnessLightBehavior;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class CandleLanternModel extends LightModel<BrightnessLightBehavior> {
    public CandleLanternModel(ModelPart root) {
        super(root);
    }

    @Override
    public void animate(final Light<?> light, final BrightnessLightBehavior behavior, final float delta) {
        super.animate(light, behavior, delta);
        this.brightness = behavior.getBrightness(delta);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().setTextureOffset(21, 0);
        helper.unlit().addBox(-1, 0.5F, -1, 2, 1, 2, -0.05F);
        helper.unlit().setTextureOffset(0, 3);
        helper.unlit().addBox(-1.5F, -1.5F, -1.5F, 3, 2, 3);
        helper.unlit().setTextureOffset(43, 15);
        helper.unlit().addBox(-2.5F, -1.75F, -2.5F, 5, 1, 5);
        helper.unlit().setTextureOffset(23, 27);
        helper.unlit().addBox(-3, -2.5F, -3, 6, 1, 6);
        helper.unlit().setTextureOffset(43, 21);
        helper.unlit().addBox(-2.5F, -8.5F, -2.5F, 5, 1, 5);
        for (int i = 0; i < 4; i++) {
            final EasyMeshBuilder frame = new EasyMeshBuilder("frame_" + i, 4 * i + 47, 27);
            frame.addBox(-0.5F, 0, -0.5F, 1, 6, 1);
            frame.setRotationPoint(2.1F * ((i & 2) == 0 ? 1 : -1), -8F, 2.1F * ((i + 1 & 2) == 0 ? 1 : -1));
            frame.xRot = 5 * Mth.DEG_TO_RAD;
            frame.yRot = (90 * i + 45) * Mth.DEG_TO_RAD;
            helper.unlit().addChild(frame);
        }
        helper.lit().setTextureOffset(63, 26);
        helper.lit().addBox(-2, -7.5F, -2, 4, 5, 4);
        helper.lit().setTextureOffset(79, 28);
        helper.lit().addBox(-1, -7.5F, -1, 2, 2, 2);
        helper.lit().setTextureOffset(81, 26);
        helper.lit().addBox(-1, -5.5F, 0, 2, 2, 0);
        return helper.build();
    }
}
