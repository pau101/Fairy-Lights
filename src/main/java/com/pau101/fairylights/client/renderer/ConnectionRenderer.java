package com.pau101.fairylights.client.renderer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import org.lwjgl.opengl.GL11;

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
import com.pau101.fairylights.util.vectormath.Point3f;
import com.pau101.fairylights.util.vectormath.Vector3f;

public class ConnectionRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.MODID, "textures/entity/fairy_lights.png");

	private static final Comparator<Vector3f> Y_AXIS_ANGLE_COMPARATOR = new Comparator<Vector3f>() {
		@Override
		public int compare(Vector3f a, Vector3f b) {
			double angleA = Math.atan2(a.z, a.x);
			double angleB = Math.atan2(b.z, b.x);
			return angleA < angleB ? -1 : angleA > angleB ? 1 : 0;
		}
	};

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

	private Frustum getFrustum() {
		try {
			return (Frustum) currentFrustumField.get(Minecraft.getMinecraft().entityRenderer);
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
		Frustum frustum = getFrustum();
		mc.getTextureManager().bindTexture(TEXTURE);
		mc.entityRenderer.enableLightmap();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableFog();
		for (TileEntity loadedTileEntity : (List<TileEntity>) world.loadedTileEntityList) {
			if (loadedTileEntity instanceof TileEntityConnectionFastener) {
				TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) loadedTileEntity;
				if (frustum.isBoundingBoxInFrustum(fastener.getRenderBoundingBox())) {
					BlockPos pos = fastener.getPos();
					int combinedLight = world.getCombinedLight(pos, 0);
					int sunlight = combinedLight % 65536;
					int moonlight = combinedLight / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
					GlStateManager.color(1, 1, 1);
					GlStateManager.pushMatrix();
					GlStateManager.translate(pos.getX() - TileEntityRendererDispatcher.staticPlayerX, pos.getY() - TileEntityRendererDispatcher.staticPlayerY, pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);
					renderConnections(fastener, delta);
					GlStateManager.popMatrix();
				}
			}
		}
		GlStateManager.disableFog();
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();
	}

	private void renderConnections(TileEntityConnectionFastener fastener, float delta) {
		World world = fastener.getWorld();
		if (world == null || !(world.getBlockState(fastener.getPos()).getBlock() instanceof BlockConnectionFastener)) {
			return;
		}
		List<Connection> connections = removeUnnecessaryConnections(fastener.getConnections());
		GlStateManager.pushMatrix();
		Point3f offset = ((BlockConnectionFastener) fastener.getBlockType()).getOffsetForData(fastener.getBlockType() instanceof BlockConnectionFastenerFence ? null : (EnumFacing) fastener.getWorld().getBlockState(fastener.getPos()).getValue(BlockConnectionFastener.FACING_PROP), 0.125F);
		GlStateManager.translate(offset.x, offset.y, offset.z);
		int blockBrightness = fastener.getWorld().getCombinedLight(fastener.getPos(), 0);
		int skylight = blockBrightness % 65536;
		int moonlight = blockBrightness / 65536;
		GlStateManager.enableRescaleNormal();
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
		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1);
	}

	private void renderBow(TileEntityConnectionFastener fastener, Collection<Connection> connections, float delta) {
		if (fastener.getBlockType() instanceof BlockConnectionFastenerFence) {
			return;
		}
		EnumFacing facing = BlockConnectionFastener.getFacingFromMeta(fastener.getBlockMetadata());
		if (facing.getAxis() == Axis.Y) {
			return;
		}
		Vector3f fastenerDir = new Vector3f(facing.getDirectionVec());
		float yaw = (float) -Math.atan2(fastenerDir.z, fastenerDir.x) - MathUtils.HALF_PI;
		GlStateManager.pushMatrix();
		GlStateManager.translate(fastenerDir.x * 1.5F / 16, 0, fastenerDir.z * 1.5F / 16);
		GlStateManager.rotate(yaw * MathUtils.RAD_TO_DEG, 0, 1, 0);
		GlStateManager.translate(-9F / 16, -6F / 16, -0.5F / 16);
		GlStateManager.scale(1, 1, 2.5F);
		render3DTexture(Tessellator.getInstance().getWorldRenderer(), 18, 12, 0, 72);
		GlStateManager.popMatrix();
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

	public static void render3DTexture(WorldRenderer render, int width, int height, int u, int v) {
		float u1 = u / (float) TEXTURE_WIDTH;
		float u2 = (u + width) / (float) TEXTURE_WIDTH;
		float v1 = v / (float) TEXTURE_HEIGHT;
		float v2 = (v + height) / (float) TEXTURE_HEIGHT;
		GL11.glPushMatrix();
		GL11.glScalef(width / 16F, height / 16F, 1);
		Tessellator tessellator = Tessellator.getInstance();
		float depth = 0.0625F;
		render.startDrawingQuads();
		GL11.glNormal3f(0, 0, 1);
		render.addVertexWithUV(0, 0, 0, u1, v2);
		render.addVertexWithUV(1, 0, 0, u2, v2);
		render.addVertexWithUV(1, 1, 0, u2, v1);
		render.addVertexWithUV(0, 1, 0, u1, v1);
		tessellator.draw();
		render.startDrawingQuads();
		GL11.glNormal3f(0, 0, -1);
		render.addVertexWithUV(0, 1, -depth, u1, v1);
		render.addVertexWithUV(1, 1, -depth, u2, v1);
		render.addVertexWithUV(1, 0, -depth, u2, v2);
		render.addVertexWithUV(0, 0, -depth, u1, v2);
		tessellator.draw();
		float widthStretch = 0.5F * (u1 - u2) / width;
		float heightStretch = 0.5F * (v2 - v1) / height;
		render.startDrawingQuads();
		GL11.glNormal3f(-1, 0, 0);
		for (int p = 0; p < width; p++) {
			float x = (float) p / width;
			float ui = u1 + (u2 - u1) * x - widthStretch;
			render.addVertexWithUV(x, 0, -depth, ui, v2);
			render.addVertexWithUV(x, 0, 0, ui, v2);
			render.addVertexWithUV(x, 1, 0, ui, v1);
			render.addVertexWithUV(x, 1, -depth, ui, v1);
		}
		tessellator.draw();
		render.startDrawingQuads();
		GL11.glNormal3f(1, 0, 0);
		for (int p = 0; p < width; p++) {
			float xi = (float) p / width;
			float ui = u1 + (u2 - u1) * xi - widthStretch;
			float x = xi + 1F / width;
			render.addVertexWithUV(x, 1, -depth, ui, v1);
			render.addVertexWithUV(x, 1, 0, ui, v1);
			render.addVertexWithUV(x, 0, 0, ui, v2);
			render.addVertexWithUV(x, 0, -depth, ui, v2);
		}
		tessellator.draw();
		render.startDrawingQuads();
		GL11.glNormal3f(0, 1, 0);
		for (int p = 0; p < height; p++) {
			float yi = (float) p / height;
			float vi = v2 + (v1 - v2) * yi - heightStretch;
			float y = yi + 1.0F / height;
			render.addVertexWithUV(0, y, 0, u1, vi);
			render.addVertexWithUV(1, y, 0, u2, vi);
			render.addVertexWithUV(1, y, -depth, u2, vi);
			render.addVertexWithUV(0, y, -depth, u1, vi);
		}
		tessellator.draw();
		render.startDrawingQuads();
		GL11.glNormal3f(0, -1, 0);
		for (int p = 0; p < height; p++) {
			float y = (float) p / height;
			float vi = v2 + (v1 - v2) * y - heightStretch;
			render.addVertexWithUV(1, y, 0, u2, vi);
			render.addVertexWithUV(0, y, 0, u1, vi);
			render.addVertexWithUV(0, y, -depth, u1, vi);
			render.addVertexWithUV(1, y, -depth, u2, vi);
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}
}
