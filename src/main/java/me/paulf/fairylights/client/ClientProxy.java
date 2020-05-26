package me.paulf.fairylights.client;

import com.google.common.collect.*;
import me.paulf.fairylights.*;
import me.paulf.fairylights.client.command.*;
import me.paulf.fairylights.client.renderer.block.entity.*;
import me.paulf.fairylights.client.renderer.entity.*;
import me.paulf.fairylights.server.*;
import me.paulf.fairylights.server.block.*;
import me.paulf.fairylights.server.block.entity.*;
import me.paulf.fairylights.server.entity.*;
import me.paulf.fairylights.server.item.*;
import me.paulf.fairylights.server.net.clientbound.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.util.Constants.*;
import net.minecraftforge.fml.client.registry.*;
import net.minecraftforge.fml.javafmlmod.*;
import net.minecraftforge.fml.network.*;

import java.util.*;
import java.util.function.*;

public final class ClientProxy extends ServerProxy {
    public ClientProxy() {
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
        ClientRegistry.bindTileEntityRenderer(FLBlockEntities.FASTENER.orElseThrow(IllegalStateException::new), dispatcher -> new FastenerBlockEntityRenderer(dispatcher, ServerProxy.buildBlockView()));
        ClientRegistry.bindTileEntityRenderer(FLBlockEntities.LIGHT.orElseThrow(IllegalStateException::new), LightBlockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.FASTENER.orElseThrow(IllegalStateException::new), FenceFastenerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.LADDER.orElseThrow(IllegalStateException::new), LadderRenderer::new);
        FMLJavaModLoadingContext.get().getModEventBus().<TextureStitchEvent.Pre>addListener(e -> {
            if (SOLID_TEXTURE.getAtlasLocation().equals(e.getMap().getTextureLocation())) {
                e.addSprite(SOLID_TEXTURE.getTextureLocation());
            }
        });
        final ImmutableList<ResourceLocation> entityModels = new ImmutableList.Builder<ResourceLocation>()
            .add(PennantBuntingRenderer.MODEL)
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
        colors.register((stack, index) -> {
                if (index == 0) {
                    return 0xFFFFFFFF;
                }
                return LightItem.getColorValue(LightItem.getLightColor(stack));
            },
            FLItems.FAIRY_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.PAPER_LANTERN.orElseThrow(IllegalStateException::new),
            FLItems.ORB_LANTERN.orElseThrow(IllegalStateException::new),
            FLItems.FLOWER_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.ORNATE_LANTERN.orElseThrow(IllegalStateException::new),
            FLItems.OIL_LANTERN.orElseThrow(IllegalStateException::new),
            FLItems.JACK_O_LANTERN.orElseThrow(IllegalStateException::new),
            FLItems.SKULL_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.GHOST_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.SPIDER_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.WITCH_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.SNOWFLAKE_LIGHT.orElseThrow(IllegalStateException::new),
            FLItems.ICICLE_LIGHTS.orElseThrow(IllegalStateException::new),
            FLItems.METEOR_LIGHT.orElseThrow(IllegalStateException::new)
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
        }, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
        colors.register((stack, index) -> {
            final DyeColor color;
            if (stack.hasTag()) {
                color = DyeColor.byId(stack.getTag().getByte("color"));
            } else {
                color = DyeColor.BLACK;
            }
            return LightItem.getColorValue(color);
        }, FLItems.TINSEL.orElseThrow(IllegalStateException::new));
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
        }, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new));
        colors.register((stack, index) -> {
            if (index == 0) {
                return 0xFFFFFF;
            }
            return LightItem.getColorValue(LightItem.getLightColor(stack));
        }, FLItems.PENNANT.orElseThrow(IllegalStateException::new));
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
        }, FLItems.LETTER_BUNTING.orElseThrow(IllegalStateException::new));
        // Early runTick hook after getMouseOver
        class HackyHook extends Texture implements ITickable {
            @Override
            public void tick() {
                ClientEventHandler.updateHitConnection();
            }

            @Override
            public void loadTexture(final IResourceManager manager) {
            }
        }
        Minecraft.getInstance().getTextureManager().loadTexture(new ResourceLocation(FairyLights.ID, "hacky_hook"), new HackyHook());
        RenderTypeLookup.setRenderLayer(FLBlocks.FASTENER.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        /*RenderTypeLookup.setRenderLayer(FLBlocks.FAIRY_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.PAPER_LANTERN.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ORB_LANTERN.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.FLOWER_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ORNATE_LANTERN.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.OIL_LANTERN.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.JACK_O_LANTERN.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SKULL_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.GHOST_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SPIDER_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.WITCH_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.SNOWFLAKE_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.ICICLE_LIGHTS.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FLBlocks.METEOR_LIGHT.orElseThrow(IllegalStateException::new), RenderType.getCutoutMipped());*/
    }
}
