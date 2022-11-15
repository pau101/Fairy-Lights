package me.paulf.fairylights.client.model.light;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class StarLightModel extends ColorLightModel {
    public StarLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final int u = 101, v = 73;
        helper.unlit().setTextureOffset(u + 12, v + 0).addBox(-0.5F, 5.0F-7.0f, -0.5F, 1.0F, 3.0F, 1.0F);
        final BulbBuilder bulb = helper.createBulb();
        bulb.setPosition(0.0F, 1.0F-7.0f, 0.0F);
        final BulbBuilder center = bulb.createChild("center", u, v);
        center.setAngles(0.0F, 0.0F, 0.7854F);
        center.addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, 0.15F);
        bulb.setUV(u + 0, v + 4).addBox(-1.0F, 1.07F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F);
        final BulbBuilder b1 = bulb.createChild("b1", u, v);
        b1.setAngles(0.0F, 0.0F, -1.2566F);
        b1.setUV(u + 0, v + 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.02F);
        final BulbBuilder b2 = bulb.createChild("b2", u, v);
        b2.setAngles(0.0F, 0.0F, -2.5133F);
        b2.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F);
        final BulbBuilder b3 = bulb.createChild("b3", u, v);
        b3.setAngles(0.0F, 0.0F, 2.5133F);
        b3.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.02F);
        final BulbBuilder b4 = bulb.createChild("b4", u, v);
        b4.setAngles(0.0F, 0.0F, 1.2566F);
        b4.setUV(u + 0, v + 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, -0.02F);
        return helper.build();
    }
}
