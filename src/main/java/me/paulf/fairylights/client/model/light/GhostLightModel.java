package me.paulf.fairylights.client.model.light;

import com.mojang.math.Axis;
import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GhostLightModel extends ColorLightModel {
    public GhostLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder littleFace = new EasyMeshBuilder("little_face", 40, 17);
        littleFace.setRotationPoint(0.0F, -1.0F, -2.25F);
        littleFace.addBox(-1.5F, -1.5F, 0, 3, 3, 0, 0);
        littleFace.xRot = FLMth.PI;
        littleFace.yRot = FLMth.PI;
        helper.lit().addChild(littleFace);
        final BulbBuilder bulb = helper.createBulb();
        final BulbBuilder bodyTop = bulb.createChild("body_top", 52, 48);
        bodyTop.setPosition(0.0F, 2.0F, 0.0F);
        bodyTop.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 1.0F, 3.0F);
        bodyTop.setAngles(FLMth.PI, 0.0F, 0.0F);
        final BulbBuilder body = bulb.createChild("body", 46, 40);
        body.setPosition(0.0F, 1.0F, 0.0F);
        body.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F);
        body.setAngles(FLMth.PI, 0.0F, 0.0F);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternionf droop = Axis.of(vec).rotation(-FLMth.PI / 3.0F);
        final int finCount = 8;
        for (int i = 0; i < finCount; i++) {
            final BulbBuilder fin = bulb.createChild("fin_" + i, 40, 21);
            final Quaternionf q = Axis.YP.rotation(i * FLMth.TAU / finCount);
            q.mul(droop);
            final float[] magicAngles = toEuler(q);
            final float theta = i * FLMth.TAU / finCount;
            fin.setPosition(Mth.cos(-theta + FLMth.PI / 4) * 1.1F, -2.75F, Mth.sin(-theta + FLMth.PI / 4.0F) * 1.1F);
            fin.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 2.0F, -0.1F);
            fin.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
        return helper.build();
    }
}
