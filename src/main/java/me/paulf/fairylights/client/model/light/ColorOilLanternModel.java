package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ColorOilLanternModel extends ColorLightModel {
    public ColorOilLanternModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().yRot = -Mth.PI / 2.0F;
        helper.unlit().setTextureOffset(10, 6);
        helper.unlit().addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        helper.unlit().setTextureOffset(0, 8);
        helper.unlit().addBox(-1.5F, -1, -1.5F, 3, 1, 3);
        helper.unlit().setTextureOffset(16, 18);
        helper.unlit().addBox(-3, -10.5F, -3, 6, 2, 6);
        helper.unlit().setTextureOffset(0, 12);
        helper.unlit().addBox(-1.5F, -9.5F, -1.5F, 3, 2, 3);
        helper.unlit().setTextureOffset(38, 7);
        helper.unlit().addBox(-0.5F, -9, -3.5F, 1, 9, 1);
        helper.unlit().setTextureOffset(42, 7);
        helper.unlit().addBox(-0.5F, -9, 2.5F, 1, 9, 1);
        helper.unlit().setTextureOffset(38, 0);
        helper.unlit().addBox(-0.5F, -0.5F, -3, 1, 1, 6);
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(0, 17);
        bulb.addBox(-2, -7.5F, -2, 4, 6, 4);
        bulb.setUV(6, 0);
        bulb.addBox(-1, -1.5F, -1, 2, 1, 2);
        return helper.build();
    }
}
