package me.paulf.fairylights.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.command.JinglerCommand;
import me.paulf.fairylights.client.model.light.BowModel;
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
import me.paulf.fairylights.client.renderer.block.entity.FastenerBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.block.entity.GarlandTinselRenderer;
import me.paulf.fairylights.client.renderer.block.entity.GarlandVineRenderer;
import me.paulf.fairylights.client.renderer.block.entity.HangingLightsRenderer;
import me.paulf.fairylights.client.renderer.block.entity.LetterBuntingRenderer;
import me.paulf.fairylights.client.renderer.block.entity.LightBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.block.entity.PennantBuntingRenderer;
import me.paulf.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.paulf.fairylights.client.tutorial.ClippyController;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.feature.light.ColorChangingBehavior;
import me.paulf.fairylights.server.item.DyeableItem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.HangingLightsConnectionItem;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ClientProxy extends ServerProxy {
    @SuppressWarnings("deprecation")
    public static final Material SOLID_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(FairyLights.ID, "entity/connections"));

    @SuppressWarnings("deprecation")
    public static final Material TRANSLUCENT_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(FairyLights.ID, "entity/connections"));

    private final ImmutableList<ResourceLocation> entityModels = new ImmutableList.Builder<ResourceLocation>()
        .addAll(PennantBuntingRenderer.MODELS)
        .addAll(LetterBuntingRenderer.MODELS.values())
        .build();

    @Override
    public void init(final IEventBus modBus) {
        super.init(modBus);
        new ClippyController().init(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FLClientConfig.SPEC);
        ClientEventHandler clientEventHandler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(clientEventHandler);
        modBus.<RegisterGuiOverlaysEvent>addListener(e -> {
            e.registerBelowAll("overlay", clientEventHandler::renderOverlay);
        });
        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent e) -> JinglerCommand.register(e.getDispatcher()));
        JinglerCommand.register(MinecraftForge.EVENT_BUS);

        modBus.addListener(this::setup);
        modBus.addListener(this::setupLayerDefinitions);
        modBus.addListener(this::setupColors);
        modBus.addListener(this::setupModels);
    }

    private int getUvIndex(VertexFormat vertexFormat) {
        int position = 0;
        for (final VertexFormatElement ee : vertexFormat.getElements()) {
            if (ee.getUsage() == VertexFormatElement.Usage.UV) {
                if (position % 4 == 0) {
                    return position / 4;
                }
                break;
            }
            position += ee.getByteSize();
        }
        return -1;
    }

    private void recomputeUv(final int stride, final int finalUvOffset, final BakedModel model) {
        final TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uSize = uMax - uMin;
        float vSize = vMax - vMin;

        final int w = (int) (uSize / (sprite.getU1() - sprite.getU0()));
        final int h = (int) (vSize / (sprite.getV1() - sprite.getV0()));
        for (final BakedQuad quad : model.getQuads(null, null, RandomSource.create(42L), ModelData.EMPTY, RenderType.cutoutMipped())) {
            final int[] data = quad.getVertices();
            for (int n = 0; n < 4; n++) {
                int iu = n * stride + finalUvOffset;
                int iv = n * stride + finalUvOffset + 1;
                data[iu] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iu]) * w) / w);
                data[iv] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iv]) * h) / h);
            }
        }
    }

    private void setup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(FLBlockEntities.FASTENER.get(), context -> new FastenerBlockEntityRenderer(context, ServerProxy.buildBlockView()));
        BlockEntityRenderers.register(FLBlockEntities.LIGHT.get(), LightBlockEntityRenderer::new);
        EntityRenderers.register(FLEntities.FASTENER.get(), FenceFastenerRenderer::new);
        /*final LightRenderer r = new LightRenderer();
        final StringBuilder bob = new StringBuilder();
        FLItems.lights().forEach(l -> {
            final LightModel<?> model = r.getModel(l.getBlock().getVariant(), -1);
            final AxisAlignedBB bb = model.getBounds();
            bob.append(String.format("%n%s new AxisAlignedBB(%.3fD, %.3fD, %.3fD, %.3fD, %.3fD, %.3fD), %.3fD", l.getRegistryName(), bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, model.getFloorOffset()));
        });
        LogManager.getLogger().debug("waldo {}", bob);*/
    }

    private void setupLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FLModelLayers.BOW, BowModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.GARLAND_RINGS, GarlandVineRenderer.RingsModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.TINSEL_STRIP, GarlandTinselRenderer.StripModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.FAIRY_LIGHT, FairyLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.PAPER_LANTERN, PaperLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.ORB_LANTERN, OrbLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.FLOWER_LIGHT, FlowerLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.CANDLE_LANTERN_LIGHT, ColorCandleLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.OIL_LANTERN_LIGHT, ColorOilLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.JACK_O_LANTERN, JackOLanternLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SKULL_LIGHT, SkullLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.GHOST_LIGHT, GhostLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SPIDER_LIGHT, SpiderLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.WITCH_LIGHT, WitchLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.SNOWFLAKE_LIGHT, SnowflakeLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.HEART_LIGHT, HeartLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.MOON_LIGHT, MoonLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.STAR_LIGHT, StarLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_1, () -> IcicleLightsModel.createLayer(1));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_2, () -> IcicleLightsModel.createLayer(2));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_3, () -> IcicleLightsModel.createLayer(3));
        event.registerLayerDefinition(FLModelLayers.ICICLE_LIGHTS_4, () -> IcicleLightsModel.createLayer(4));
        event.registerLayerDefinition(FLModelLayers.METEOR_LIGHT, MeteorLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.OIL_LANTERN, OilLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.CANDLE_LANTERN, CandleLanternModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.INCANDESCENT_LIGHT, IncandescentLightModel::createLayer);
        event.registerLayerDefinition(FLModelLayers.LETTER_WIRE, LetterBuntingRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.PENNANT_WIRE, PennantBuntingRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.TINSEL_WIRE, GarlandTinselRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.VINE_WIRE, GarlandVineRenderer::wireLayer);
        event.registerLayerDefinition(FLModelLayers.LIGHTS_WIRE, HangingLightsRenderer::wireLayer);
    }

    private void setupModels(final ModelEvent.RegisterAdditional event) {
        event.register(FenceFastenerRenderer.MODEL);
        this.entityModels.forEach(event::register);
    }

    private void setupColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> {
            if (index == 1) {
                if (ColorChangingBehavior.exists(stack)) {
                    return ColorChangingBehavior.animate(stack);
                }
                return DyeableItem.getColor(stack);
            }
            return 0xFFFFFF;
        },
            FLItems.FAIRY_LIGHT.get(),
            FLItems.PAPER_LANTERN.get(),
            FLItems.ORB_LANTERN.get(),
            FLItems.FLOWER_LIGHT.get(),
            FLItems.CANDLE_LANTERN_LIGHT.get(),
            FLItems.OIL_LANTERN_LIGHT.get(),
            FLItems.JACK_O_LANTERN.get(),
            FLItems.SKULL_LIGHT.get(),
            FLItems.GHOST_LIGHT.get(),
            FLItems.SPIDER_LIGHT.get(),
            FLItems.WITCH_LIGHT.get(),
            FLItems.SNOWFLAKE_LIGHT.get(),
            FLItems.HEART_LIGHT.get(),
            FLItems.MOON_LIGHT.get(),
            FLItems.STAR_LIGHT.get(),
            FLItems.ICICLE_LIGHTS.get(),
            FLItems.METEOR_LIGHT.get()
        );
        event.register((stack, index) -> {
            final CompoundTag tag = stack.getTag();
            if (index == 0) {
                if (tag != null) {
                    return HangingLightsConnectionItem.getString(tag).getColor();
                }
                return StringTypes.BLACK_STRING.get().getColor();
            }
            if (tag != null) {
                final ListTag tagList = tag.getList("pattern", Tag.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    final ItemStack item = ItemStack.of(tagList.getCompound((index - 1) % tagList.size()));
                    if (ColorChangingBehavior.exists(item)) {
                        return ColorChangingBehavior.animate(item);
                    }
                    return DyeableItem.getColor(item);
                }
            }
            if (FairyLights.CHRISTMAS.isOccurringNow()) {
                return (index + Util.getMillis() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
            }
            if (FairyLights.HALLOWEEN.isOccurringNow()) {
                return index % 2 == 0 ? 0xf9801d : 0x8932b8;
            }
            return 0xFFD584;
        }, FLItems.HANGING_LIGHTS.get());
        event.register((stack, index) -> index == 0 ? DyeableItem.getColor(stack) : 0xFFFFFFFF, FLItems.TINSEL.get());
        event.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFFFF;
            }
            final CompoundTag tag = stack.getTag();
            if (tag != null) {
                final ListTag tagList = tag.getList("pattern", Tag.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    final ItemStack light = ItemStack.of(tagList.getCompound((index - 1) % tagList.size()));
                    return DyeableItem.getColor(light);
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.PENNANT_BUNTING.get());
        event.register(ClientProxy::secondLayerColor, FLItems.TRIANGLE_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SPEARHEAD_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SWALLOWTAIL_PENNANT.get());
        event.register(ClientProxy::secondLayerColor, FLItems.SQUARE_PENNANT.get());
        event.register((stack, index) -> {
            final CompoundTag tag = stack.getTag();
            if (index > 0 && tag != null) {
                final StyledString str = StyledString.deserialize(tag.getCompound("text"));
                if (str.length() > 0) {
                    ChatFormatting lastColor = null, color = null;
                    int n = (index - 1) % str.length();
                    for (int i = 0; i < str.length(); lastColor = color, i++) {
                        color = str.styleAt(i).getColor();
                        if (lastColor != color && (n-- == 0)) {
                            break;
                        }
                    }
                    return StyledString.getColor(color) | 0xFF000000;
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.LETTER_BUNTING.get());
    }

    private static int secondLayerColor(final ItemStack stack, final int index) {
        return index == 0 ? 0xFFFFFF : DyeableItem.getColor(stack);
    }
}
