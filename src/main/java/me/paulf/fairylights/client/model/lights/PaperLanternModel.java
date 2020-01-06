package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.util.Mth;

public final class PaperLanternModel extends LightModel {
    public PaperLanternModel() {
        this.amutachromicParts.setTextureOffset(34, 18);
        this.amutachromicParts.addBox(-1, -0.5F, -1, 2, 2, 2); // point

        this.amutachromicParts.setTextureOffset(21, 26);
        this.amutachromicParts.addBox(-1, -9.5F, -1, 2, 3, 2); // base

        this.amutachromicParts.setTextureOffset(58, 0);
        this.amutachromicParts.addBox(-0.5F, -14.5F, -0.5F, 1, 5, 1); // string

        for (int i = 0; i < 8; i++) {
            final boolean straight = (i & 1) == 0;
            final AdvancedRendererModel hSupport = new AdvancedRendererModel(this, 28, 34);
            hSupport.addBox(0, 0, -0.5F, straight ? 4 : 5, 7, 1);
            hSupport.rotateAngleY = 45 * i * Mth.DEG_TO_RAD;
            hSupport.rotationPointY = -7;
            this.amutachromicParts.addChild(hSupport);
        }

        this.colorableParts.setTextureOffset(0, 41);
        this.colorableParts.addBox(-3.5F, -6.5F, -3.5F, 7, 6, 7); // orb
        this.colorableParts.glowExpandAmount = 0.3F;
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }
}
