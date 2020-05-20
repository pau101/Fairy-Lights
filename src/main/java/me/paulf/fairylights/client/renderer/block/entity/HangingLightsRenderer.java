package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.Vec3d;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final LightRenderer lights = new LightRenderer();

    public HangingLightsRenderer() {
        super(0, 0, 2.0F);
    }

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light[] currLights = conn.getFeatures();
        final Light[] prevLights = conn.getPrevFeatures();
        if (currLights == null || prevLights == null) {
            return;
        }
        final LightRenderer.Data data = this.lights.start(source);
        final int count = Math.min(currLights.length, prevLights.length);
        for (int i = 0; i < count; i++) {
            final Light prevLight = prevLights[i];
            final Light currLight = currLights[i];
            final LightVariant variant = currLight.getVariant();
            final Vec3d pos = Mth.lerp(prevLight.getPoint(), currLight.getPoint(), delta);
            matrix.push();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.rotate(Vector3f.YP.rotation(-currLight.getYaw(delta)));
            if (currLight.getVariant().parallelsCord()) {
                matrix.rotate(Vector3f.ZP.rotation(currLight.getPitch(delta)));
            }
            if (variant != LightVariant.FAIRY) {
                matrix.rotate(Vector3f.YP.rotation(Mth.mod(Mth.hash(i) * Mth.DEG_TO_RAD, Mth.TAU) + Mth.PI / 4.0F));
            }
            matrix.rotate(Vector3f.XP.rotation(currLight.getRoll(delta)));
            matrix.translate(0.0D, -0.125D, 0.0D);
            this.lights.render(matrix, data, currLight, i, delta, packedLight, packedOverlay);
            matrix.pop();
        }
    }
}
