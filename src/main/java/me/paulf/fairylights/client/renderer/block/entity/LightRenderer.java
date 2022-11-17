package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.FLModelLayers;
import me.paulf.fairylights.client.model.light.CandleLanternModel;
import me.paulf.fairylights.client.model.light.ColorCandleLanternModel;
import me.paulf.fairylights.client.model.light.ColorOilLanternModel;
import me.paulf.fairylights.client.model.light.FairyLightModel;
import me.paulf.fairylights.client.model.light.FlowerLightModel;
import me.paulf.fairylights.client.model.light.GhostLightModel;
import me.paulf.fairylights.client.model.light.HeartLightModel;
import me.paulf.fairylights.client.model.light.IcicleLightsModel;
import me.paulf.fairylights.client.model.light.IncandescentLightModel;
import me.paulf.fairylights.client.model.light.JackOLanternLightModel;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.client.model.light.MeteorLightModel;
import me.paulf.fairylights.client.model.light.MoonLightModel;
import me.paulf.fairylights.client.model.light.OilLanternModel;
import me.paulf.fairylights.client.model.light.OrbLanternModel;
import me.paulf.fairylights.client.model.light.PaperLanternModel;
import me.paulf.fairylights.client.model.light.SkullLightModel;
import me.paulf.fairylights.client.model.light.SnowflakeLightModel;
import me.paulf.fairylights.client.model.light.SpiderLightModel;
import me.paulf.fairylights.client.model.light.StarLightModel;
import me.paulf.fairylights.client.model.light.WitchLightModel;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.ForgeRenderTypes;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LightRenderer {
    static class DefaultModel extends LightModel<LightBehavior> {
        private static final ModelPart EMPTY = new ModelPart(List.of(), Map.of());

        public DefaultModel() {
            super(new ModelPart(List.of(), Map.of(
                "lit", EMPTY,
                "lit_tint", EMPTY,
                "lit_tint_glow", EMPTY,
                "unlit", EMPTY
            )));
        }

        @Override
        public void renderToBuffer(final PoseStack matrix, final VertexConsumer builder, final int light, final int overlay, final float r, final float g, final float b, final float a) {
        }
    }

    private final LightModelProvider<LightBehavior> defaultLight = LightModelProvider.of(new DefaultModel());

    private final Map<LightVariant<?>, LightModelProvider<?>> lights;

    public LightRenderer(final Function<ModelLayerLocation, ModelPart> baker) {
        lights = new ImmutableMap.Builder<LightVariant<?>, LightModelProvider<?>>()
            .put(SimpleLightVariant.FAIRY_LIGHT, LightModelProvider.of(new FairyLightModel(baker.apply(FLModelLayers.FAIRY_LIGHT))))
            .put(SimpleLightVariant.PAPER_LANTERN, LightModelProvider.of(new PaperLanternModel(baker.apply(FLModelLayers.PAPER_LANTERN))))
            .put(SimpleLightVariant.ORB_LANTERN, LightModelProvider.of(new OrbLanternModel(baker.apply(FLModelLayers.ORB_LANTERN))))
            .put(SimpleLightVariant.FLOWER_LIGHT, LightModelProvider.of(new FlowerLightModel(baker.apply(FLModelLayers.FLOWER_LIGHT))))
            .put(SimpleLightVariant.CANDLE_LANTERN_LIGHT, LightModelProvider.of(new ColorCandleLanternModel(baker.apply(FLModelLayers.CANDLE_LANTERN_LIGHT))))
            .put(SimpleLightVariant.OIL_LANTERN_LIGHT, LightModelProvider.of(new ColorOilLanternModel(baker.apply(FLModelLayers.OIL_LANTERN_LIGHT))))
            .put(SimpleLightVariant.JACK_O_LANTERN, LightModelProvider.of(new JackOLanternLightModel(baker.apply(FLModelLayers.JACK_O_LANTERN))))
            .put(SimpleLightVariant.SKULL_LIGHT, LightModelProvider.of(new SkullLightModel(baker.apply(FLModelLayers.SKULL_LIGHT))))
            .put(SimpleLightVariant.GHOST_LIGHT, LightModelProvider.of(new GhostLightModel(baker.apply(FLModelLayers.GHOST_LIGHT))))
            .put(SimpleLightVariant.SPIDER_LIGHT, LightModelProvider.of(new SpiderLightModel(baker.apply(FLModelLayers.SPIDER_LIGHT))))
            .put(SimpleLightVariant.WITCH_LIGHT, LightModelProvider.of(new WitchLightModel(baker.apply(FLModelLayers.WITCH_LIGHT))))
            .put(SimpleLightVariant.SNOWFLAKE_LIGHT, LightModelProvider.of(new SnowflakeLightModel(baker.apply(FLModelLayers.SNOWFLAKE_LIGHT))))
            .put(SimpleLightVariant.HEART_LIGHT, LightModelProvider.of(new HeartLightModel(baker.apply(FLModelLayers.HEART_LIGHT))))
            .put(SimpleLightVariant.MOON_LIGHT, LightModelProvider.of(new MoonLightModel(baker.apply(FLModelLayers.MOON_LIGHT))))
            .put(SimpleLightVariant.STAR_LIGHT, LightModelProvider.of(new StarLightModel(baker.apply(FLModelLayers.STAR_LIGHT))))
            .put(SimpleLightVariant.ICICLE_LIGHTS, LightModelProvider.of(
                new IcicleLightsModel[] {
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_1), 1),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_2), 2),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_3), 3),
                    new IcicleLightsModel(baker.apply(FLModelLayers.ICICLE_LIGHTS_4), 4)
                },
                (models, i) -> models[i < 0 ? 3 : Mth.mod(Mth.hash(i), 4)]
            ))
            .put(SimpleLightVariant.METEOR_LIGHT, LightModelProvider.of(new MeteorLightModel(baker.apply(FLModelLayers.METEOR_LIGHT))))
            .put(SimpleLightVariant.OIL_LANTERN, LightModelProvider.of(new OilLanternModel(baker.apply(FLModelLayers.OIL_LANTERN))))
            .put(SimpleLightVariant.CANDLE_LANTERN, LightModelProvider.of(new CandleLanternModel(baker.apply(FLModelLayers.CANDLE_LANTERN))))
            .put(SimpleLightVariant.INCANDESCENT_LIGHT, LightModelProvider.of(new IncandescentLightModel(baker.apply(FLModelLayers.INCANDESCENT_LIGHT))))
            .build();
    }

    public Data start(final MultiBufferSource source) {
        final VertexConsumer buf = ClientProxy.TRANSLUCENT_TEXTURE.buffer(source, ForgeRenderTypes::getUnsortedTranslucent);
        ForwardingVertexConsumer translucent = new ForwardingVertexConsumer() {
            @Override
            protected VertexConsumer delegate() {
                return buf;
            }

            @Override
            public VertexConsumer normal(float x, float y, float z) {
                return super.normal(0.0F, 1.0F, 0.0F);
            }
        };
        return new Data(buf, translucent);
    }

    public <T extends LightBehavior> LightModel<T> getModel(final Light<?> light, final int index) {
        return this.getModel(light.getVariant(), index);
    }

    @SuppressWarnings("unchecked")
    public <T extends LightBehavior> LightModel<T> getModel(final LightVariant<?> variant, final int index) {
        return (LightModel<T>) this.lights.getOrDefault(variant, this.defaultLight).get(index);
    }

    public void render(final PoseStack matrix, final Data data, final Light<?> light, final int index, final float delta, final int packedLight, final int packedOverlay) {
        this.render(matrix, data, light, this.getModel(light, index), delta, packedLight, packedOverlay);
    }

    public <T extends LightBehavior> void render(final PoseStack matrix, final Data data, final Light<T> light, final LightModel<T> model, final float delta, final int packedLight, final int packedOverlay) {
        model.animate(light, light.getBehavior(), delta);
        model.renderToBuffer(matrix, data.solid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        model.renderTranslucent(matrix, data.translucent, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    interface LightModelProvider<T extends LightBehavior> {
        LightModel<T> get(final int index);

        static <T extends LightBehavior> LightModelProvider<T> of(final LightModel<T> model) {
            return i -> model;
        }

        static <T extends LightBehavior> LightModelProvider<T> of(final Supplier<LightModel<T>> model) {
            return i -> model.get();
        }

        static <T extends LightBehavior, D> LightModelProvider<T> of(final D data, final BiFunction<? super D, Integer, LightModel<T>> function) {
            return i -> function.apply(data, i);
        }
    }

    static class Data {
        final VertexConsumer solid;
        final VertexConsumer translucent;

        Data(final VertexConsumer solid, final VertexConsumer translucent) {
            this.solid = solid;
            this.translucent = translucent;
        }
    }

}
