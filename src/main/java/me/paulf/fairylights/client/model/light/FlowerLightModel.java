package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.FLMath;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class FlowerLightModel extends ColorLightModel {
    public FlowerLightModel() {
        this.unlit.func_78784_a(12, 0);
        this.unlit.func_228300_a_(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F);
        final BulbBuilder bulb = this.createBulb();
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.func_229194_d_();
        final Quaternion droop = vec.func_229193_c_(-FLMath.PI / 6.0F);
        final int peddleCount = 5;
        for (int p = 0; p < peddleCount; p++) {
            final Quaternion q = Vector3f.field_229181_d_.func_229193_c_(p * FLMath.TAU / peddleCount);
            q.func_195890_a(droop);
            final float[] magicAngles = toEuler(q);
            final BulbBuilder peddleModel = bulb.createChild(24, 0);
            peddleModel.addBox(0.0F, 0.0F, 0.0F, 5.0F, 1.0F, 5.0F);
            peddleModel.setPosition(0.0F, 1.0F, 0.0F);
            peddleModel.setAngles(magicAngles[0], magicAngles[1], magicAngles[2]);
        }
    }
}
