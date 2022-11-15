package me.paulf.fairylights.client.model.light;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class OrbLanternModel extends ColorLightModel {
    public OrbLanternModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().setTextureOffset(30, 6);
        helper.unlit().addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(0, 27);
        bulb.addBox(-3.5F, -7.5F, -3.5F, 7, 7, 7);
        return helper.build();
    }
}
