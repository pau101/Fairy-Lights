package me.paulf.fairylights.client;

import com.google.common.collect.ImmutableList;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.command.ClientCommandProvider;
import me.paulf.fairylights.client.command.JinglerCommand;
import me.paulf.fairylights.client.renderer.block.entity.FastenerBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.block.entity.LetterBuntingRenderer;
import me.paulf.fairylights.client.renderer.block.entity.LightBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.block.entity.PennantBuntingRenderer;
import me.paulf.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.paulf.fairylights.client.renderer.entity.LadderRenderer;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.LightItem;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ClientProxy extends ServerProxy {
    public ClientProxy() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FLClientConfig.SPEC);
    }

    @Override
    protected BiConsumer<JingleMessage, Supplier<NetworkEvent.Context>> createJingleHandler() {
        return new JingleMessage.Handler();
    }

    @Override
    protected BiConsumer<UpdateEntityFastenerMessage, Supplier<NetworkEvent.Context>> createUpdateFastenerEntityHandler() {
        return new UpdateEntityFastenerMessage.Handler();
    }

    @Override
    protected BiConsumer<OpenEditLetteredConnectionScreenMessage, Supplier<NetworkEvent.Context>> createOpenEditLetteredConnectionGUIHandler() {
        return new OpenEditLetteredConnectionScreenMessage.Handler();
    }

    @SuppressWarnings("deprecation")
    public static final Material SOLID_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(FairyLights.ID, "entity/connections"));

    @SuppressWarnings("deprecation")
    public static final Material TRANSLUCENT_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(FairyLights.ID, "entity/connections"));

    @Override
    public void initHandlers() {
        super.initHandlers();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        new ClientCommandProvider.Builder()
            .add(JinglerCommand::register)
            .build()
            .register(MinecraftForge.EVENT_BUS);
        JinglerCommand.register(MinecraftForge.EVENT_BUS);
    }

    @Override
    public void initRenders() {
        ClientRegistry.bindTileEntityRenderer(FLBlockEntities.FASTENER.get(), dispatcher -> new FastenerBlockEntityRenderer(dispatcher, ServerProxy.buildBlockView()));
        ClientRegistry.bindTileEntityRenderer(FLBlockEntities.LIGHT.get(), LightBlockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.FASTENER.get(), FenceFastenerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.LADDER.get(), LadderRenderer::new);
        FMLJavaModLoadingContext.get().getModEventBus().<TextureStitchEvent.Pre>addListener(e -> {
            if (SOLID_TEXTURE.getAtlasLocation().equals(e.getMap().getTextureLocation())) {
                e.addSprite(SOLID_TEXTURE.getTextureLocation());
            }
        });
        ModelLoader.addSpecialModel(FenceFastenerRenderer.MODEL);
        final ImmutableList<ResourceLocation> entityModels = new ImmutableList.Builder<ResourceLocation>()
            .addAll(PennantBuntingRenderer.MODELS.values())
            .addAll(LetterBuntingRenderer.MODELS.values())
            .build();
        entityModels.forEach(ModelLoader::addSpecialModel);
        // Undo sprite uv contraction
        FMLJavaModLoadingContext.get().getModEventBus().<ModelBakeEvent>addListener(e -> {
            entityModels.forEach(path -> {
                final IBakedModel model = Minecraft.getInstance().getModelManager().getModel(path);
                if (model == Minecraft.getInstance().getModelManager().getMissingModel()) {
                    return;
                }
                final TextureAtlasSprite sprite = model.getParticleTexture(EmptyModelData.INSTANCE);
                final int w = (int) (sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU()));
                final int h = (int) (sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV()));
                for (final BakedQuad quad : model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE)) {
                    final int[] data = quad.getVertexData();
                    for (int n = 0; n < 4; n++) {
                        data[n * 8 + 4] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[n * 8 + 4]) * w) / w);
                        data[n * 8 + 5] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[n * 8 + 5]) * h) / h);
                    }
                }
            });
        });
    }

    @Override
    public void initRendersLate() {
        final ItemColors colors = Minecraft.getInstance().getItemColors();
        colors.register(ClientProxy::secondLayerColor,
            FLItems.FAIRY_LIGHT.get(),
            FLItems.PAPER_LANTERN.get(),
            FLItems.ORB_LANTERN.get(),
            FLItems.FLOWER_LIGHT.get(),
            FLItems.ORNATE_LANTERN.get(),
            FLItems.OIL_LANTERN.get(),
            FLItems.JACK_O_LANTERN.get(),
            FLItems.SKULL_LIGHT.get(),
            FLItems.GHOST_LIGHT.get(),
            FLItems.SPIDER_LIGHT.get(),
            FLItems.WITCH_LIGHT.get(),
            FLItems.SNOWFLAKE_LIGHT.get(),
            FLItems.ICICLE_LIGHTS.get(),
            FLItems.METEOR_LIGHT.get()
        );
        colors.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFFFF;
            }
            if (stack.hasTag()) {
                final ListNBT tagList = stack.getTag().getList("pattern", NBT.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    return LightItem.getColorValue(DyeColor.byId(tagList.getCompound((index - 1) % tagList.size()).getByte("color")));
                }
            }
            if (FairyLights.CHRISTMAS.isOccurringNow()) {
                return (index + System.currentTimeMillis() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
            }
            return 0xFFD584;
        }, FLItems.HANGING_LIGHTS.get());
        colors.register((stack, index) -> {
            return LightItem.getColorValue(LightItem.getLightColor(stack));
        }, FLItems.TINSEL.get());
        colors.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFFFF;
            }
            if (stack.hasTag()) {
                final ListNBT tagList = stack.getTag().getList("pattern", NBT.TAG_COMPOUND);
                if (tagList.size() > 0) {
                    return LightItem.getColorValue(DyeColor.byId(tagList.getCompound((index - 1) % tagList.size()).getByte("color")));
                }
            }
            return 0xFFFFFFFF;
        }, FLItems.PENNANT_BUNTING.get());
        colors.register(ClientProxy::secondLayerColor, FLItems.TRIANGLE_PENNANT.get());
        colors.register(ClientProxy::secondLayerColor, FLItems.SPEARHEAD_PENNANT.get());
        colors.register(ClientProxy::secondLayerColor, FLItems.SWALLOWTAIL_PENNANT.get());
        colors.register(ClientProxy::secondLayerColor, FLItems.SQUARE_PENNANT.get());
        colors.register((stack, index) -> {
            if (index > 0 && stack.hasTag()) {
                final StyledString str = StyledString.deserialize(stack.getTag().getCompound("text"));
                if (str.length() > 0) {
                    TextFormatting lastColor = null, color = null;
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
        RenderTypeLookup.setRenderLayer(FLBlocks.FASTENER.get(), RenderType.getCutoutMipped());
        /*RenderTypeLookup.setRenderLayer(FLBlocks.FAIRY_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.PAPER_LANTERN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ORB_LANTERN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.FLOWER_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ORNATE_LANTERN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.OIL_LANTERN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.JACK_O_LANTERN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SKULL_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.GHOST_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SPIDER_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.WITCH_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SNOWFLAKE_LIGHT.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ICICLE_LIGHTS.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.METEOR_LIGHT.get(), RenderType.getCutoutMipped());*/
    }

    private static int secondLayerColor(final ItemStack stack, final int index) {
        return index == 0 ? 0xFFFFFF : LightItem.getColorValue(LightItem.getLightColor(stack));
    }
}
