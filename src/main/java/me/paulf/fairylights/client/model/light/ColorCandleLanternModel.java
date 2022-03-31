package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ColorCandleLanternModel extends ColorLightModel {
    public ColorCandleLanternModel() {
        this.unlit.func_78784_a(21, 0);
        this.unlit.func_228301_a_(-1, 0.5F, -1, 2, 1, 2, -0.05F);
        this.unlit.func_78784_a(0, 3);
        this.unlit.func_228300_a_(-1.5F, -1.5F, -1.5F, 3, 2, 3);
        this.unlit.func_78784_a(43, 15);
        this.unlit.func_228300_a_(-2.5F, -1.75F, -2.5F, 5, 1, 5);
        this.unlit.func_78784_a(23, 27);
        this.unlit.func_228300_a_(-3, -2.5F, -3, 6, 1, 6);
        this.unlit.func_78784_a(43, 21);
        this.unlit.func_228300_a_(-2.5F, -8.5F, -2.5F, 5, 1, 5);
        for (int i = 0; i < 4; i++) {
            final ModelRenderer frame = new ModelRenderer(this, 4 * i + 47, 27);
            frame.func_228300_a_(-0.5F, 0, -0.5F, 1, 6, 1);
            frame.func_78793_a(2.1F * ((i & 2) == 0 ? 1 : -1), -8F, 2.1F * ((i + 1 & 2) == 0 ? 1 : -1));
            frame.field_78795_f = 5 * Mth.DEG_TO_RAD;
            frame.field_78796_g = (90 * i + 45) * Mth.DEG_TO_RAD;
            this.unlit.func_78792_a(frame);
        }
        final BulbBuilder bulb = this.createBulb();
        bulb.setUV(48, 6);
        bulb.addBox(-2, -7.5F, -2, 4, 5, 4, 0.0F, 0.2F);
    }
}
