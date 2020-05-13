package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;

public class FlowerLightModel extends LightModel {
    public FlowerLightModel() {
        this.lit.setTextureOffset(12, 0);
        this.lit.addCuboid(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F);
    }

    @Override
    protected void build(final BulbBuilder bulb) {
        super.build(bulb);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.normalize();
        final Quaternion droop = vec.getRadialQuaternion(-Mth.PI / 6.0F);
        final int peddleCount = 5;
        for (int p = 0; p < peddleCount; p++) {
            final Quaternion q = Vector3f.POSITIVE_Y.getRadialQuaternion(p * Mth.TAU / peddleCount);
            q.multiply(droop);
            final float[] magicAngles = toEuler(q);
            final BulbBuilder peddleModel = bulb.createChild(24, 0);
            peddleModel.addCuboid(0.0F, 0.0F, 0.0F, 5.0F, 1.0F, 5.0F);
            peddleModel.setPosition(0.0F, 1.0F, 0.0F);
            peddleModel.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
    }
}
