package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.TranslucentLightRenderer;
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
import me.paulf.fairylights.util.FLMath;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class LightRenderer {
    static class DefaultModel extends LightModel<LightBehavior> {
    }

    private final LightModelProvider<LightBehavior> defaultLight = LightModelProvider.of(new DefaultModel());

    private final Map<LightVariant<?>, LightModelProvider<?>> lights = new ImmutableMap.Builder<LightVariant<?>, LightModelProvider<?>>()
        .put(SimpleLightVariant.FAIRY_LIGHT, LightModelProvider.of(new FairyLightModel()))
        .put(SimpleLightVariant.PAPER_LANTERN, LightModelProvider.of(new PaperLanternModel()))
        .put(SimpleLightVariant.ORB_LANTERN, LightModelProvider.of(new OrbLanternModel()))
        .put(SimpleLightVariant.FLOWER_LIGHT, LightModelProvider.of(new FlowerLightModel()))
        .put(SimpleLightVariant.CANDLE_LANTERN_LIGHT, LightModelProvider.of(new ColorCandleLanternModel()))
        .put(SimpleLightVariant.OIL_LANTERN_LIGHT, LightModelProvider.of(new ColorOilLanternModel()))
        .put(SimpleLightVariant.JACK_O_LANTERN, LightModelProvider.of(new JackOLanternLightModel()))
        .put(SimpleLightVariant.SKULL_LIGHT, LightModelProvider.of(new SkullLightModel()))
        .put(SimpleLightVariant.GHOST_LIGHT, LightModelProvider.of(new GhostLightModel()))
        .put(SimpleLightVariant.SPIDER_LIGHT, LightModelProvider.of(new SpiderLightModel()))
        .put(SimpleLightVariant.WITCH_LIGHT, LightModelProvider.of(new WitchLightModel()))
        .put(SimpleLightVariant.SNOWFLAKE_LIGHT, LightModelProvider.of(new SnowflakeLightModel()))
        .put(SimpleLightVariant.HEART_LIGHT, LightModelProvider.of(new HeartLightModel()))
        .put(SimpleLightVariant.MOON_LIGHT, LightModelProvider.of(new MoonLightModel()))
        .put(SimpleLightVariant.STAR_LIGHT, LightModelProvider.of(new StarLightModel()))
        .put(SimpleLightVariant.ICICLE_LIGHTS, LightModelProvider.of(
            IntStream.rangeClosed(0, 4).mapToObj(IcicleLightsModel::new).toArray(IcicleLightsModel[]::new),
            (models, i) -> models[i < 0 ? 4 : FLMath.mod(FLMath.hash(i), 4) + 1]
        ))
        .put(SimpleLightVariant.METEOR_LIGHT, LightModelProvider.of(new MeteorLightModel()))
        .put(SimpleLightVariant.OIL_LANTERN, LightModelProvider.of(new OilLanternModel()))
        .put(SimpleLightVariant.CANDLE_LANTERN, LightModelProvider.of(new CandleLanternModel()))
        .put(SimpleLightVariant.INCANDESCENT_LIGHT, LightModelProvider.of(new IncandescentLightModel()))
        .build();

    public LightRenderer() {
    }

    public Data start(final IRenderTypeBuffer source) {
        final IVertexBuilder solid = ClientProxy.SOLID_TEXTURE.func_229311_a_(source, RenderType::func_228638_b_);
        return new Data(solid, TranslucentLightRenderer.get(source, ClientProxy.TRANSLUCENT_TEXTURE));
    }

    public <T extends LightBehavior> LightModel<T> getModel(final Light<?> light, final int index) {
        return this.getModel(light.getVariant(), index);
    }

    @SuppressWarnings("unchecked")
    public <T extends LightBehavior> LightModel<T> getModel(final LightVariant<?> variant, final int index) {
        return (LightModel<T>) this.lights.getOrDefault(variant, this.defaultLight).get(index);
    }

    public void render(final MatrixStack matrix, final Data data, final Light<?> light, final int index, final float delta, final int packedLight, final int packedOverlay) {
        this.render(matrix, data, light, this.getModel(light, index), delta, packedLight, packedOverlay);
    }

    public <T extends LightBehavior> void render(final MatrixStack matrix, final Data data, final Light<T> light, final LightModel<T> model, final float delta, final int packedLight, final int packedOverlay) {
        model.animate(light, light.getBehavior(), delta);
        model.func_225598_a_(matrix, data.solid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
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
        final IVertexBuilder solid;
        final IVertexBuilder translucent;

        Data(final IVertexBuilder solid, final IVertexBuilder translucent) {
            this.solid = solid;
            this.translucent = translucent;
        }
    }
}
