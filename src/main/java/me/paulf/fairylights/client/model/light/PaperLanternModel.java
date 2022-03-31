package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PaperLanternModel extends ColorLightModel {
    public PaperLanternModel() {
        this.unlit.func_78784_a(34, 18);
        this.unlit.func_228301_a_(-1, -0.5F, -1, 2, 2, 2, -0.05F);
        this.unlit.func_78784_a(21, 26);
        this.unlit.func_228300_a_(-1, -9.5F, -1, 2, 3, 2);
        this.unlit.func_78784_a(58, 0);
        this.unlit.func_228300_a_(-0.5F, -14.5F, -0.5F, 1, 5, 1);
        for (int i = 0; i < 8; i++) {
            final boolean straight = (i & 1) == 0;
            final ModelRenderer hSupport = new ModelRenderer(this, 28, 34);
            hSupport.func_228300_a_(0, 0, -0.5F, straight ? 4 : 5, 7, 1);
            hSupport.field_78796_g = 45 * i * Mth.DEG_TO_RAD;
            hSupport.field_78797_d = -7;
            this.unlit.func_78792_a(hSupport);
        }
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(0, 41);
        bulb.addBox(-3.5F, -6.5F, -3.5F, 7, 6, 7, 0.0F, 0.3F);
    }
}
