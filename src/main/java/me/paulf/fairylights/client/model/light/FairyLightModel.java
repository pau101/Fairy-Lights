package me.paulf.fairylights.client.model.light;

public class FairyLightModel extends LightModel {
    public FairyLightModel() {
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(46, 0);
        bulb.addCuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
    }
}
