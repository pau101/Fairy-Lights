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
            matrix.push();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.rotate(Vector3f.YP.rotation(-light.getYaw(delta)));
            if (light.parallelsCord()) {
                matrix.rotate(Vector3f.ZP.rotation(light.getPitch(delta)));
            }
            matrix.rotate(Vector3f.XP.rotation(light.getRoll(delta)));
            if (light.getVariant() != SimpleLightVariant.FAIRY_LIGHT) { // FIXME
                matrix.rotate(Vector3f.YP.rotation(Mth.mod(Mth.hash(i) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4.0F));
            }
            matrix.translate(0.0D, -light.getDescent(), 0.0D);
            this.lights.render(matrix, data, light, i, delta, packedLight, packedOverlay);
            matrix.pop();
        }
    }
}
