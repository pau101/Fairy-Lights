package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ModelRenderer;

public class JackOLanternLightModel extends ColorLightModel {
    public JackOLanternLightModel() {
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder pumpkin = bulb.createChild(28, 42);
        pumpkin.addBox(-3, 0, -3, 6, 6, 6, 0);
        pumpkin.setAngles(Mth.PI, 0.0F, 0.0F);
        final ModelRenderer leaf1 = new ModelRenderer(this, 12, 18);
        leaf1.setRotationPoint(0.5F, 0, 0.5F);
        leaf1.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternion droop = vec.rotation(Mth.PI / 12.0F);
        float[] leafAngles = toEuler(droop);
        leaf1.rotateAngleX = leafAngles[0];
        leaf1.rotateAngleY = leafAngles[1];
        leaf1.rotateAngleZ = leafAngles[2];
        this.unlit.addChild(leaf1);
        final ModelRenderer leaf2 = new ModelRenderer(this, 12, 18);
        leaf2.setRotationPoint(-0.5F, 0, -0.5F);
        leaf2.addBox(0, -0.5F, 0, 2, 1, 2, 0);
        final Quaternion q = Vector3f.YP.rotation(Mth.PI);
        q.multiply(droop);
        leafAngles = toEuler(q);
        leaf2.rotateAngleX = leafAngles[0];
        leaf2.rotateAngleY = leafAngles[1];
        leaf2.rotateAngleZ = leafAngles[2];
        this.unlit.addChild(leaf2);
        final ModelRenderer stem = new ModelRenderer(this, 21, 41);
        stem.setRotationPoint(0, 2, 0);
        stem.addBox(-1, 0, -1, 2, 2, 2, -0.05F);
        stem.rotateAngleX = Mth.PI;
        this.unlit.addChild(stem);
        final ModelRenderer face = new ModelRenderer(this, 56, 34);
        face.setRotationPoint(0, -3, -3.25F);
        face.addBox(-3, -3, 0, 6, 6, 0, 0);
        face.rotateAngleX = Mth.PI;
        face.rotateAngleY = Mth.PI;
        this.lit.addChild(face);
    }
}
