package me.paulf.fairylights.client.model.light;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class FairyLightModel extends ColorLightModel {
    public FairyLightModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(46, 0);
        bulb.addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
        return helper.build();
    }
}
