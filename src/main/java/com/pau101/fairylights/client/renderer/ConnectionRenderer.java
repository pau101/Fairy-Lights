package com.pau101.fairylights.client.renderer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.block.BlockConnectionFastener;
import com.pau101.fairylights.block.BlockConnectionFastenerFence;
import com.pau101.fairylights.client.model.connection.ModelConnection;
import com.pau101.fairylights.connection.ConnectionLogicGarland;
import com.pau101.fairylights.connection.ConnectionType;
import com.pau101.fairylights.tileentity.TileEntityConnectionFastener;
import com.pau101.fairylights.tileentity.connection.Connection;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.MathUtils;
import com.pau101.fairylights.util.Segment;
import com.pau101.fairylights.util.mc.EnumFacing;
import com.pau101.fairylights.util.mc.EnumFacing.Axis;
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ConnectionRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.MODID, "textures/entity/fairy_lights.png");

	public static final int TEXTURE_WIDTH = 128;

	public static final int TEXTURE_HEIGHT = 128;

	private Field currentFrustumField;

	private ModelConnection[] connectionRenderers;

	public ConnectionRenderer() {
		currentFrustumField = ReflectionHelper.findField(EntityRenderer.class, "currentFrustum");
		ConnectionType[] types = ConnectionType.values();
		connectionRenderers = new ModelConnection[types.length];
		for (int i = 0; i < types.length; i++) {
			connectionRenderers[i] = types[i].createRenderer();
		}
	}

	private Frustrum getFrustrum() {
		try {
			return (Frustrum) currentFrustumField.get(Minecraft.getMinecraft().entityRenderer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		float delta = e.partialTicks;
		World world = mc.theWorld;
		Frustrum frustrum = getFrustrum();
		mc.getTextureManager().bindTexture(TEXTURE);
		mc.entityRenderer.enableLightmap(delta);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL11.GL_FOG);
		for (TileEntity loadedTileEntity : (List<TileEntity>) world.loadedTileEntityList) {
			if (loadedTileEntity instanceof TileEntityConnectionFastener) {
				TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) loadedTileEntity;
				if (frustrum.isBoundingBoxInFrustum(fastener.getRenderBoundingBox())) {
					int x = fastener.xCoord, y = fastener.yCoord, z = fastener.zCoord;
					int combinedLight = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
					int sunlight = combinedLight % 65536;
					int moonlight = combinedLight / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
					GL11.glColor3f(1, 1, 1);
					GL11.glPushMatrix();
					GL11.glTranslated(x - TileEntityRendererDispatcher.staticPlayerX, y - TileEntityRendererDispatcher.staticPlayerY, z	- TileEntityRendererDispatcher.staticPlayerZ);
					renderConnections(fastener, delta);
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glDisable(GL11.GL_FOG);
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap(delta);
	}

	public void renderConnections(TileEntityConnectionFastener fastener, float delta) {
		World world = fastener.getWorldObj();
		if (world == null || !(world.getBlock(fastener.xCoord, fastener.yCoord, fastener.zCoord) instanceof BlockConnectionFastener)) {
			return;
		}
		List<Connection> connections = removeUnnecessaryConnections(fastener.getConnections());
		GL11.glPushMatrix();
		Point3f offset = ((BlockConnectionFastener) fastener.getBlockType()).getOffsetForData(fastener.getWorldObj().getBlockMetadata(fastener.xCoord, fastener.yCoord, fastener.zCoord), 0.125F);
		GL11.glTranslatef(offset.x, offset.y, offset.z);
		int blockBrightness = fastener.getWorldObj().getLightBrightnessForSkyBlocks(fastener.xCoord, fastener.yCoord, fastener.zCoord, 0);
		int skylight = blockBrightness % 65536;
		int moonlight = blockBrightness / 65536;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		boolean shouldRenderBow = true;
		for (Connection connection : connections) {
			if (connection.isOrigin()) {
				ModelConnection renderer = connectionRenderers[connection.getType().ordinal()];
				renderer.render(fastener, connection.getLogic(), world, skylight, moonlight, delta);
			}
			if (connection.getLogic() instanceof ConnectionLogicGarland && shouldRenderBow) {
				renderBow(fastener, connections, delta);
				shouldRenderBow = false;
			}
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor3f(1, 1, 1);
		GL11.glPopMatrix();
	}

	private void renderBow(TileEntityConnectionFastener fastener, Collection<Connection> connections, float delta) {
		if (fastener.getBlockType() instanceof BlockConnectionFastenerFence) {
			return;
		}
		EnumFacing facing = BlockConnectionFastener.DATA_TO_FACING[fastener.getBlockMetadata()];
		if (facing.getAxis() == Axis.Y) {
			return;
		}
		Vector3f fastenerDir = new Vector3f(facing.getDirectionVec());
		float yaw = (float) -Math.atan2(fastenerDir.z, fastenerDir.x) - MathUtils.HALF_PI;
		GL11.glPushMatrix();
		GL11.glTranslatef(fastenerDir.x * 1.5F / 16, 0, fastenerDir.z * 1.5F / 16);
		GL11.glRotatef(yaw * MathUtils.RAD_TO_DEG, 0, 1, 0);
		GL11.glTranslatef(-9F / 16, -6F / 16, -0.5F / 16);
		GL11.glScalef(1, 1, 2.5F);
		render3DTexture(18, 12, 0, 72);
		GL11.glPopMatrix();
	}

	private static List<Connection> removeUnnecessaryConnections(Collection<Connection> connections) {
		List<Connection> visibleConnections = new ArrayList<Connection>();
		Iterator<Connection> connectionIterator = connections.iterator();
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			if (connection.getTo() == null) {
				continue;
			}
			Segment[] vertices = catenary.getSegments();
			if (vertices == null) {
				continue;
			}
			visibleConnections.add(connection);
		}
		return visibleConnections;
	}

	public static void render3DTexture(int width, int height, int u, int v) {
		float u1 = u / (float) TEXTURE_WIDTH;
		float u2 = (u + width) / (float) TEXTURE_WIDTH;
		float v1 = v / (float) TEXTURE_HEIGHT;
		float v2 = (v + height) / (float) TEXTURE_HEIGHT;
		GL11.glPushMatrix();
		GL11.glScalef(width / 16F, height / 16F, 1);
		Tessellator tessellator = Tessellator.instance;
		float depth = 0.0625F;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, 1);
		tessellator.addVertexWithUV(0, 0, 0, u1, v2);
		tessellator.addVertexWithUV(1, 0, 0, u2, v2);
		tessellator.addVertexWithUV(1, 1, 0, u2, v1);
		tessellator.addVertexWithUV(0, 1, 0, u1, v1);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, -1);
		tessellator.addVertexWithUV(0, 1, -depth, u1, v1);
		tessellator.addVertexWithUV(1, 1, -depth, u2, v1);
		tessellator.addVertexWithUV(1, 0, -depth, u2, v2);
		tessellator.addVertexWithUV(0, 0, -depth, u1, v2);
		tessellator.draw();
		float widthStretch = 0.5F * (u1 - u2) / width;
		float heightStretch = 0.5F * (v2 - v1) / height;
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1, 0, 0);
		for (int p = 0; p < width; p++) {
			float x = (float) p / width;
			float ui = u1 + (u2 - u1) * x - widthStretch;
			tessellator.addVertexWithUV(x, 0, -depth, ui, v2);
			tessellator.addVertexWithUV(x, 0, 0, ui, v2);
			tessellator.addVertexWithUV(x, 1, 0, ui, v1);
			tessellator.addVertexWithUV(x, 1, -depth, ui, v1);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1, 0, 0);
		for (int p = 0; p < width; p++) {
			float xi = (float) p / width;
			float ui = u1 + (u2 - u1) * xi - widthStretch;
			float x = xi + 1F / width;
			tessellator.addVertexWithUV(x, 1, -depth, ui, v1);
			tessellator.addVertexWithUV(x, 1, 0, ui, v1);
			tessellator.addVertexWithUV(x, 0, 0, ui, v2);
			tessellator.addVertexWithUV(x, 0, -depth, ui, v2);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 1, 0);
		for (int p = 0; p < height; p++) {
			float yi = (float) p / height;
			float vi = v2 + (v1 - v2) * yi - heightStretch;
			float y = yi + 1.0F / height;
			tessellator.addVertexWithUV(0, y, 0, u1, vi);
			tessellator.addVertexWithUV(1, y, 0, u2, vi);
			tessellator.addVertexWithUV(1, y, -depth, u2, vi);
			tessellator.addVertexWithUV(0, y, -depth, u1, vi);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, -1, 0);
		for (int p = 0; p < height; p++) {
			float y = (float) p / height;
			float vi = v2 + (v1 - v2) * y - heightStretch;
			tessellator.addVertexWithUV(1, y, 0, u2, vi);
			tessellator.addVertexWithUV(0, y, 0, u1, vi);
			tessellator.addVertexWithUV(0, y, -depth, u1, vi);
			tessellator.addVertexWithUV(1, y, -depth, u2, vi);
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}
}
