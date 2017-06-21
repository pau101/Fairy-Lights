package com.pau101.fairylights.client;

import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.FastenerType;
import com.pau101.fairylights.server.fastener.connection.Catenary;
import com.pau101.fairylights.server.fastener.connection.PlayerAction;
import com.pau101.fairylights.server.fastener.connection.Segment;
import com.pau101.fairylights.server.fastener.connection.collision.ConnectionCollision;
import com.pau101.fairylights.server.fastener.connection.collision.Intersection;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.fastener.connection.type.hanginglights.ConnectionHangingLights;
import com.pau101.fairylights.server.jingle.Jingle;
import com.pau101.fairylights.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ClientEventHandler {
	private static boolean optifinePresent = FMLClientHandler.instance().hasOptifine();

	private static final HitConnection HIT_CONNECTION = new HitConnection();

	private static final float HIGHLIGHT_ALPHA = 0.4F;

	@Nullable
	private Vec3d prevCatenaryVec;

	@Nullable
	private net.minecraft.client.renderer.vertex.VertexBuffer connHighlightVBO;

	private int connHighlightId;

	private boolean useVBO;

	public static boolean isShaders() {
		return optifinePresent && Shaders.shaderPackLoaded;
	}

	public static Connection getHitConnection() {
		return HIT_CONNECTION.connection;
	}

	@SubscribeEvent
	public void gatherOverlayText(RenderGameOverlayEvent.Text event) {
		Connection conn = HIT_CONNECTION.connection;
		if (!(conn instanceof ConnectionHangingLights)) {
			return;
		}
		Jingle jingle = ((ConnectionHangingLights) conn).getPlayingJingle();
		if (jingle != null) {
			List<String> lines = event.getRight();
			if (lines.size() > 0) {
				lines.add("");	
			}
			lines.add("Song: " + jingle.getName());
			lines.add("Artist: "+ jingle.getArtist());
		}
	}

	@SubscribeEvent
	public void renderWorldEarly(EntityViewRenderEvent.FogColors event) {
		if (HIT_CONNECTION.connection != null) {
			Minecraft mc = Minecraft.getMinecraft();
			HIT_CONNECTION.setWorld(mc.world);
			mc.objectMouseOver = new RayTraceResult(HIT_CONNECTION);
		}
	}

	public static void updateHitConnection() {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewer = mc.getRenderViewEntity();
		HIT_CONNECTION.setWorld(null);
		if (mc.objectMouseOver != null && mc.world != null && viewer != null) {
			HitResult result = getHitConnection(mc.world, viewer);
			if (result != null) {
				Vec3d eyes = viewer.getPositionEyes(1);
				if (result.intersection.getResult().hitVec.distanceTo(eyes) < mc.objectMouseOver.hitVec.distanceTo(eyes)) {
					HIT_CONNECTION.connection = result.connection;
					HIT_CONNECTION.intersection= result.intersection;
					HIT_CONNECTION.setWorld(mc.world);
					mc.objectMouseOver = new RayTraceResult(HIT_CONNECTION);
					return;
				}
			}
		}
		HIT_CONNECTION.connection = null;
		HIT_CONNECTION.intersection = null;
	}

	@Nullable
	private static HitResult getHitConnection(World world, Entity viewer) {
		AxisAlignedBB bounds = new AxisAlignedBB(viewer.getPosition()).grow(Connection.MAX_LENGTH + 1);
		List<Fastener<?>> fasteners = collectFasteners(world, bounds);
		return getHitConnection(viewer, bounds, fasteners);
	}

	private static List<Fastener<?>> collectFasteners(World world, AxisAlignedBB bounds) {
		List<Fastener<?>> fasteners = world.getEntitiesWithinAABB(EntityFenceFastener.class, bounds)
			.stream()
			.map(e -> e.<Fastener<?>>getCapability(CapabilityHandler.FASTENER_CAP, null))
			.collect(Collectors.toCollection(ArrayList::new)
		);
        int minX = MathHelper.floor((bounds.minX - 1) / 16D);
        int maxX = MathHelper.ceil((bounds.maxX + 1) / 16D);
        int minZ = MathHelper.floor((bounds.minZ - 1) / 16D);
        int maxZ = MathHelper.ceil((bounds.maxZ + 1) / 16D);
        IChunkProvider provider = world.getChunkProvider();
        for (int x = minX; x < maxX; x++) {
        	for (int z = minZ; z < maxZ; z++) {
        		// Since this is the client we use provideChunk
        		Chunk chunk = provider.provideChunk(x, z);
        		if (chunk.isEmpty()) {
        			continue;
        		}
        		Map<BlockPos, TileEntity> blockEntities = chunk.getTileEntityMap();
        		// In case it is fixed
        		if (blockEntities instanceof ConcurrentHashMap) {
        			collectFasteners(bounds, fasteners, blockEntities);
        		} else {
        			try {
        				collectFasteners(bounds, fasteners, blockEntities);
        			} catch (ConcurrentModificationException e) {
        				/*
        				 * Oh noes!.. I would guess a ChunkRenderWorker discovered
        				 * an invalid block entity and graciously removed it, or
        				 * something modded did.
        				 */
        			}
        		}
        	}
        }
        return fasteners;
	}

	private static void collectFasteners(AxisAlignedBB bounds, List<Fastener<?>> fasteners, Map<BlockPos, TileEntity> blockEntities) {
		for (Entry<BlockPos, TileEntity> entry : blockEntities.entrySet()) {
			Vec3d vec = new Vec3d(entry.getKey()).addVector(0.5, 0.5, 0.5);
			if (!bounds.contains(vec)) {
				continue;
			}
			TileEntity blockEntity = entry.getValue();
			if (blockEntity.hasCapability(CapabilityHandler.FASTENER_CAP, null)) {
				fasteners.add(blockEntity.getCapability(CapabilityHandler.FASTENER_CAP, null));
			}
		}
	}

	@Nullable
	private static HitResult getHitConnection(Entity viewer, AxisAlignedBB bounds, List<Fastener<?>> fasteners) {
		if (fasteners.isEmpty()) {
			return null;
		}
		Vec3d origin = viewer.getPositionEyes(1);
		Vec3d look = viewer.getLook(1);
		double reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d end = origin.addVector(look.x * reach, look.y * reach, look.z * reach);
		Connection found = null;
		Intersection rayTrace = null;
		double distance = Double.MAX_VALUE;
		for (Fastener<?> fastener : fasteners) {
			if (!bounds.intersects(fastener.getBounds())) {
				continue;
			}
			for (Connection connection : fastener.getConnections().values()) {
				if (!connection.isOrigin()) {
					continue;
				}
				if (connection.getDestination().getType() == FastenerType.PLAYER) {
					continue;
				}
				ConnectionCollision collision = connection.getCollision();
				Intersection result = collision.intersect(origin, end);
				if (result != null) {
					double dist = result.getResult().hitVec.distanceTo(origin);
					if (dist < distance) {
						distance = dist;
						found = connection;
						rayTrace = result;
					}
				}
			}
		}
		if (found == null) {
			return null;
		}
		return new HitResult(found, rayTrace);
	}

	@SubscribeEvent
	public void drawBlockHighlight(DrawBlockHighlightEvent event) {
		RayTraceResult over = event.getTarget();
		boolean isFence = over.entityHit instanceof EntityFenceFastener;
		boolean isHitConnection = over.entityHit == HIT_CONNECTION;
		if (isFence || isHitConnection) {
			EntityPlayer player = event.getPlayer();
			float delta = event.getPartialTicks();
			double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * delta;
			double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * delta;
			double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * delta;
			setupHighlightGL();
			if (isFence) {
				drawFenceFastenerHighlight(player, (EntityFenceFastener) over.entityHit, delta, dx, dy, dz);
			} else {
				if (HIT_CONNECTION.intersection.getFeatureType() == Connection.CORD_FEATURE) {
					drawConnectionHighlight(HIT_CONNECTION.connection, delta, dx, dy, dz);
				} else {
					AxisAlignedBB aabb = HIT_CONNECTION.intersection.getHitBox().offset(-dx, -dy, -dz).grow(0.002);
					RenderGlobal.drawSelectionBoundingBox(aabb, 0, 0, 0, HIGHLIGHT_ALPHA);
				}
			}
			restoreHighlightGL();
		}
	}

	private void drawFenceFastenerHighlight(EntityPlayer player, EntityFenceFastener fence, float delta, double dx, double dy, double dz) {
		// Check if the server will allow interaction
		if (player.canEntityBeSeen(fence) || player.getDistanceSqToEntity(fence) <= 9) {
			AxisAlignedBB selection = fence.getEntityBoundingBox().offset(-dx, -dy, -dz).grow(0.002);
			RenderGlobal.drawSelectionBoundingBox(selection, 0, 0, 0, HIGHLIGHT_ALPHA);
		}
	}

	private void drawConnectionHighlight(Connection connection, float delta, double dx, double dy, double dz) {
		Catenary catenary = connection.getCatenary();
		if (catenary != null) {
			Vec3d vec = catenary.getVector();
			boolean update = useVBO != OpenGlHelper.useVbo();
			if (update) {
				useVBO = OpenGlHelper.useVbo();
			}
			if (prevCatenaryVec != vec || update) {
				generateHighlight(connection);
				prevCatenaryVec = vec;
			}
			GlStateManager.pushMatrix();
			Vec3d offset = connection.getFastener().getConnectionPoint();
			GlStateManager.translate(offset.x - dx, offset.y - dy, offset.z - dz);
			if (useVBO) {
				connHighlightVBO.bindBuffer();
				GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
				GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
				GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 16, 0);
				GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
				connHighlightVBO.drawArrays(GL11.GL_LINE_STRIP);
	            connHighlightVBO.unbindBuffer();
				GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
			} else {
				GlStateManager.callList(connHighlightId);
			}
			GlStateManager.popMatrix();
		}
	}

	private void setupHighlightGL() {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		GlStateManager.glLineWidth(2);
		GlStateManager.disableTexture2D();
		if (isShaders()) {
			// Gotta get that gbuffers_basic active to not get grainy color artifacts 
			Shaders.disableTexture2D();
		}
		GlStateManager.depthMask(false);
	}

	private void restoreHighlightGL() {
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		if (isShaders()) {
			Shaders.enableTexture2D();
		}
		GlStateManager.depthMask(true);
	}

	private void generateHighlight(Connection connection) {
		if (connHighlightVBO != null) {
			connHighlightVBO.deleteGlBuffers();
		}
		if (connHighlightId >= 0) {
			GLAllocation.deleteDisplayLists(connHighlightId);
			connHighlightId = 0;
		}
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
		if (OpenGlHelper.useVbo()) {
			connHighlightVBO = new net.minecraft.client.renderer.vertex.VertexBuffer(DefaultVertexFormats.POSITION_COLOR);
			renderHighlight(connection, buf);
			buf.finishDrawing();
			buf.reset();
			connHighlightVBO.bufferData(buf.getByteBuffer());
		} else {
			connHighlightId = GLAllocation.generateDisplayLists(1);
			GlStateManager.pushMatrix();
			GlStateManager.glNewList(connHighlightId, GL11.GL_COMPILE);
			renderHighlight(connection, buf);
			tessellator.draw();
			GlStateManager.glEndList();
			GlStateManager.popMatrix();
		}
	}

	private void renderHighlight(Connection connection, BufferBuilder buf) {
		buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		Segment[] segments = connection.getCatenary().getSegments();
		float cordRadius = connection.getRadius();
		for (int edge = 0; edge < 4; edge++) {
			int start, end, step;
			boolean forward = edge % 2 == 0;
			if (forward) {
				start = 0;
				end = segments.length;
				step = 1;
				Segment first = segments[0];
				Vec3d dir = first.getVector();
				renderVertex(buf, edge, forward, first.getStart(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
			} else {
				start = segments.length - 1;
				end = -1;
				step = -1;
				Segment last = segments[start];
				Vec3d dir = last.getVector();
				renderVertex(buf, edge, forward, last.getEnd(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
			}
			for (int i = start; i != end; i += step) {
				Segment segment = segments[i];
				Segment next = i + step >= segments.length || i + step < 0 ? segment : segments[i + step];
				Vec3d dir = segment.getVector();
				Vec3d pos = forward ? segment.getEnd() : segment.getStart();
				renderVertex(buf, edge, forward, pos, dir, next.getVector(), cordRadius, 0.4F);
			}
			if (edge == 0) {
				Segment last = segments[segments.length - 1];
				Vec3d dir = last.getVector();
				Vec3d endPoint = last.getEnd();
				renderVertex(buf, 0, false, endPoint, dir, dir, cordRadius, 0);
				renderVertex(buf, 2, false, endPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
				renderVertex(buf, 2, true, endPoint, dir, dir, cordRadius, 0);
				renderVertex(buf, 0, true, endPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
			} else if (edge == 1) {
				Segment first = segments[0];
				Vec3d dir = first.getVector();
				renderVertex(buf, 2, forward, first.getStart(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
			}
		}
		Segment first = segments[0];
		Vec3d dir = first.getVector();
		Vec3d startPoint = first.getStart();
		renderVertex(buf, 0, true, startPoint, dir, dir, cordRadius, 0);
		renderVertex(buf, 2, true, startPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
		renderVertex(buf, 0, false, startPoint, dir, dir, cordRadius, 0);
		renderVertex(buf, 0, true, startPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
	}

	private void renderVertex(BufferBuilder buf, int edge, boolean forward, Vec3d pos, Vec3d dir, Vec3d toDir, float cordRadius, float alpha) {
		if (forward) {
			toDir = Mth.negate(toDir);
		} else {
			dir = Mth.negate(dir);
		}
		Vec3d up;
		boolean colinear = dir.dotProduct(toDir) < -1 + 1e-6;
		if (colinear) {
			double h = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
			if (h < 1e-6) {
				up = new Vec3d(-1, 0, 0);
			} else {
				up = new Vec3d(dir.x / h * -dir.y, h, dir.z / h * -dir.y).normalize();
			}
		} else {
			up = Mth.lerp(dir, toDir, 0.5F).normalize();
		}
		Vec3d side = dir.crossProduct(up).normalize();
		if (edge < 2) {
			up = Mth.negate(up);
		}
		pos = pos.scale(0.0625F).add(up.scale(cordRadius + 0.01F).add(side.scale(cordRadius + 0.01F)));
		buf.pos(pos.x, pos.y, pos.z).color(0, 0, 0, alpha).endVertex();
	}

	private static class HitConnection extends Entity {
		@Nullable
		private Connection connection;

		@Nullable
		private Intersection intersection;

		public HitConnection() {
			super(null);
			setEntityId(-1);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			processAction(PlayerAction.ATTACK);
			return false;
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			if (hand == EnumHand.MAIN_HAND) {
				processAction(PlayerAction.INTERACT);
			}
			return false;
		}

		private void processAction(PlayerAction action) {
			connection.processClientAction(Minecraft.getMinecraft().player, action, intersection);
		}

		@Override
		public ItemStack getPickedResult(RayTraceResult target) {
			return connection.getItemStack();
		}

		@Override
		protected void entityInit() {}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {}
	}

	private static final class HitResult {
		private final Connection connection;

		private final Intersection intersection;

		public HitResult(Connection connection, Intersection intersection) {
			this.connection = connection;
			this.intersection = intersection;
		}
	}
}
