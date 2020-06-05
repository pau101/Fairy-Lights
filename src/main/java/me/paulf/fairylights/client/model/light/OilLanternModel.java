package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;

public class OilLanternModel extends ColorLightModel {
    public OilLanternModel() {
        this.unlit.rotateAngleY = -Mth.PI / 2.0F;
        this.unlit.setTextureOffset(10, 6);
        this.unlit.addBox(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.setTextureOffset(0, 8);
        this.unlit.addBox(-1.5F, -1, -1.5F, 3, 1, 3);
        this.unlit.setTextureOffset(16, 18);
        this.unlit.addBox(-3, -10.5F, -3, 6, 2, 6);
        this.unlit.setTextureOffset(0, 12);
        this.unlit.addBox(-1.5F, -9.5F, -1.5F, 3, 2, 3);
        this.unlit.setTextureOffset(38, 7);
        this.unlit.addBox(-0.5F, -9, -3.5F, 1, 9, 1);
        this.unlit.setTextureOffset(42, 7);
        this.unlit.addBox(-0.5F, -9, 2.5F, 1, 9, 1);
        this.unlit.setTextureOffset(38, 0);
        this.unlit.addBox(-0.5F, -0.5F, -3, 1, 1, 6);
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(0, 17);
        bulb.addBox(-2, -7.5F, -2, 4, 6, 4);
        bulb.setUV(6, 0);
        bulb.addBox(-1, -1.5F, -1, 2, 1, 2);
    }
}
