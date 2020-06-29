package me.paulf.fairylights.client.model.light;

public class StarLightModel extends ColorLightModel {
    public StarLightModel() {
        final int u = 101, v = 73;
        this.unlit.setTextureOffset(u + 8, v + 0).addBox(-0.5F, 5.0F-7.0f, -0.5F, 1.0F, 3.0F, 1.0F);
        final BulbBuilder bulb = this.createBulb();
        bulb.setPosition(0.0F, 1.0F-7.0f, 0.0F);
        bulb.setUV(u, v).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.2F);
        bulb.setUV(u + 0, v + 5).addBox(-1.0F, 1.07F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F);
        bulb.setUV(u + 0, v + 8).addBox(-0.5F, 3.035F, -1.0F, 1.0F, 1.0F, 2.0F, -0.035F);
        final BulbBuilder b1 = bulb.createChild(u, v);
        b1.setAngles(0.0F, 0.0F, -1.2566F);
        b1.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.02F);
        b1.setUV(u + 0, v + 8).addBox(-0.5F, 2.965F, -1.0F, 1.0F, 1.0F, 2.0F, -0.035F);
        final BulbBuilder b2 = bulb.createChild(u, v);
        b2.setAngles(0.0F, 0.0F, -2.5133F);
        b2.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F);
        b2.setUV(u + 0, v + 8).addBox(-0.5F, 2.965F, -1.0F, 1.0F, 1.0F, 2.0F, -0.035F);
        final BulbBuilder b3 = bulb.createChild(u, v);
        b3.setAngles(0.0F, 0.0F, 2.5133F);
        b3.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.02F);
        b3.setUV(u + 0, v + 8).addBox(-0.5F, 2.965F, -1.0F, 1.0F, 1.0F, 2.0F, -0.035F);
        final BulbBuilder b4 = bulb.createChild(u, v);
        b4.setAngles(0.0F, 0.0F, 1.2566F);
        b4.setUV(u + 0, v + 4).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, -0.02F);
        b4.setUV(u + 0, v + 8).addBox(-0.5F, 2.965F, -1.0F, 1.0F, 1.0F, 2.0F, -0.035F);
    }
}
