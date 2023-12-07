package me.paulf.fairylights.client.renderer.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.util.FLMth;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final LightRenderer lights;

    public HangingLightsRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        super(baker, FLModelLayers.LIGHTS_WIRE);
        this.lights = new LightRenderer(baker);
    }

    @Override
    protected int getWireColor(final HangingLightsConnection conn) {
        return conn.getString().getColor();
    }

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final PoseStack matrix, final MultiBufferSource source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light<?>[] lights = conn.getFeatures();
        if (lights == null) {
            return;
        }
        final LightRenderer.Data data = this.lights.start(source);
        for (int i = 0; i < lights.length; i++) {
            final Light<?> light = lights[i];
            final Vec3 pos = light.getPoint(delta);
            matrix.pushPose();
            matrix.translate(pos.x, pos.y, pos.z);
            matrix.mulPose(Axis.YP.rotation(-light.getYaw(delta)));
            if (light.parallelsCord()) {
                matrix.mulPose(Axis.ZP.rotation(light.getPitch(delta)));
            }
            matrix.mulPose(Axis.XP.rotation(light.getRoll(delta)));
            if (light.getVariant() != SimpleLightVariant.FAIRY_LIGHT) { // FIXME
                matrix.mulPose(Axis.YP.rotation(FLMth.mod(FLMth.hash(i) * FLMth.DEG_TO_RAD, FLMth.TAU) + FLMth.PI / 4.0F));
            }
            matrix.translate(0.0D, -light.getDescent(), 0.0D);
            this.lights.render(matrix, data, light, i, delta, packedLight, packedOverlay);
            matrix.popPose();
        }
    }

    public static LayerDefinition wireLayer() {
        return WireModel.createLayer(0, 0, 2);
    }
}
