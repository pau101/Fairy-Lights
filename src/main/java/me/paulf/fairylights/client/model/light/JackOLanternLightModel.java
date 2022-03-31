package me.paulf.fairylights.client.model.light;

import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class JackOLanternLightModel extends ColorLightModel {
    public JackOLanternLightModel() {
        final BulbBuilder bulb = this.createBulb();
        final BulbBuilder pumpkin = bulb.createChild(28, 42);
        pumpkin.addBox(-3, 0, -3, 6, 6, 6, 0);
        pumpkin.setAngles(Mth.PI, 0.0F, 0.0F);
        final ModelRenderer leaf1 = new ModelRenderer(this, 12, 18);
        leaf1.func_78793_a(0.5F, 0, 0.5F);
        leaf1.func_228301_a_(0, -0.5F, 0, 2, 1, 2, 0);
        final Vector3f vec = new Vector3f(-1.0F, 0.0F, 1.0F);
        vec.func_229194_d_();
        final Quaternion droop = vec.func_229193_c_(Mth.PI / 12.0F);
        float[] leafAngles = toEuler(droop);
        leaf1.field_78795_f = leafAngles[0];
        leaf1.field_78796_g = leafAngles[1];
        leaf1.field_78808_h = leafAngles[2];
        this.unlit.func_78792_a(leaf1);
        final ModelRenderer leaf2 = new ModelRenderer(this, 12, 18);
        leaf2.func_78793_a(-0.5F, 0, -0.5F);
        leaf2.func_228301_a_(0, -0.5F, 0, 2, 1, 2, 0);
        final Quaternion q = Vector3f.field_229181_d_.func_229193_c_(Mth.PI);
        q.func_195890_a(droop);
        leafAngles = toEuler(q);
        leaf2.field_78795_f = leafAngles[0];
        leaf2.field_78796_g = leafAngles[1];
        leaf2.field_78808_h = leafAngles[2];
        this.unlit.func_78792_a(leaf2);
        final ModelRenderer stem = new ModelRenderer(this, 21, 41);
        stem.func_78793_a(0, 2, 0);
        stem.func_228301_a_(-1, 0, -1, 2, 2, 2, -0.05F);
        stem.field_78795_f = Mth.PI;
        this.unlit.func_78792_a(stem);
        final ModelRenderer face = new ModelRenderer(this, 56, 34);
        face.func_78793_a(0, -3, -3.25F);
        face.func_228301_a_(-3, -3, 0, 6, 6, 0, 0);
        face.field_78795_f = Mth.PI;
        face.field_78796_g = Mth.PI;
        this.lit.func_78792_a(face);
    }
}
