package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class WitchLightModel extends LightModel {
    public WitchLightModel() {
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder tip = bulb.createChild(15, 54);
        tip.addBox(-1, 0, -1, 2, 2, 2, 0.075F);
        final BulbBuilder middleTop = bulb.createChild(52, 52);
        middleTop.setPosition(0, -3, 0);
        middleTop.addBox(-1.5F, 0, -1.5F, 3, 3, 3, 0);
        final BulbBuilder middleBottom = middleTop.createChild(56, 58);
        middleBottom.setPosition(0, -2, 0);
        middleBottom.addBox(-2, 0, -2, 4, 2, 4, 0);
        final BulbBuilder rim = middleBottom.createChild(58, 7);
        rim.setPosition(0, -1, 0);
        rim.addBox(-4.0F, 0, -4.0F, 8, 1, 8, 0);
        final ModelRenderer belt = new ModelRenderer(this, 62, 0);
        belt.setRotationPoint(0, -4.5F, 0);
        belt.addBox(-2.5F, 0, -2.5F, 5, 1, 5, 0);
        this.unlit.addChild(belt);
        final ModelRenderer buckle = new ModelRenderer(this, 0, 27);
        buckle.setRotationPoint(0, -0.6F, -2.9F);
        buckle.rotateAngleY = -Mth.HALF_PI;
        buckle.addBox(0, 0, -1, 1, 2, 2, 0);
        belt.addChild(buckle);
        final ModelRenderer beltPoke = new ModelRenderer(this, 66, 4);
        beltPoke.setRotationPoint(0.2F, 0.5F, -0.5F);
        beltPoke.addBox(0, 0, 0, 1, 1, 1, 0);
        buckle.addChild(beltPoke);
    }
}
