package me.paulf.fairylights.client.model.light;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class MoonLightModel extends ColorLightModel {
    public MoonLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        int u = 76, v = 60;
        helper.unlit().setTextureOffset(u + 14, v + 0).addBox(-0.5F, 5.0F -7.0F, -0.5F, 1.0F, 3.0F, 1.0F);
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(u + 0, v + 0).addBox(0.0F, -1.0F -7.0F, -1.5F, 4.0F, 4.0F, 3.0F, 0.1F);
        bulb.setUV(u + 0, v + 7).addBox(-2.5F, 3.0F -7.0F, -1.5F, 5.0F, 2.0F, 3.0F);
        bulb.setUV(u + 0, v + 12).addBox(-2.5F, -3.0F -7.0F, -1.5F, 5.0F, 2.0F, 3.0F);
        return helper.build();
    }
}
