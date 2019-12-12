package com.pau101.fairylights.client;

import com.google.common.collect.ForwardingSet;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.renderer.FenceFastenerRendererDispatcher;
import com.pau101.fairylights.client.renderer.FenceFastenerRepresentative;
import com.pau101.fairylights.client.renderer.block.entity.BlockEntityFastenerRenderer;
import com.pau101.fairylights.client.renderer.entity.RenderFenceFastener;
import com.pau101.fairylights.client.renderer.entity.RenderLadder;
import com.pau101.fairylights.server.ServerProxy;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.entity.EntityLadder;
import com.pau101.fairylights.server.item.FLItems;
import com.pau101.fairylights.server.item.ItemLight;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.server.net.clientbound.MessageJingle;
import com.pau101.fairylights.server.net.clientbound.MessageOpenEditLetteredConnectionGUI;
import com.pau101.fairylights.server.net.clientbound.MessageUpdateFastenerEntity;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ClientProxy extends ServerProxy {
	@Override
	protected BiConsumer<MessageJingle, Supplier<NetworkEvent.Context>> createJingleHandler() {
		return new MessageJingle.Handler();
	}

	@Override
	protected BiConsumer<MessageUpdateFastenerEntity, Supplier<NetworkEvent.Context>> createUpdateFastenerEntityHandler() {
		return new MessageUpdateFastenerEntity.Handler();
	}

	@Override
	protected BiConsumer<MessageOpenEditLetteredConnectionGUI, Supplier<NetworkEvent.Context>> createOpenEditLetteredConnectionGUIHandler() {
		return new MessageOpenEditLetteredConnectionGUI.Handler();
	}

	@Override
	public void initHandlers() {
		super.initHandlers();
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}

	@Override
	public void initRenders() {
		ClientRegistry.bindTileEntitySpecialRenderer(BlockEntityFastener.class, new BlockEntityFastenerRenderer(ServerProxy.buildBlockView()));
		ClientRegistry.bindTileEntitySpecialRenderer(FenceFastenerRepresentative.class, FenceFastenerRendererDispatcher.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityFenceFastener.class, RenderFenceFastener::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLadder.class, RenderLadder::new);
	}

	@Override
	public void initRendersLate() {
		ItemColors colors = Minecraft.getInstance().getItemColors();
		colors.register((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFFFF;
			}
			return ItemLight.getColorValue(ItemLight.getLightColor(stack));
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
				ListNBT tagList = stack.getTag().getList("pattern", NBT.TAG_COMPOUND);
				if (tagList.size() > 0) {
					return ItemLight.getColorValue(DyeColor.byId(tagList.getCompound((index - 1) % tagList.size()).getByte("color")));
				}
			}
			if (FairyLights.christmas.isOcurringNow()) {
				return (index + System.currentTimeMillis() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
			}
			return 0xFFD584;
		}, FLItems.HANGING_LIGHTS.orElseThrow(IllegalStateException::new));
		colors.register((stack, index) -> {
			DyeColor color;
			if (stack.hasTag()) {
				color = DyeColor.byId(stack.getTag().getByte("color"));
			} else {
				color = DyeColor.BLACK;
			}
			return ItemLight.getColorValue(color);
		}, FLItems.TINSEL.orElseThrow(IllegalStateException::new));
		colors.register((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFFFF;
			}
			if (stack.hasTag()) {
				ListNBT tagList = stack.getTag().getList("pattern", NBT.TAG_COMPOUND);
				if (tagList.size() > 0) {
					return ItemLight.getColorValue(DyeColor.byId(tagList.getCompound((index - 1) % tagList.size()).getByte("color")));
				}
			}
			return 0xFFFFFFFF;
		}, FLItems.PENNANT_BUNTING.orElseThrow(IllegalStateException::new));
		colors.register((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFF;
			}
			return ItemLight.getColorValue(ItemLight.getLightColor(stack));
		}, FLItems.PENNANT.orElseThrow(IllegalStateException::new));
		colors.register((stack, index) -> {
			if (index > 0 && stack.hasTag()) {
				StyledString str = StyledString.deserialize(stack.getTag().getCompound("text"));
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
		try {
			setupRenderGlobal();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// Early runTick hook after getMouseOver
		Minecraft.getInstance().getTextureManager().loadTickableTexture(new ResourceLocation(FairyLights.ID, "hacky_hook"), new ITickableTextureObject() {
			@Override
			public void tick() {
				ClientEventHandler.updateHitConnection();
			}

			@Override
			public void setBlurMipmap(boolean blur, boolean mipmap) {}

			@Override
			public void restoreLastBlurMipmap() {}

			@Override
			public void loadTexture(IResourceManager manager) {}

			@Override
			public int getGlTextureId() {
				return 0;
			}
		});
	}

	@Override
	protected void loadJingleLibraries() {
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) -> {
			if (resourcePredicate.test(VanillaResourceType.SOUNDS)) {
				JingleLibrary.loadAll();
			}
		});
	}

	/*
	 * Entities are only rendered if the chunk they're
	 * contained in is visible. This poses a problem
	 * for fence fasteners, they're connections can
	 * easily extend into other chunks that are visible.
	 * Rendering fence fastener entities during
	 * RenderWorldLastEvent isn't really ideal since
	 * it produces unsatisfactory results with shaders.
	 * We need to get our fence fasteners to render
	 * during regular block entity rendering and the
	 * best way I've found to do that with no ASM is by
	 * hackily substituting the set of global rendering
	 * block entities with our own forwarding version
	 * which always contains a dummy block entity
	 * (FenceFastenerRepresentative) bound to
	 * FenceFastenerRendererDispatcher. This renderer
	 * keeps track of all the fence fasteners on the
	 * client and renders them all in place of actually
	 * rendering a normal block entity.
	 */
	private void setupRenderGlobal() throws Exception {
		WorldRenderer render = Minecraft.getInstance().worldRenderer;
		Field setTileEntities = ObfuscationReflectionHelper.findField(WorldRenderer.class, "field_181024_n");
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.set(setTileEntities, setTileEntities.getModifiers() & ~Modifier.FINAL);
		Set<TileEntity> value = (Set<TileEntity>) setTileEntities.get(render);
		setTileEntities.set(render, new ForwardingSet<TileEntity>() {
			@Override
			protected Set<TileEntity> delegate() {
				return value;
			}

			@Override
			public void clear() {
				super.clear();
				add(FenceFastenerRepresentative.INSTANCE);
			}
		});
	}
}
