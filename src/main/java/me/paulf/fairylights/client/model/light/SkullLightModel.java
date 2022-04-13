package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.FLMath;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SkullLightModel extends ColorLightModel {
    public SkullLightModel() {
        final ModelRenderer apertures = new ModelRenderer(this, 12, 10);
        apertures.func_78793_a(0, -3, -2.75F);
        apertures.func_228301_a_(-1.5F, -1, 0, 3, 2, 0, 0);
        apertures.field_78795_f = FLMath.PI;
        apertures.field_78796_g = FLMath.PI;
        this.unlit.func_78792_a(apertures);
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder skull = bulb.createChild(0, 54);
        skull.addBox(-2.5F, 0, -2.5F, 5, 4, 5, 0);
        skull.setAngles(FLMath.PI, 0, 0);
        final BulbBuilder mandible = bulb.createChild(40, 34);
        mandible.setPosition(0, -3.5F, 0.3F);
        mandible.addBox(-2.5F, 0, -3, 5, 2, 3, -0.25F, 0.6F);
        mandible.setAngles(FLMath.PI / 16 + FLMath.PI, FLMath.PI, 0);
        final BulbBuilder maxilla = bulb.createChild(46, 7);
        maxilla.setPosition(0, -3.875F, -2.125F);
        maxilla.setAngles(FLMath.PI, 0, 0);
        maxilla.addBox(-1, 0, -0.5F, 2, 1, 1, -0.125F);
        final ModelRenderer chain = new ModelRenderer(this, 34, 18);
        chain.func_78793_a(0, 2, 0);
        chain.func_228301_a_(-1, 0, -1, 2, 2, 2, -0.05F);
        chain.field_78795_f = FLMath.PI;
        this.lit.func_78792_a(chain);
    }
}
