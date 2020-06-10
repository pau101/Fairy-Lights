package me.paulf.fairylights.client.renderer.block.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.client.model.light.ColorOilLanternModel;
import me.paulf.fairylights.client.model.light.FairyLightModel;
import me.paulf.fairylights.client.model.light.FlowerLightModel;
import me.paulf.fairylights.client.model.light.GhostLightModel;
import me.paulf.fairylights.client.model.light.IcicleLightsModel;
import me.paulf.fairylights.client.model.light.JackOLanternLightModel;
import me.paulf.fairylights.client.model.light.LightModel;
import me.paulf.fairylights.client.model.light.MeteorLightModel;
import me.paulf.fairylights.client.model.light.OilLanternModel;
import me.paulf.fairylights.client.model.light.OrbLanternModel;
import me.paulf.fairylights.client.model.light.CandleLanternModel;
import me.paulf.fairylights.client.model.light.PaperLanternModel;
import me.paulf.fairylights.client.model.light.SkullLightModel;
import me.paulf.fairylights.client.model.light.SnowflakeLightModel;
import me.paulf.fairylights.client.model.light.SpiderLightModel;
import me.paulf.fairylights.client.model.light.WitchLightModel;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.Light;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.LightBehavior;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.util.Mth;
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
        .put(SimpleLightVariant.FAIRY, LightModelProvider.of(new FairyLightModel()))
        .put(SimpleLightVariant.PAPER, LightModelProvider.of(new PaperLanternModel()))
        .put(SimpleLightVariant.ORB, LightModelProvider.of(new OrbLanternModel()))
        .put(SimpleLightVariant.FLOWER, LightModelProvider.of(new FlowerLightModel()))
        .put(SimpleLightVariant.CANDLE, LightModelProvider.of(new CandleLanternModel()))
        .put(SimpleLightVariant.OIL, LightModelProvider.of(new ColorOilLanternModel()))
        .put(SimpleLightVariant.JACK_O_LANTERN, LightModelProvider.of(new JackOLanternLightModel()))
        .put(SimpleLightVariant.SKULL, LightModelProvider.of(new SkullLightModel()))
        .put(SimpleLightVariant.GHOST, LightModelProvider.of(new GhostLightModel()))
        .put(SimpleLightVariant.SPIDER, LightModelProvider.of(new SpiderLightModel()))
        .put(SimpleLightVariant.WITCH, LightModelProvider.of(new WitchLightModel()))
        .put(SimpleLightVariant.SNOWFLAKE, LightModelProvider.of(new SnowflakeLightModel()))
        .put(SimpleLightVariant.ICICLE, LightModelProvider.of(
            IntStream.rangeClosed(0, 4).mapToObj(IcicleLightsModel::new).toArray(IcicleLightsModel[]::new),
            (models, i) -> models[i < 0 ? 4 : Mth.mod(Mth.hash(i), 4) + 1]
        ))
        .put(SimpleLightVariant.METEOR, LightModelProvider.of(new MeteorLightModel()))
        .put(SimpleLightVariant.TORCH_LANTERN, LightModelProvider.of(new OilLanternModel()))
        .build();

    public LightRenderer() {
    }

    public Data start(final IRenderTypeBuffer source) {
        final IVertexBuilder solid = ClientProxy.SOLID_TEXTURE.getBuffer(source, RenderType::getEntityCutout);
        final IVertexBuilder translucent = ClientProxy.TRANSLUCENT_TEXTURE.getBuffer(source, RenderType::getEntityTranslucent);
        return new Data(solid, translucent);
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
        model.animate(light, delta);
        model.render(matrix, data.solid, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
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
