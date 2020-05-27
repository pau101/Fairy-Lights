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
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class LightRenderer {
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
            (models, i) -> models[i < 0 ? 4 : Mth.mod(Mth.hash(i), 4) + 1]
        ))
        .put(LightVariant.METEOR, LightModelProvider.of(new MeteorLightModel()))
        .build()
    );

    public Data start(final IRenderTypeBuffer source) {
        final IVertexBuilder solid = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
        final IVertexBuilder translucent = ClientProxy.TRANSLUCENT_TEXTURE.getBuffer(source, RenderType::getEntityTranslucent);
        return new Data(solid, translucent);
    }

    public LightModel getModel(final Light light, final int index) {
        return this.lights.getOrDefault(light.getVariant(), this.defaultLight).get(index);
    }

    public void render(final MatrixStack matrix, final Data data, final Light light, final int index, final float delta, final int packedLight, final int packedOverlay) {
        this.render(matrix, data, light, this.getModel(light, index), delta, packedLight, packedOverlay);
    }

    public void render(final MatrixStack matrix, final Data data, final Light light, final LightModel model, final float delta, final int packedLight, final int packedOverlay) {
        model.animate(light, delta);
        model.render(matrix, data.solid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        model.renderTranslucent(matrix, data.translucent, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
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

    static class Data {
        final IVertexBuilder solid;
        final IVertexBuilder translucent;

        Data(final IVertexBuilder solid, final IVertexBuilder translucent) {
            this.solid = solid;
            this.translucent = translucent;
        }
    }
}
