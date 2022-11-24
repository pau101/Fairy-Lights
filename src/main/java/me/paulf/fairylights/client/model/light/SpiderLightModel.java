package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class SpiderLightModel extends ColorLightModel {
    public SpiderLightModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        final LightMeshHelper helper = LightMeshHelper.create();
        final EasyMeshBuilder string = new EasyMeshBuilder("string", 30, 6);
        string.addBox(-1, 0, -1, 2, 2, 2, -0.05F);
        helper.unlit().addChild(string);
        final BulbBuilder bulb = helper.createBulb();
        bulb.setAngles(0.0F, -FLMth.PI / 2.0F, 0.0F);
        final BulbBuilder abdomen = bulb.createChild("abdomen", 20, 54);
        abdomen.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5, 0);
        final BulbBuilder pedicel = abdomen.createChild("pedicel", 6, 0);
        pedicel.setPosition(0, -6.0F, 0);
        pedicel.addBox(-1, 0, -1, 2, 1, 2, 0);
        final BulbBuilder cephalothorax = pedicel.createChild("cephalothorax", 40, 57);
        cephalothorax.setPosition(0, -2.25F, 0);
        cephalothorax.addBox(-2, 0, -2, 4, 3, 4, 0);
        final BulbBuilder cheliceraLeft = cephalothorax.createChild("cheliceraLeft", 0, 0);
        cheliceraLeft.setPosition(0, 0.3F, 0.6F);
        cheliceraLeft.addBox(-1, -1.5F, 0, 2, 2, 1, 0);
        cheliceraLeft.setAngles(-0.2617993877991494F, 0, 0);
        final BulbBuilder cheliceraRight = cephalothorax.createChild("cheliceraRight", 0, 0);
        cheliceraRight.setPosition(0, 0.3F, -0.6F);
        cheliceraRight.addBox(-1, -1.5F, -1, 2, 2, 1, 0);
        cheliceraRight.setAngles(0.2617993877991494F, 0, 0);
        createLegs(helper, pedicel, 0);
        createLegs(helper, pedicel, 1);
        return helper.build();
    }

    private static void createLegs(final LightMeshHelper helper, final BulbBuilder bulb, final int side) {
        final BulbBuilder legs = bulb.createChild("legs_" + side, 0, 0);
        legs.setAngles(0.0F, FLMth.PI * side, 0.0F);
        final BulbBuilder leg1 = legs.createChild("leg1_" + side, 21, 45);
        leg1.setPosition(0, 0.6F, 1.1F);
        leg1.addBox(0, -0.5F, -0.5F, 5, 1, 1, 0);
        leg1.setAngles(-0.13962634015954636F, -0.7853981633974483F, 1.5707963267948966F);
        final BulbBuilder leg1Lower = leg1.createChild("leg1Lower_" + side, 21, 45);
        leg1Lower.setPosition(4.59F, 0, -0.24F);
        leg1Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg1Lower.setAngles(0, -1.0471975511965976F, 0);
        final BulbBuilder leg2 = legs.createChild("leg2_" + side, 21, 45);
        leg2.setPosition(0, 0.6F, 1.1F);
        leg2.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg2.setAngles(0.13962634015954636F, -1.2217304763960306F, 1.5707963267948966F);
        final BulbBuilder leg2Lower = leg2.createChild("leg2Lower_" + side, 21, 45);
        leg2Lower.setPosition(3.65F, 0, -0.13F);
        leg2Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg2Lower.setAngles(0, -0.7853981633974483F, 0);
        final BulbBuilder leg3 = legs.createChild("leg3_" + side, 21, 45);
        leg3.setPosition(0, 0.3F, 1.1F);
        leg3.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg3.setAngles(-0.06981317007977318F, -1.8212510744560826F, 1.5707963267948966F);
        final BulbBuilder leg3Lower = leg3.createChild("leg3Lower_" + side, 21, 45);
        leg3Lower.setPosition(3.81F, 0, -0.05F);
        leg3Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg3Lower.setAngles(0, -0.4363323129985824F, 0);
        final BulbBuilder leg4 = legs.createChild("leg4_" + side, 21, 45);
        leg4.setPosition(0, -0.5F, 1.1F);
        leg4.addBox(0, -0.5F, -0.5F, 4, 1, 1, 0);
        leg4.setAngles(0.06981317007977318F, -2.1855012893472994F, 1.5707963267948966F);
        final BulbBuilder leg4Lower = leg4.createChild("leg4Lower_" + side, 21, 45);
        leg4Lower.setPosition(3.76F, 0, -0.06F);
        leg4Lower.addBox(0, -0.5F, -0.5F, 4, 1, 1, -0.075F);
        leg4Lower.setAngles(0, -0.5235987755982988F, 0);
    }
}
