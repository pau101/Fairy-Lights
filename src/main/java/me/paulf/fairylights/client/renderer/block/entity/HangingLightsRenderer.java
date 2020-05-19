package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.model.light.FairyLightModel;
import me.paulf.fairylights.client.model.light.FlowerLightModel;
import me.paulf.fairylights.client.model.light.GhostLightModel;
import me.paulf.fairylights.client.model.light.IcicleLightsModel;
import me.paulf.fairylights.client.model.light.JackOLanternLightModel;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.client.model.light.MeteorLightModel;
import me.paulf.fairylights.client.model.light.OilLanternModel;
import me.paulf.fairylights.client.model.light.OrbLanternModel;
import me.paulf.fairylights.client.model.light.OrnateLanternModel;
import me.paulf.fairylights.client.model.light.PaperLanternModel;
import me.paulf.fairylights.client.model.light.SkullLightModel;
import me.paulf.fairylights.client.model.light.SnowflakeLightModel;
import me.paulf.fairylights.client.model.light.SpiderLightModel;
import me.paulf.fairylights.client.model.light.WitchLightModel;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.Vec3d;

import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class HangingLightsRenderer extends ConnectionRenderer<HangingLightsConnection> {
    private final LightModelProvider defaultLight = LightModelProvider.of(new FairyLightModel());

    private final EnumMap<LightVariant, LightModelProvider> lights = Maps.newEnumMap(new ImmutableMap.Builder<LightVariant, LightModelProvider>()
        .put(LightVariant.FAIRY, this.defaultLight)
        .put(LightVariant.PAPER, LightModelProvider.of(new PaperLanternModel()))
        .put(LightVariant.ORB, LightModelProvider.of(new OrbLanternModel()))
        .put(LightVariant.FLOWER, LightModelProvider.of(new FlowerLightModel()))
        .put(LightVariant.ORNATE, LightModelProvider.of(new OrnateLanternModel()))
        .put(LightVariant.OIL, LightModelProvider.of(new OilLanternModel()))
        .put(LightVariant.JACK_O_LANTERN, LightModelProvider.of(new JackOLanternLightModel()))
        .put(LightVariant.SKULL, LightModelProvider.of(new SkullLightModel()))
        .put(LightVariant.GHOST, LightModelProvider.of(new GhostLightModel()))
        .put(LightVariant.SPIDER, LightModelProvider.of(new SpiderLightModel()))
        .put(LightVariant.WITCH, LightModelProvider.of(new WitchLightModel()))
        .put(LightVariant.SNOWFLAKE, LightModelProvider.of(new SnowflakeLightModel()))
        .put(LightVariant.ICICLE, LightModelProvider.of(
            IntStream.rangeClosed(1, 4).mapToObj(IcicleLightsModel::new).toArray(LightModel[]::new),
            (models, i) -> models[Mth.mod(Mth.hash(i), 4) + 1]
        ))
        .put(LightVariant.METEOR, LightModelProvider.of(new MeteorLightModel()))
        .build()
    );

    public HangingLightsRenderer() {
        super(0, 0, 2.0F);
    }

    @Override
    public void render(final HangingLightsConnection conn, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
        super.render(conn, delta, matrix, source, packedLight, packedOverlay);
        final Light[] currLights = conn.getFeatures();
        final Light[] prevLights = conn.getPrevFeatures();
        if (currLights != null && prevLights != null) {
            final IVertexBuilder bufSolid = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
            final IVertexBuilder bufTranslucent = ClientProxy.TRANSLUCENT_TEXTURE.getBuffer(source, RenderType::getEntityTranslucent);
            final int count = Math.min(currLights.length, prevLights.length);
            for (int i = 0; i < count; i++) {
                final Light prevLight = prevLights[i];
                final Light currLight = currLights[i];
                final LightVariant variant = currLight.getVariant();
                final LightModel light = this.lights.getOrDefault(variant, this.defaultLight).get(i);
                final Vec3d pos = Mth.lerp(prevLight.getPoint(), currLight.getPoint(), delta);
                light.animate(currLight, delta);
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
                light.render(matrix, bufSolid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                light.renderTranslucent(matrix, bufTranslucent, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrix.pop();
            }
        }
    }

    @Override
    protected void render(final HangingLightsConnection conn, final Catenary catenary, final float delta, final MatrixStack matrix, final IRenderTypeBuffer source, final int packedLight, final int packedOverlay) {
    }

    interface LightModelProvider {
        LightModel get(final int index);

        static LightModelProvider of(final LightModel model) {
            return i -> model;
        }

        static <T> LightModelProvider of(final T data, final BiFunction<? super T, Integer, LightModel> function) {
            return i -> function.apply(data, i);
        }
    }
}
