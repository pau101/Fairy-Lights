package me.paulf.fairylights.client.model.light;

public class OrbLanternModel extends ColorLightModel {
    public OrbLanternModel() {
        this.unlit.func_78784_a(30, 6);
        this.unlit.func_228301_a_(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(0, 27);
        bulb.addBox(-3.5F, -7.5F, -3.5F, 7, 7, 7);
    }
}
