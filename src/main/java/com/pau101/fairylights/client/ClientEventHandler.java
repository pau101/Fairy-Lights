package com.pau101.fairylights.client;

import com.google.common.collect.Sets;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.CollectFastenersEvent;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public final class ClientEventHandler {

	private static final float HIGHLIGHT_ALPHA = 0.4F;

	@Nullable
	private static HitConnection hit;

	@Nullable
	private Vec3d prevCatenaryVec;

	@Nullable
	private net.minecraft.client.renderer.vertex.VertexBuffer connHighlightVBO;

	private int connHighlightId;

	private boolean useVBO;

	@Nullable
	public static Connection getHitConnection() {
		return hit == null || hit.result == null ? null : hit.result.connection;
	}

	@SubscribeEvent
	public void onClientTick(final TickEvent.ClientTickEvent event) {
		final World world = Minecraft.getMinecraft().world;
		if (event.phase != TickEvent.Phase.START && world != null) {
			world.loadedTileEntityList.stream()
				.filter(BlockEntityFastener.class::isInstance)
				.map(BlockEntityFastener.class::cast)
				.forEach(f -> {
					f.getCapability(CapabilityHandler.FASTENER_CAP, null).update();
				});
		}
	}

	@SubscribeEvent
	public void gatherOverlayText(RenderGameOverlayEvent.Text event) {
		if (hit == null || hit.result == null) {
			return;
		}
		Connection conn = hit.result.connection;
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
		if (hit != null && hit.result != null) {
			Minecraft mc = Minecraft.getMinecraft();
			hit.setWorld(mc.world);
			mc.objectMouseOver = new RayTraceResult(hit);
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (hit != null && hit.world == event.getWorld()) {
			hit = null;
		}
	}

	public static void updateHitConnection() {
		Minecraft mc = Minecraft.getMinecraft();
		Entity viewer = mc.getRenderViewEntity();
		if (mc.objectMouseOver != null && mc.world != null && viewer != null) {
			HitResult result = getHitConnection(mc.world, viewer);
			if (result != null) {
				Vec3d eyes = viewer.getPositionEyes(1);
				if (result.intersection.getResult().hitVec.distanceTo(eyes) < mc.objectMouseOver.hitVec.distanceTo(eyes)) {
					if (hit == null) {
						hit = new HitConnection(mc.world);
					}
					hit.result = result;
					hit.setWorld(mc.world);
					mc.objectMouseOver = new RayTraceResult(hit);
					return;
				}
			}
		}
		if (hit != null) {
			hit.result = null;
		}
	}

	@Nullable
	private static HitResult getHitConnection(World world, Entity viewer) {
		AxisAlignedBB bounds = new AxisAlignedBB(viewer.getPosition()).grow(Connection.MAX_LENGTH + 1);
		Set<Fastener<?>> fasteners = collectFasteners(world, bounds);
		return getHitConnection(viewer, bounds, fasteners);
	}

	private static Set<Fastener<?>> collectFasteners(final World world, final AxisAlignedBB bounds) {
		final Set<Fastener<?>> fasteners = Sets.newLinkedHashSet();
		final CollectFastenersEvent event = new CollectFastenersEvent(world, bounds, fasteners);
		world.getEntitiesWithinAABB(EntityFenceFastener.class, bounds)
			.forEach(event::accept);
        final int minX = MathHelper.floor(bounds.minX / 16.0D);
		final int maxX = MathHelper.ceil(bounds.maxX / 16.0D);
		final int minZ = MathHelper.floor(bounds.minZ / 16.0D);
		final int maxZ = MathHelper.ceil(bounds.maxZ / 16.0D);
        final IChunkProvider provider = world.getChunkProvider();
        for (int x = minX; x < maxX; x++) {
        	for (int z = minZ; z < maxZ; z++) {
				final Chunk chunk = provider.getLoadedChunk(x, z);
				if (chunk != null) {
					event.accept(chunk);
				}
        	}
        }
		MinecraftForge.EVENT_BUS.post(event);
        return fasteners;
	}

	@Nullable
	private static HitResult getHitConnection(Entity viewer, AxisAlignedBB bounds, Set<Fastener<?>> fasteners) {
		if (fasteners.isEmpty()) {
			return null;
		}
		Vec3d origin = viewer.getPositionEyes(1);
		Vec3d look = viewer.getLook(1);
		double reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d end = origin.add(look.x * reach, look.y * reach, look.z * reach);
		Connection found = null;
		Intersection rayTrace = null;
		double distance = Double.MAX_VALUE;
		for (Fastener<?> fastener : fasteners) {
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
		boolean isHitConnection = over.entityHit == hit;
		if (isFence || isHitConnection) {
			EntityPlayer player = event.getPlayer();
			float delta = event.getPartialTicks();
			double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * delta;
			double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * delta;
			double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * delta;
			setupHighlightGL();
			if (isFence) {
				drawFenceFastenerHighlight(player, (EntityFenceFastener) over.entityHit, delta, dx, dy, dz);
			} else if (hit != null && hit.result != null) {
				if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
					drawConnectionHighlight(hit.result.connection, delta, dx, dy, dz);
				} else {
					AxisAlignedBB aabb = hit.result.intersection.getHitBox().offset(-dx, -dy, -dz).grow(0.002);
					RenderGlobal.drawSelectionBoundingBox(aabb, 0, 0, 0, HIGHLIGHT_ALPHA);
				}
			}
			restoreHighlightGL();
		}
	}

	private void drawFenceFastenerHighlight(EntityPlayer player, EntityFenceFastener fence, float delta, double dx, double dy, double dz) {
		// Check if the server will allow interaction
		if (player.canEntityBeSeen(fence) || player.getDistanceSq(fence) <= 9) {
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
		GlStateManager.depthMask(false);
	}

	private void restoreHighlightGL() {
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
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
		private HitResult result;

		private HitConnection(World world) {
			super(world);
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
			if (result != null) {
				result.connection.processClientAction(Minecraft.getMinecraft().player, action, result.intersection);
			}
		}

		@Override
		public ItemStack getPickedResult(RayTraceResult target) {
			if (result == null) {
				return ItemStack.EMPTY;
			}
			return result.connection.getItemStack();
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
