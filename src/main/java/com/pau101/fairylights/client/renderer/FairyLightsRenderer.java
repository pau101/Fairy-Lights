package com.pau101.fairylights.client.renderer;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.client.model.ModelFairyLights;
import com.pau101.fairylights.tileentity.TileEntityFairyLightsFastener;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class FairyLightsRenderer {
	private Minecraft minecraft;

	private ModelFairyLights fairyLights;

	private ResourceLocation texture;

	private Field currentFrustum;

	public FairyLightsRenderer() {
		minecraft = Minecraft.getMinecraft();
		fairyLights = new ModelFairyLights();
		texture = new ResourceLocation(FairyLights.MODID, "textures/entity/fairy_lights.png");
		currentFrustum = ReflectionHelper.findField(EntityRenderer.class, "currentFrustum");
	}

	private Frustrum getFrustrum() {
		try {
			return (Frustrum) currentFrustum.get(minecraft.entityRenderer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent e) {
		float partialRenderTicks = e.partialTicks;
		World world = minecraft.theWorld;
		Frustrum frustrum = getFrustrum();
		RenderHelper.enableStandardItemLighting();
		minecraft.getTextureManager().bindTexture(texture);
		for (TileEntity loadedTileEntity : (List<TileEntity>) world.loadedTileEntityList) {
			if (loadedTileEntity instanceof TileEntityFairyLightsFastener) {
				TileEntityFairyLightsFastener fairyLightsFastener = (TileEntityFairyLightsFastener) loadedTileEntity;
				if (frustrum.isBoundingBoxInFrustum(fairyLightsFastener.getRenderBoundingBox())) {
					int x = fairyLightsFastener.xCoord, y = fairyLightsFastener.yCoord, z = fairyLightsFastener.zCoord;
					int combinedLight = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
					int sunlight = combinedLight % 65536;
					int moonlight = combinedLight / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
					GL11.glColor3f(1, 1, 1);
					GL11.glPushMatrix();
					GL11.glTranslated(x - TileEntityRendererDispatcher.staticPlayerX, y - TileEntityRendererDispatcher.staticPlayerY, z	- TileEntityRendererDispatcher.staticPlayerZ);
					fairyLights.renderConnections(fairyLightsFastener, partialRenderTicks);
					GL11.glPopMatrix();
				}
			}
		}
	}
}
