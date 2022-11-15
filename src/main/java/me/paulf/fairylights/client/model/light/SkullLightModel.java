package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class SkullLightModel extends ColorLightModel {
    public SkullLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder apertures = new EasyMeshBuilder("apertures", 12, 10);
        apertures.setRotationPoint(0, -3, -2.75F);
        apertures.addBox(-1.5F, -1, 0, 3, 2, 0, 0);
        apertures.xRot = Mth.PI;
        apertures.yRot = Mth.PI;
        helper.unlit().addChild(apertures);
        final BulbBuilder bulb = helper.createBulb();
        final BulbBuilder skull = bulb.createChild("skull", 0, 54);
        skull.addBox(-2.5F, 0, -2.5F, 5, 4, 5, 0);
        skull.setAngles(Mth.PI, 0, 0);
        final BulbBuilder mandible = bulb.createChild("mandible", 40, 34);
        mandible.setPosition(0, -3.5F, 0.3F);
        mandible.addBox(-2.5F, 0, -3, 5, 2, 3, -0.25F, 0.6F);
        mandible.setAngles(Mth.PI / 16 + Mth.PI, Mth.PI, 0);
        final BulbBuilder maxilla = bulb.createChild("maxilla", 46, 7);
        maxilla.setPosition(0, -3.875F, -2.125F);
        maxilla.setAngles(Mth.PI, 0, 0);
        maxilla.addBox(-1, 0, -0.5F, 2, 1, 1, -0.125F);
        final EasyMeshBuilder chain = new EasyMeshBuilder("chain", 34, 18);
        chain.setRotationPoint(0, 2, 0);
        chain.addBox(-1, 0, -1, 2, 2, 2, -0.05F);
        chain.xRot = Mth.PI;
        helper.lit().addChild(chain);
        return helper.build();
    }
}
