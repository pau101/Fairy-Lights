package me.paulf.fairylights.client.model.light;

public class MoonLightModel extends ColorLightModel {
    public MoonLightModel() {
        int u = 76, v = 60;
        this.unlit.func_78784_a(u + 14, v + 0).func_228300_a_(-0.5F, 5.0F -7.0F, -0.5F, 1.0F, 3.0F, 1.0F);
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(u + 0, v + 0).addBox(0.0F, -1.0F -7.0F, -1.5F, 4.0F, 4.0F, 3.0F, 0.1F);
        bulb.setUV(u + 0, v + 7).addBox(-2.5F, 3.0F -7.0F, -1.5F, 5.0F, 2.0F, 3.0F);
        bulb.setUV(u + 0, v + 12).addBox(-2.5F, -3.0F -7.0F, -1.5F, 5.0F, 2.0F, 3.0F);
    }
}
