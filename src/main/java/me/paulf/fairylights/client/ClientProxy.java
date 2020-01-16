package me.paulf.fairylights.client;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.renderer.block.entity.FastenerBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.block.entity.LightBlockEntityRenderer;
import me.paulf.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.paulf.fairylights.client.renderer.entity.LadderRenderer;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.entity.LadderEntity;
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
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ClientProxy extends ServerProxy {
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

    @Override
    public void initHandlers() {
        super.initHandlers();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void initRenders() {
        ClientRegistry.bindTileEntityRenderer(FLBlockEntities.FASTENER.orElseThrow(IllegalStateException::new), dispatcher -> new FastenerBlockEntityRenderer(ServerProxy.buildBlockView()));
        ClientRegistry.bindTileEntityRenderer(LightBlockEntity.class, new LightBlockEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.FASTENER.orElseThrow(IllegalStateException::new), FenceFastenerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FLEntities.LADDER.orElseThrow(IllegalStateException::new), LadderRenderer::new);
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
                        color = str.colorAt(i);
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
        Minecraft.getInstance().getTextureManager().registerTexture(new ResourceLocation(FairyLights.ID, "hacky_hook"), new HackyHook());
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
