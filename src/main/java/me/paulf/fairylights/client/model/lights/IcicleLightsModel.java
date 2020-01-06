package me.paulf.fairylights.client.model.lights;

import me.paulf.fairylights.client.model.AdvancedRendererModel;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class IcicleLightsModel extends LightModel {
    private final AdvancedRendererModel wireMiddle;
    private final AdvancedRendererModel wireBottom;
    private final AdvancedRendererModel wireEnd;

    private final AdvancedRendererModel light2;
    private final AdvancedRendererModel light3;
    private final AdvancedRendererModel light4;

    public IcicleLightsModel() {
        this.wireMiddle = new AdvancedRendererModel(this, 29, 76);
        this.wireMiddle.setRotationPoint(0, 4, 0);
        this.wireMiddle.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
        this.wireMiddle.rotateAngleX = -0.2617993877991494F;
        this.wireMiddle.rotateAngleY = 0.5235987755982988F;
        final AdvancedRendererModel lightBase3 = new AdvancedRendererModel(this, 33, 76);
        lightBase3.setRotationPoint(0, 2, 0.5F);
        lightBase3.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
        final AdvancedRendererModel lightBase2 = new AdvancedRendererModel(this, 33, 76);
        lightBase2.setRotationPoint(0, 2, -1.5F);
        lightBase2.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
        final AdvancedRendererModel light1 = new AdvancedRendererModel(this, 29, 72);
        light1.setRotationPoint(0, -2.405233653435833F, -1.170506183587062F);
        light1.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
        light1.rotateAngleX = -3.0543261909900767F;
        this.wireBottom = new AdvancedRendererModel(this, 29, 76);
        this.wireBottom.setRotationPoint(0, 4, 0);
        this.wireBottom.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
        this.wireBottom.rotateAngleX = 0.4363323129985824F;
        this.wireBottom.rotateAngleY = 0.5235987755982988F;
        this.light2 = new AdvancedRendererModel(this, 29, 72);
        this.light2.setRotationPoint(-1.7077077845361228F, -5.893569134597652F, 2.4972589475492635F);
        this.light2.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
        this.light2.rotateAngleX = 2.9804748914277273F;
        this.light2.rotateAngleY = -0.5214031733599432F;
        this.light2.rotateAngleZ = -0.050276985685263745F;
        final AdvancedRendererModel lightBase1 = new AdvancedRendererModel(this, 33, 76);
        lightBase1.setRotationPoint(0, 2, 0.5F);
        lightBase1.addBox(-0.5F, 0, 0, 1, 1, 1, 0);
        final AdvancedRendererModel wireTop = new AdvancedRendererModel(this, 29, 76);
        wireTop.setRotationPoint(0, -0.5F, 0);
        wireTop.addBox(-0.5F, 0, -0.5F, 1, 4, 1, 0);
        wireTop.rotateAngleX = -3.0543261909900767F;
        this.light3 = new AdvancedRendererModel(this, 29, 72);
        this.light3.setRotationPoint(0.7935216418735993F, -10.095277243516536F, -0.4609129179470893F);
        this.light3.addBox(-1, -0.5F, 0, 2, 2, 2, 0);
        this.light3.rotateAngleX = -2.9807093751793796F;
        this.light3.rotateAngleY = -1.0339196228641108F;
        this.light3.rotateAngleZ = 0.10720187699072795F;
        final AdvancedRendererModel connector = new AdvancedRendererModel(this, 77, 0);
        connector.setRotationPoint(0, 0, 0);
        connector.addBox(-1, -0.5F, -1, 2, 2, 2, 0);
        this.light4 = new AdvancedRendererModel(this, 29, 72);
        this.light4.setRotationPoint(-2.4652688758772636F, -12.95165172579971F, -1.9323986317282649F);
        this.light4.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.light4.rotateAngleX = -0.7522647962292457F;
        this.light4.rotateAngleY = -1.3039482923151255F;
        this.light4.rotateAngleZ = -2.5903859234812483F;
        this.wireEnd = new AdvancedRendererModel(this, 29, 76);
        this.wireEnd.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.wireEnd.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.wireEnd.rotateAngleX = -0.4363323129985824F;
        this.wireEnd.rotateAngleY = 0.7853981633974483F;
        final AdvancedRendererModel lightBase4 = new AdvancedRendererModel(this, 33, 76);
        lightBase4.setRotationPoint(0.0F, 2.0F, -1.5F);
        lightBase4.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        wireTop.addChild(lightBase1);
        wireTop.addChild(this.wireMiddle);
        this.wireMiddle.addChild(lightBase2);
        this.wireMiddle.addChild(this.wireBottom);
        this.wireBottom.addChild(lightBase3);
        this.wireBottom.addChild(this.wireEnd);
        this.wireEnd.addChild(lightBase4);
        connector.addChild(wireTop);
        this.amutachromicParts.addChild(connector);
        this.colorableParts.addChild(light1);
        this.colorableParts.addChild(this.light2);
        this.colorableParts.addChild(this.light3);
        this.colorableParts.addChild(this.light4);
    }

    @Override
    public boolean hasRandomRotation() {
        return true;
    }

    @Override
    public void prepare(final int index) {
        super.prepare(index);
        int which = Mth.mod(Mth.hash(index), 4);
        if (which == 0) {
            which = index % 3;
        }
        this.set(which);
    }

    private void set(final int which) {
        this.wireMiddle.isHidden = which == 0;
        this.wireBottom.isHidden = which == 1;
        this.wireEnd.isHidden = which == 2;
        this.light2.isHidden = which < 1;
        this.light3.isHidden = which < 2;
        this.light4.isHidden = which < 3;
    }

    @Override
    public void render(final World world, final Light light, final float scale, final Vec3d color, final int moonlight, final int sunlight, final float brightness, final int index, final float partialRenderTicks) {
        super.render(world, light, scale, color, moonlight, sunlight, brightness, index, partialRenderTicks);
        this.set(3);
    }
}
