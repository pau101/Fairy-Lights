package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class PaperLanternModel extends ColorLightModel {
    public PaperLanternModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().setTextureOffset(34, 18);
        helper.unlit().addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        helper.unlit().setTextureOffset(21, 26);
        helper.unlit().addBox(-1, -9.5F, -1, 2, 3, 2);
        helper.unlit().setTextureOffset(58, 0);
        helper.unlit().addBox(-0.5F, -14.5F, -0.5F, 1, 5, 1);
        for (int i = 0; i < 8; i++) {
            final boolean straight = (i & 1) == 0;
            final EasyMeshBuilder hSupport = new EasyMeshBuilder("hSupport_" + i, 28, 34);
            hSupport.addBox(0, 0, -0.5F, straight ? 4 : 5, 7, 1);
            hSupport.yRot = 45 * i * Mth.DEG_TO_RAD;
            hSupport.y = -7;
            helper.unlit().addChild(hSupport);
        }
        final BulbBuilder bulb = helper.createBulb();
        bulb.setUV(0, 41);
        bulb.addBox(-3.5F, -6.5F, -3.5F, 7, 6, 7, 0.0F, 0.3F);
        return helper.build();
    }
}
