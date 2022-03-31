package me.paulf.fairylights.client.model.light;

public class HeartLightModel extends ColorLightModel {
    public HeartLightModel() {
        final BulbBuilder bulb = this.createBulb();
        bulb.setPosition(-5.0F, -2.0F, 0.0F);
        bulb.setAngles(0.0F, 0.0F, -0.7854F);
        bulb.setUV(66 + 0, 38 + 0).addBox(3.0F, 1.0F, -1.0F, 3.0F, 3.0F, 2.0F);
        bulb.setUV(66 + 0, 38 + 5).addBox(3.02F, 3.98F, -1.0F, 3.0F, 2.0F, 2.0F, -0.02F, 0.65F);
        bulb.setUV(66 + 10, 38 + 0).addBox(1.02F, 0.98F, -1.0F, 2.0F, 3.0F, 2.0F, -0.02F, 0.65F);
        this.unlit.func_78784_a(66 + 10, 38 + 5).func_228300_a_(-0.5F, -2.0F, -0.5F, 1.0F, 3.0F, 1.0F);
    }
}
