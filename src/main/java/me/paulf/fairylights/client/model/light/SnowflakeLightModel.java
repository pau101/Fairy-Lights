package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Random;

public class SnowflakeLightModel extends ColorLightModel {
    public SnowflakeLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder connector = new EasyMeshBuilder("connector", 90, 40);
        connector.addBox(-1.0F, 0.2F, -1.0F, 2.0F, 1.0F, 2.0F, -0.05F);
        helper.unlit().addChild(connector);
        final BulbBuilder bulb = helper.createBulb();
        final Random rng = new Random(0xFE337752);
        final float size = 8.0F;
        final int branches = 6;
        for (int n = 0; n < branches; n++) {
            final BulbBuilder branch = bulb.createChild("branch_" + n, 10, 37);
            branch.addBox(-0.5F, 0.0F, -0.5F, 1.0F, size, 1.0F, rng.nextFloat() * 0.01F + 0.1F);
            branch.setPosition(0.0F, -size, 0.0F);
            branch.setAngles(0.0F, 0.0F, n * FLMth.TAU / branches);
            for (int side = -1; side <= 1; side += 2) {
                final BulbBuilder sub = branch.createChild("side_" + (side == 1), 10, 39);
                sub.addBox(-0.5F, 0.0F, -0.5F, 1.0F, size * 0.4F, 1.0F, rng.nextFloat() * 0.01F - 0.05F);
                sub.setAngles(0.0F, 0.0F, FLMth.PI * 0.75F * side);
                sub.setPosition(-0.2F * side, -size * 0.55F, 0.0F);
            }
        }
        return helper.build();
    }
}
