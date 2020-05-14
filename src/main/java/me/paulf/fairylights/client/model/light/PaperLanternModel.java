package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PaperLanternModel extends LightModel {
    public PaperLanternModel() {
        this.unlit.setTextureOffset(34, 18);
        this.unlit.addCuboid(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.setTextureOffset(21, 26);
        this.unlit.addCuboid(-1, -9.5F, -1, 2, 3, 2);
        this.unlit.setTextureOffset(58, 0);
        this.unlit.addCuboid(-0.5F, -14.5F, -0.5F, 1, 5, 1);
        for (int i = 0; i < 8; i++) {
            final boolean straight = (i & 1) == 0;
            final ModelRenderer hSupport = new ModelRenderer(this, 28, 34);
            hSupport.addCuboid(0, 0, -0.5F, straight ? 4 : 5, 7, 1);
            hSupport.rotateAngleY = 45 * i * Mth.DEG_TO_RAD;
            hSupport.rotationPointY = -7;
            this.unlit.addChild(hSupport);
        }
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(0, 41);
        bulb.addCuboid(-3.5F, -6.5F, -3.5F, 7, 6, 7, 0.0F, 0.3F);
    }
}
