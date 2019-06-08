package com.pau101.fairylights.client;

import com.google.common.collect.ForwardingSet;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.midi.CommandJingler;
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
import com.pau101.fairylights.server.item.LightVariant;
import com.pau101.fairylights.server.jingle.JingleLibrary;
import com.pau101.fairylights.util.styledstring.StyledString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public final class ClientProxy extends ServerProxy {
	public static FontRenderer recoloredFont;

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
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		colors.registerItemColorHandler((stack, index) -> {
			if (index == 0 || ItemLight.getLightVariant(stack.getMetadata()) == LightVariant.LUXO_BALL) {
				return 0xFFFFFFFF;
			}
			return ItemLight.getColorValue(ItemLight.getLightColor(stack.getMetadata()));
		}, FLItems.LIGHT);
		colors.registerItemColorHandler((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFFFF;
			}
			if (stack.hasTagCompound()) {
				NBTTagList tagList = stack.getTagCompound().getTagList("pattern", NBT.TAG_COMPOUND);
				if (tagList.tagCount() > 0) {
					return ItemLight.getColorValue(EnumDyeColor.byDyeDamage(tagList.getCompoundTagAt((index - 1) % tagList.tagCount()).getByte("color")));
				}
			}
			if (FairyLights.christmas.isOcurringNow()) {
				return (index + System.currentTimeMillis() / 2000) % 2 == 0 ? 0x993333 : 0x7FCC19;
			}
			return 0xFFD584;
		}, FLItems.HANGING_LIGHTS);
		colors.registerItemColorHandler((stack, index) -> {
			EnumDyeColor color;
			if (stack.hasTagCompound()) {
				color = EnumDyeColor.byDyeDamage(stack.getTagCompound().getByte("color"));
			} else {
				color = EnumDyeColor.BLACK;
			}
			return ItemLight.getColorValue(color);
		}, FLItems.TINSEL);
		colors.registerItemColorHandler((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFFFF;
			}
			if (stack.hasTagCompound()) {
				NBTTagList tagList = stack.getTagCompound().getTagList("pattern", NBT.TAG_COMPOUND);
				if (tagList.tagCount() > 0) {
					return ItemLight.getColorValue(EnumDyeColor.byDyeDamage(tagList.getCompoundTagAt((index - 1) % tagList.tagCount()).getByte("color")));
				}
			}
			return 0xFFFFFFFF;
		}, FLItems.PENNANT_BUNTING);
		colors.registerItemColorHandler((stack, index) -> {
			if (index == 0) {
				return 0xFFFFFF;
			}
			return ItemLight.getColorValue(EnumDyeColor.byDyeDamage(stack.getMetadata()));
		}, FLItems.PENNANT);
		colors.registerItemColorHandler((stack, index) -> {
			if (index > 0 && stack.hasTagCompound()) {
				StyledString str = StyledString.deserialize(stack.getTagCompound().getCompoundTag("text"));
				if (str.length() > 0) {
					TextFormatting lastColor = null, color = null;
					int n = (index - 1) % str.length();
					for (int i = 0; i < str.length(); lastColor = color, i++) {
						color = str.colorAt(i);
						if (lastColor != color && (n-- == 0)) {
							break;
						}
					}
					return StyledString.getColor(recoloredFont, color) | 0xFF000000;
				}
			}
			return 0xFFFFFFFF;
		}, FLItems.LETTER_BUNTING);
		initRecoloredFont();
		try {
			setupRenderGlobal();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// Early runTick hook after getMouseOver
		Minecraft.getMinecraft().getTextureManager().loadTickableTexture(new ResourceLocation(FairyLights.ID, "hacky_hook"), new ITickableTextureObject() {
			@Override
			public void tick() {
				ClientEventHandler.updateHitConnection();
			}

			@Override
			public void setBlurMipmap(boolean blur, boolean mipmap) {}

			@Override
			public void restoreLastBlurMipmap() {}

			@Override
			public void loadTexture(IResourceManager mgr) {}

			@Override
			public int getGlTextureId() {
				return 0;
			}
		});
	}

	private void initRecoloredFont() {
		recoloredFont = new FontRenderer(
			Minecraft.getMinecraft().gameSettings,
			new ResourceLocation("textures/font/ascii.png"),
			Minecraft.getMinecraft().renderEngine, false
		);
		int[] colorCode = ReflectionHelper.getPrivateValue(FontRenderer.class, recoloredFont, "field_78285_g", "colorCode");
		// Brighten black a bit to make it visible on black
		colorCode[0] = 0x191919;
		colorCode[16] = 0x040404;
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(recoloredFont);
	}

	@Override
	public void initEggs() {
		super.initEggs();
		CommandJingler cmd = new CommandJingler();
		ClientCommandHandler.instance.registerCommand(cmd);
		MinecraftForge.EVENT_BUS.register(cmd);
	}

	@Override
	protected void loadJingleLibraries() {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(m -> JingleLibrary.loadAll());
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
		RenderGlobal render = Minecraft.getMinecraft().renderGlobal;
		Field setTileEntities = ReflectionHelper.findField(RenderGlobal.class, "field_181024_n", "setTileEntities");
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
