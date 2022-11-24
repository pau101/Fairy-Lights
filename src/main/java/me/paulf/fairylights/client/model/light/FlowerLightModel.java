package me.paulf.fairylights.client.model.light;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class FlowerLightModel extends ColorLightModel {
    public FlowerLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        helper.unlit().setTextureOffset(12, 0);
        helper.unlit().addBox(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F);
        final BulbBuilder bulb = helper.createBulb();
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternion droop = vec.rotation(-FLMth.PI / 6.0F);
        final int petalCount = 5;
        for (int p = 0; p < petalCount; p++) {
            final Quaternion q = Vector3f.YP.rotation(p * FLMth.TAU / petalCount);
            q.mul(droop);
            final float[] magicAngles = toEuler(q);
            final BulbBuilder petalModel = bulb.createChild("petal_" + p, 24, 0);
            petalModel.addBox(0.0F, 0.0F, 0.0F, 5.0F, 1.0F, 5.0F);
            petalModel.setPosition(0.0F, 1.0F, 0.0F);
            petalModel.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
        return helper.build();
    }
}
