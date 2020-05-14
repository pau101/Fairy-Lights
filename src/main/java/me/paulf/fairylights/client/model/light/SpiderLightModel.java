package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpiderLightModel extends LightModel {
    public SpiderLightModel() {
        final ModelRenderer string = new ModelRenderer(this, 30, 6);
        string.addCuboid(-1, 0, -1, 2, 2, 2, -0.05F);
        this.unlit.addChild(string);
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder abdomen = bulb.createChild(20, 54);
        abdomen.addCuboid(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0);
        final BulbBuilder pedicel = abdomen.createChild(6, 0);
        pedicel.setPosition(0, -6.0F, 0);
        pedicel.addCuboid(-1, 0, -1, 2, 1, 2, 0);
        final BulbBuilder cephalothorax = pedicel.createChild(40, 57);
        cephalothorax.setPosition(0, -2.25F, 0);
        cephalothorax.addCuboid(-2, 0, -2, 4, 3, 4, 0);
        final BulbBuilder cheliceraLeft = cephalothorax.createChild(0, 0);
        cheliceraLeft.setPosition(0, 0.3F, 0.6F);
        cheliceraLeft.addCuboid(-1, -1.5F, 0, 2, 2, 1, 0);
        cheliceraLeft.setAngles(-0.2617993877991494F, 0, 0);
        final BulbBuilder cheliceraRight = cephalothorax.createChild(0, 0);
        cheliceraRight.setPosition(0, 0.3F, -0.6F);
        cheliceraRight.addCuboid(-1, -1.5F, -1, 2, 2, 1, 0);
        cheliceraRight.setAngles(0.2617993877991494F, 0, 0);
        this.createLegs(pedicel, 0);
        this.createLegs(pedicel, 1);
    }

    private void createLegs(final BulbBuilder bulb, final int side) {
        final BulbBuilder legs = bulb.createChild(0, 0);
        legs.setAngles(0.0F, Mth.PI * side, 0.0F);
        final BulbBuilder leg1 = legs.createChild(21, 45);
        leg1.setPosition(0, 0.6F, 1.1F);
        leg1.addCuboid(0, -0.5F, -0.5F, 5, 1, 1, 0);
        leg1.setAngles(-0.13962634015954636F, -0.7853981633974483F, 1.5707963267948966F);
        final BulbBuilder leg1Lower = leg1.createChild(21, 45);
        leg1Lower.setPosition(4.59F, 0, -0.24F);
        leg1Lower.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg1Lower.setAngles(0, -1.0471975511965976F, 0);
        final BulbBuilder leg2 = legs.createChild(21, 45);
        leg2.setPosition(0, 0.6F, 1.1F);
        leg2.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg2.setAngles(0.13962634015954636F, -1.2217304763960306F, 1.5707963267948966F);
        final BulbBuilder leg2Lower = leg2.createChild(21, 45);
        leg2Lower.setPosition(3.65F, 0, -0.13F);
        leg2Lower.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg2Lower.setAngles(0, -0.7853981633974483F, 0);
        final BulbBuilder leg3 = legs.createChild(21, 45);
        leg3.setPosition(0, 0.3F, 1.1F);
        leg3.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg3.setAngles(-0.06981317007977318F, -1.8212510744560826F, 1.5707963267948966F);
        final BulbBuilder leg3Lower = leg3.createChild(21, 45);
        leg3Lower.setPosition(3.81F, 0, -0.05F);
        leg3Lower.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg3Lower.setAngles(0, -0.4363323129985824F, 0);
        final BulbBuilder leg4 = legs.createChild(21, 45);
        leg4.setPosition(0, -0.5F, 1.1F);
        leg4.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg4.setAngles(0.06981317007977318F, -2.1855012893472994F, 1.5707963267948966F);
        final BulbBuilder leg4Lower = leg4.createChild(21, 45);
        leg4Lower.setPosition(3.76F, 0, -0.06F);
        leg4Lower.addCuboid(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg4Lower.setAngles(0, -0.5235987755982988F, 0);
    }
}
