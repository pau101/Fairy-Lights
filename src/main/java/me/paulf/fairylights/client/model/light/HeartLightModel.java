package me.paulf.fairylights.client.model.light;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class HeartLightModel extends ColorLightModel {
    public HeartLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final BulbBuilder bulb = helper.createBulb();
        bulb.setPosition(-5.0F, -2.0F, 0.0F);
        bulb.setAngles(0.0F, 0.0F, -0.7854F);
        bulb.setUV(66 + 0, 38 + 0).addBox(3.0F, 1.0F, -1.0F, 3.0F, 3.0F, 2.0F);
        bulb.setUV(66 + 0, 38 + 5).addBox(3.02F, 3.98F, -1.0F, 3.0F, 2.0F, 2.0F, -0.02F, 0.65F);
        bulb.setUV(66 + 10, 38 + 0).addBox(1.02F, 0.98F, -1.0F, 2.0F, 3.0F, 2.0F, -0.02F, 0.65F);
        helper.unlit().setTextureOffset(66 + 10, 38 + 5).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F);
        return helper.build();
    }
}
