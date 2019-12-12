package me.paulf.fairylights.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.model.connection.ModelConnection;
import me.paulf.fairylights.client.model.connection.ModelConnectionGarland;
import me.paulf.fairylights.client.model.connection.ModelConnectionHangingLights;
import me.paulf.fairylights.client.model.connection.ModelConnectionLetterBunting;
import me.paulf.fairylights.client.model.connection.ModelConnectionPennantBunting;
import me.paulf.fairylights.client.model.connection.ModelConnectionTinsel;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class FastenerRenderer {
	private FastenerRenderer() {}

	public static final ResourceLocation TEXTURE = new ResourceLocation(FairyLights.ID, "textures/entity/connections.png");

	public static final int TEXTURE_WIDTH = 128;

	public static final int TEXTURE_HEIGHT = 128;

	private static final ImmutableMap<ConnectionType, ModelConnection> MODELS = new ImmutableMap.Builder<ConnectionType, ModelConnection>()
		.put(ConnectionType.HANGING_LIGHTS, new ModelConnectionHangingLights())
		.put(ConnectionType.GARLAND, new ModelConnectionGarland())
		.put(ConnectionType.TINSEL, new ModelConnectionTinsel())
		.put(ConnectionType.PENNANT_BUNTING, new ModelConnectionPennantBunting())
		.put(ConnectionType.LETTER_BUNTING, new ModelConnectionLetterBunting())
		.build();

	public static void render(Fastener<?> fastener, float delta) {
		World world = fastener.getWorld();
		List<Connection> connections = removeUnnecessaryConnections(fastener.getConnections().values(), world);
		GlStateManager.enableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.pushMatrix();
		if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox() && !Minecraft.getInstance().isReducedDebug()) {
			renderBoundingBox(fastener);
		}
		int blockBrightness = fastener.getWorld().getCombinedLight(fastener.getPos(), 0);
		int skylight = blockBrightness % 0x10000;
		int moonlight = blockBrightness / 0x10000;
		boolean shouldRenderBow = true;
		class TexturedRender {
			Connection connection;

			ModelConnection renderer;

			TexturedRender(Connection connection, ModelConnection renderer) {
				this.connection = connection;
				this.renderer = renderer;
			}
		}
		List<TexturedRender> texturedRenders = new ArrayList<>();
		for (Connection connection : connections) {
			if (connection.isOrigin()) {
				ModelConnection renderer = MODELS.get(connection.getType());
				renderer.render(fastener, connection, world, skylight, moonlight, delta);
				if (renderer.hasTexturedRender()) {
					texturedRenders.add(new TexturedRender(connection, renderer));
				}
			}
			if (connection.getType() == ConnectionType.GARLAND && shouldRenderBow) {
				renderBow(fastener, connections, delta);
				shouldRenderBow = false;
			}
		}
		TextureManager texturer = Minecraft.getInstance().getTextureManager();
		for (TexturedRender render : texturedRenders) {
			ResourceLocation tex = render.renderer.getAlternateTexture();
			if (tex != null) {
				texturer.bindTexture(tex);
			}
			render.renderer.renderTexturePass(fastener, render.connection, world, skylight, moonlight, delta);
		}
		GlStateManager.popMatrix();
		GlStateManager.color3f(1, 1, 1);
	}

	private static void renderBoundingBox(Fastener<?> fastener) {
		GlStateManager.disableLighting();
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		GlStateManager.lineWidth(2);
		Vec3d offset = fastener.getConnectionPoint();
		WorldRenderer.drawSelectionBoundingBox(fastener.getBounds().offset(-offset.x, -offset.y, -offset.z), 1, 1, 1, 1);
		/*/
		offset = Mth.negate(offset).subtract(fastener.getOffsetPoint());
		for (Connection connection : fastener.getConnections().values()) {
			if (connection.isOrigin() && connection.getCollision().tree != null) {
				renderCollision(offset, connection.getCollision().tree, 0);
			}
		}//*/
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	/*/
	private static void renderCollision(Vec3d offset, AxisAlignedBB[] tree, int node) {
		AxisAlignedBB bounds = tree[node];
		if (bounds != null) {
			RenderGlobal.func_189697_a(bounds.offset(offset.x, offset.y, offset.z), 1, 1, 1, 1);
			if (node * 2 + 1 < tree.length && tree[node * 2 + 1] != null) {
				renderCollision(offset, tree, node * 2 + 1);
				renderCollision(offset, tree, node * 2 + 2);
			}
		}
	}//*/

	private static void renderBow(Fastener fastener, Collection<Connection> connections, float delta) {
		Direction facing = fastener.getFacing();
		if (facing.getAxis() == Direction.Axis.Y) {
			return;
		}
		Vec3d fastenerDir = new Vec3d(facing.getDirectionVec());
		float yaw = (float) -MathHelper.atan2(fastenerDir.z, fastenerDir.x) - Mth.HALF_PI;
		GlStateManager.pushMatrix();
		GlStateManager.translated(fastenerDir.x * 1.5F / 16, 0, fastenerDir.z * 1.5F / 16);
		GlStateManager.rotatef(yaw * Mth.RAD_TO_DEG, 0, 1, 0);
		GlStateManager.translatef(-9F / 16, -6F / 16, -0.5F / 16);
		GlStateManager.scalef(1, 1, 2.5F);
		render3DTexture(18, 12, 0, 72);
		GlStateManager.popMatrix();
	}

	private static List<Connection> removeUnnecessaryConnections(Collection<Connection> connections, World world) {
		List<Connection> visibleConnections = new ArrayList<>();
		Iterator<Connection> connectionIterator = connections.iterator();
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			if (!connection.getDestination().isLoaded(world)) {
				continue;
			}
			visibleConnections.add(connection);
		}
		return visibleConnections;
	}

	public static void render3DTexture(int width, int height, int u, int v) {
		render3DTexture(width, height, u, v, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

	public static void render3DTexture(int width, int height, int u, int v, int texWidth, int texHeight) {
		float u1 = u / (float) texWidth;
		float u2 = (u + width) / (float) texWidth;
		float v1 = v / (float) texHeight;
		float v2 = (v + height) / (float) texHeight;
		GlStateManager.pushMatrix();
		GlStateManager.scalef(width / 16F, height / 16F, 1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder render = tessellator.getBuffer();
		float depth = 0.0625F;
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(0, 0, 1);
		render.pos(0, 0, 0).tex(u1, v2).endVertex();
		render.pos(1, 0, 0).tex(u2, v2).endVertex();
		render.pos(1, 1, 0).tex(u2, v1).endVertex();
		render.pos(0, 1, 0).tex(u1, v1).endVertex();
		tessellator.draw();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(0, 0, -1);
		render.pos(0, 1, -depth).tex(u1, v1).endVertex();
		render.pos(1, 1, -depth).tex(u2, v1).endVertex();
		render.pos(1, 0, -depth).tex(u2, v2).endVertex();
		render.pos(0, 0, -depth).tex(u1, v2).endVertex();
		tessellator.draw();
		float widthStretch = 0.5F * (u1 - u2) / width;
		float heightStretch = 0.5F * (v2 - v1) / height;
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(1, 0, 0);
		for (int p = 0; p < width; p++) {
			float x = (float) p / width;
			float ui = u1 + (u2 - u1) * x - widthStretch;
			render.pos(x, 0, -depth).tex(ui, v2).endVertex();
			render.pos(x, 0, 0).tex(ui, v2).endVertex();
			render.pos(x, 1, 0).tex(ui, v1).endVertex();
			render.pos(x, 1, -depth).tex(ui, v1).endVertex();
		}
		tessellator.draw();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(-1, 0, 0);
		for (int p = 0; p < width; p++) {
			float xi = (float) p / width;
			float ui = u1 + (u2 - u1) * xi - widthStretch;
			float x = xi + 1F / width;
			render.pos(x, 1, -depth).tex(ui, v1).endVertex();
			render.pos(x, 1, 0).tex(ui, v1).endVertex();
			render.pos(x, 0, 0).tex(ui, v2).endVertex();
			render.pos(x, 0, -depth).tex(ui, v2).endVertex();
		}
		tessellator.draw();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(0, 1, 0);
		for (int p = 0; p < height; p++) {
			float yi = (float) p / height;
			float vi = v2 + (v1 - v2) * yi - heightStretch;
			float y = yi + 1.0F / height;
			render.pos(0, y, 0).tex(u1, vi).endVertex();
			render.pos(1, y, 0).tex(u2, vi).endVertex();
			render.pos(1, y, -depth).tex(u2, vi).endVertex();
			render.pos(0, y, -depth).tex(u1, vi).endVertex();
		}
		tessellator.draw();
		render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.normal3f(0, -1, 0);
		for (int p = 0; p < height; p++) {
			float y = (float) p / height;
			float vi = v2 + (v1 - v2) * y - heightStretch;
			render.pos(1, y, 0).tex(u2, vi).endVertex();
			render.pos(0, y, 0).tex(u1, vi).endVertex();
			render.pos(0, y, -depth).tex(u1, vi).endVertex();
			render.pos(1, y, -depth).tex(u2, vi).endVertex();
		}
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
