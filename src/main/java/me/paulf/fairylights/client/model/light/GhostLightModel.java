package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class GhostLightModel extends ColorLightModel {
    public GhostLightModel() {
        final ModelRenderer littleFace = new ModelRenderer(this, 40, 17);
        littleFace.setRotationPoint(0.0F, -1.0F, -2.25F);
        littleFace.addBox(-1.5F, -1.5F, 0, 3, 3, 0, 0);
        littleFace.rotateAngleX = Mth.PI;
        littleFace.rotateAngleY = Mth.PI;
        this.lit.addChild(littleFace);
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder bodyTop = bulb.createChild(52, 48);
        bodyTop.setPosition(0.0F, 2.0F, 0.0F);
        bodyTop.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 1.0F, 3.0F);
        bodyTop.setAngles(Mth.PI, 0.0F, 0.0F);
        final BulbBuilder body = bulb.createChild(46, 40);
        body.setPosition(0.0F, 1.0F, 0.0F);
        body.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F);
        body.setAngles(Mth.PI, 0.0F, 0.0F);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternion droop = vec.rotation(-Mth.PI / 3.0F);
        final int finCount = 8;
        for (int i = 0; i < finCount; i++) {
            final BulbBuilder fin = bulb.createChild(40, 21);
            final Quaternion q = Vector3f.YP.rotation(i * Mth.TAU / finCount);
            q.multiply(droop);
            final float[] magicAngles = toEuler(q);
            final float theta = i * Mth.TAU / finCount;
            fin.setPosition(MathHelper.cos(-theta + Mth.PI / 4) * 1.1F, -2.75F, MathHelper.sin(-theta + Mth.PI / 4.0F) * 1.1F);
            fin.addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 2.0F, -0.1F);
            fin.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
    }
}
