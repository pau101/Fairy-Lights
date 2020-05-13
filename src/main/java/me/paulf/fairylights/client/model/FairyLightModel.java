package me.paulf.fairylights.client.model;

public class FairyLightModel extends LightModel {
    @Override
    protected void build(final BulbBuilder bulb) {
        super.build(bulb);
        bulb.setUV(46, 0);
        bulb.addCuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
    }
}
