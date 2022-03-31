package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final LightRenderer lights = new LightRenderer();

    public HangingLightsRenderer() {
        super(0, 0, 2.0F);
    }

    @Override
    protected int getWireColor(final HangingLightsConnection conn) {
        return conn.getString().getColor();
    }

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light<?>[] lights = conn.getFeatures();
        if (lights == null) {
            return;
        }
        final LightRenderer.Data data = this.lights.start(source);
        for (int i = 0; i < lights.length; i++) {
            final Light<?> light = lights[i];
            final Vector3d pos = light.getPoint(delta);
            matrix.func_227860_a_();
            matrix.func_227861_a_(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
            matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(-light.getYaw(delta)));
            if (light.parallelsCord()) {
                matrix.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(light.getPitch(delta)));
            }
            matrix.func_227863_a_(Vector3f.field_229179_b_.func_229193_c_(light.getRoll(delta)));
            if (light.getVariant() != SimpleLightVariant.FAIRY_LIGHT) { // FIXME
                matrix.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(Mth.mod(Mth.hash(i) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4.0F));
            }
            matrix.func_227861_a_(0.0D, -light.getDescent(), 0.0D);
            this.lights.render(matrix, data, light, i, delta, packedLight, packedOverlay);
            matrix.func_227865_b_();
        }
    }
}
