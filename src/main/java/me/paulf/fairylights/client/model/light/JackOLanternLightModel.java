package me.paulf.fairylights.client.model.light;


import com.mojang.math.Axis;
import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JackOLanternLightModel extends ColorLightModel {
    public JackOLanternLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final BulbBuilder bulb = helper.createBulb();
        final BulbBuilder pumpkin = bulb.createChild("pumpkin", 28, 42);
        pumpkin.addBox(-3, 0, -3, 6, 6, 6, 0);
        pumpkin.setAngles(FLMth.PI, 0.0F, 0.0F);
        final EasyMeshBuilder leaf1 = new EasyMeshBuilder("leaf1", 12, 18);
        leaf1.setRotationPoint(0.5F, 0, 0.5F);
        leaf1.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternionf droop = Axis.of(vec).rotation(FLMth.PI / 12.0F);
        float[] leafAngles = toEuler(droop);
        leaf1.xRot = leafAngles[0];
        leaf1.yRot = leafAngles[1];
        leaf1.zRot = leafAngles[2];
        helper.unlit().addChild(leaf1);
        final EasyMeshBuilder leaf2 = new EasyMeshBuilder("leaf2", 12, 18);
        leaf2.setRotationPoint(-0.5F, 0, -0.5F);
        leaf2.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        final Quaternionf q = Axis.YP.rotation(FLMth.PI);
        q.mul(droop);
        leafAngles = toEuler(q);
        leaf2.xRot = leafAngles[0];
        leaf2.yRot = leafAngles[1];
        leaf2.zRot = leafAngles[2];
        helper.unlit().addChild(leaf2);
        final EasyMeshBuilder stem = new EasyMeshBuilder("stem", 21, 41);
        stem.setRotationPoint(0, 2, 0);
        stem.addBox(-1, 0, -1, 2, 2, 2, -0.05F);
        stem.xRot = FLMth.PI;
        helper.unlit().addChild(stem);
        final EasyMeshBuilder face = new EasyMeshBuilder("face", 56, 34);
        face.setRotationPoint(0, -3, -3.25F);
        face.addBox(-3, -3, 0, 6, 6, 0, 0);
        face.xRot = FLMth.PI;
        face.yRot = FLMth.PI;
        helper.lit().addChild(face);
        return helper.build();
    }
}
