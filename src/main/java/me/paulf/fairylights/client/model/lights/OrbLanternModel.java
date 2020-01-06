package me.paulf.fairylights.client.model.lights;

public final class OrbLanternModel extends LightModel {
    public OrbLanternModel() {
        this.amutachromicParts.setTextureOffset(30, 6);
        this.amutachromicParts.addBox(-1, -0.5F, -1, 2, 2, 2);
        this.colorableParts.setTextureOffset(0, 27);
        this.colorableParts.addBox(-3.5F, -7.5F, -3.5F, 7, 7, 7);
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }
}
