package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ColorCandleLanternModel extends ColorLightModel {
    public ColorCandleLanternModel(final ModelPart root) {
        super(root);
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
            frame.xRot = 5 * FLMth.DEG_TO_RAD;
            frame.yRot = (90 * i + 45) * FLMth.DEG_TO_RAD;
            helper.unlit().addChild(frame);
        }
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(48, 6);
        bulb.addBox(-2, -7.5F, -2, 4, 5, 4, 0.0F, 0.2F);
        return helper.build();
    }
}
