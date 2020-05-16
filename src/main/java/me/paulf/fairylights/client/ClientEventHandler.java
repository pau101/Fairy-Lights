package me.paulf.fairylights.client;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.CollectFastenersEvent;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.PlayerAction;
import me.paulf.fairylights.server.fastener.connection.collision.ConnectionCollision;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
        final Minecraft mc = Minecraft.getInstance();
        final World world = mc.world;
        if (event.phase != TickEvent.Phase.START && world != null && !mc.isGamePaused()) {
			this.getBlockEntities(world).stream()
                .filter(FastenerBlockEntity.class::isInstance)
                .map(FastenerBlockEntity.class::cast)
                .flatMap(f -> f.getCapability(CapabilityHandler.FASTENER_CAP).map(Stream::of).orElse(Stream.empty()))
                .forEach(Fastener::update);
        }
    }

    private Collection<TileEntity> getBlockEntities(final World world) {
        try {
            return new ArrayList<>(world.loadedTileEntityList);
        } catch (final ConcurrentModificationException e) {
            // RenderChunk's may find an invalid block entity while building and trigger a remove not on main thread
            return Collections.emptyList();
        }
    }

    @SubscribeEvent
    public void gatherOverlayText(final RenderGameOverlayEvent.Text event) {
        if (hit == null || hit.result == null) {
            return;
        }
        final Connection conn = hit.result.connection;
        if (!(conn instanceof HangingLightsConnection)) {
            return;
        }
        final Jingle jingle = ((HangingLightsConnection) conn).getPlayingJingle();
        if (jingle != null) {
            final List<String> lines = event.getRight();
            if (lines.size() > 0) {
                lines.add("");
            }
            lines.add("Song: " + jingle.getName());
            lines.add("Artist: " + jingle.getArtist());
        }
    }

    @SubscribeEvent
    public void renderWorldEarly(final EntityViewRenderEvent.FogColors event) {
        if (hit != null && hit.result != null) {
            final Minecraft mc = Minecraft.getInstance();
            hit.setWorld(mc.world);
            mc.objectMouseOver = new EntityRayTraceResult(hit);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        if (hit != null && hit.world == event.getWorld()) {
            hit = null;
        }
    }

    public static void updateHitConnection() {
        final Minecraft mc = Minecraft.getInstance();
        final Entity viewer = mc.getRenderViewEntity();
        if (mc.objectMouseOver != null && mc.world != null && viewer != null) {
            final HitResult result = getHitConnection(mc.world, viewer);
            if (result != null) {
                final Vec3d eyes = viewer.getEyePosition(1);
                if (result.intersection.getResult().distanceTo(eyes) < mc.objectMouseOver.getHitVec().distanceTo(eyes)) {
                    if (hit == null) {
                        hit = new HitConnection(mc.world);
                    }
                    hit.result = result;
                    hit.setWorld(mc.world);
                    mc.objectMouseOver = new EntityRayTraceResult(hit);
                    return;
                }
            }
        }
        if (hit != null) {
            hit.result = null;
        }
    }

    @Nullable
    private static HitResult getHitConnection(final World world, final Entity viewer) {
        final AxisAlignedBB bounds = new AxisAlignedBB(viewer.getPosition()).grow(Connection.MAX_LENGTH + 1);
        final Set<Fastener<?>> fasteners = collectFasteners(world, bounds);
        return getHitConnection(viewer, bounds, fasteners);
    }

    private static Set<Fastener<?>> collectFasteners(final World world, final AxisAlignedBB bounds) {
        final Set<Fastener<?>> fasteners = Sets.newLinkedHashSet();
        final CollectFastenersEvent event = new CollectFastenersEvent(world, bounds, fasteners);
        world.getEntitiesWithinAABB(FenceFastenerEntity.class, bounds)
            .forEach(event::accept);
        final int minX = MathHelper.floor(bounds.minX / 16.0D);
        final int maxX = MathHelper.ceil(bounds.maxX / 16.0D);
        final int minZ = MathHelper.floor(bounds.minZ / 16.0D);
        final int maxZ = MathHelper.ceil(bounds.maxZ / 16.0D);
        final AbstractChunkProvider provider = world.getChunkProvider();
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                final Chunk chunk = provider.getChunk(x, z, false);
                if (chunk != null) {
                    event.accept(chunk);
                }
            }
        }
        MinecraftForge.EVENT_BUS.post(event);
        return fasteners;
    }

    @Nullable
    private static HitResult getHitConnection(final Entity viewer, final AxisAlignedBB bounds, final Set<Fastener<?>> fasteners) {
        if (fasteners.isEmpty()) {
            return null;
        }
        final Vec3d origin = viewer.getEyePosition(1);
        final Vec3d look = viewer.getLook(1);
        final double reach = Minecraft.getInstance().playerController.getBlockReachDistance();
        final Vec3d end = origin.add(look.x * reach, look.y * reach, look.z * reach);
        Connection found = null;
        Intersection rayTrace = null;
        double distance = Double.MAX_VALUE;
        for (final Fastener<?> fastener : fasteners) {
            for (final Connection connection : fastener.getConnections().values()) {
                if (!connection.isOrigin()) {
                    continue;
                }
                if (connection.getDestination().getType() == FastenerType.PLAYER) {
                    continue;
                }
                final ConnectionCollision collision = connection.getCollision();
                final Intersection result = collision.intersect(origin, end);
                if (result != null) {
                    final double dist = result.getResult().distanceTo(origin);
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
    public void drawBlockHighlight(final DrawHighlightEvent event) {
        final RayTraceResult over = event.getTarget();
        final boolean isFence = over instanceof EntityRayTraceResult && ((EntityRayTraceResult) over).getEntity() instanceof FenceFastenerEntity;
        final boolean isHitConnection = over instanceof EntityRayTraceResult && ((EntityRayTraceResult) over).getEntity() == hit;
        if (isFence || isHitConnection) {
            final PlayerEntity player = Minecraft.getInstance().player;
            final float delta = event.getPartialTicks();
            final Vec3d pos = event.getInfo().getProjectedView();
            final double dx = pos.x;
            final double dy = pos.y;
            final double dz = pos.z;
			this.setupHighlightGL();
            if (isFence) {
				this.drawFenceFastenerHighlight(player, (FenceFastenerEntity) ((EntityRayTraceResult) over).getEntity(), delta, dx, dy, dz);
            } else if (hit != null && hit.result != null) {
                if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
					this.drawConnectionHighlight(hit.result.connection, delta, dx, dy, dz);
                } else {
                    final AxisAlignedBB aabb = hit.result.intersection.getHitBox().offset(-dx, -dy, -dz).grow(0.002);
                    final IRenderTypeBuffer.Impl buf = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                    WorldRenderer.drawBoundingBox(new MatrixStack(), buf.getBuffer(RenderType.getLines()), aabb, 0, 0, 0, HIGHLIGHT_ALPHA);
                    //buf.draw();
                }
            }
			this.restoreHighlightGL();
        }
    }

    private void drawFenceFastenerHighlight(final PlayerEntity player, final FenceFastenerEntity fence, final float delta, final double dx, final double dy, final double dz) {
        // Check if the server will allow interaction
        if (player.canEntityBeSeen(fence) || player.getDistanceSq(fence) <= 9) {
            final AxisAlignedBB selection = fence.getBoundingBox().offset(-dx, -dy, -dz).grow(0.002);
            //WorldRenderer.drawSelectionBoundingBox(selection, 0, 0, 0, HIGHLIGHT_ALPHA); TODO
        }
    }

    private void drawConnectionHighlight(final Connection connection, final float delta, final double dx, final double dy, final double dz) {
        final Catenary catenary = connection.getCatenary();
        if (catenary != null) {
            /*final Vec3d vec = catenary.getVector(); TODO
            final boolean update = this.useVBO != GLX.useVbo();
            if (update) {
				this.useVBO = GLX.useVbo();
            }
            if (this.prevCatenaryVec != vec || update) {
				this.generateHighlight(connection);
				this.prevCatenaryVec = vec;
            }
            GlStateManager.pushMatrix();
            final Vec3d offset = connection.getFastener().getConnectionPoint();
            GlStateManager.translated(offset.x - dx, offset.y - dy, offset.z - dz);
            if (this.useVBO) {
				this.connHighlightVBO.bindBuffer();
                GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
                GlStateManager.enableClientState(GL11.GL_COLOR_ARRAY);
                GlStateManager.vertexPointer(3, GL11.GL_FLOAT, 16, 0);
                GlStateManager.colorPointer(4, GL11.GL_UNSIGNED_BYTE, 16, 12);
				this.connHighlightVBO.drawArrays(GL11.GL_LINE_STRIP);
				VertexBuffer.unbindBuffer();
                GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
                GlStateManager.disableClientState(GL11.GL_COLOR_ARRAY);
            } else {
                GlStateManager.callList(this.connHighlightId);
            }
            GlStateManager.popMatrix();*/
        }
    }

    private void setupHighlightGL() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.lineWidth(2);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
    }

    private void restoreHighlightGL() {
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
    }

    private void generateHighlight(final Connection connection) {
        /*if (this.connHighlightVBO != null) { TODO
			this.connHighlightVBO.deleteGlBuffers();
        }
        if (this.connHighlightId >= 0) {
            GLAllocation.deleteDisplayLists(this.connHighlightId);
            this.connHighlightId = 0;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buf = tessellator.getBuffer();
        if (GLX.useVbo()) {
            this.connHighlightVBO = new net.minecraft.client.renderer.vertex.VertexBuffer(DefaultVertexFormats.POSITION_COLOR);
            this.renderHighlight(connection, buf);
            buf.finishDrawing();
            buf.reset();
            this.connHighlightVBO.bufferData(buf.getByteBuffer());
        } else {
            this.connHighlightId = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.newList(this.connHighlightId, GL11.GL_COMPILE);
            this.renderHighlight(connection, buf);
            tessellator.draw();
            GlStateManager.endList();
            GlStateManager.popMatrix();
        }*/
    }

    /*private void renderHighlight(final Connection connection, final BufferBuilder buf) {
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        final Segment[] segments = connection.getCatenary().getSegments();
        final float cordRadius = connection.getRadius();
        for (int edge = 0; edge < 4; edge++) {
            final int start;
			int end;
			final int step;
			final boolean forward = edge % 2 == 0;
            if (forward) {
                start = 0;
                end = segments.length;
                step = 1;
                final Segment first = segments[0];
                final Vec3d dir = first.getVector();
				this.renderVertex(buf, edge, forward, first.getStart(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
            } else {
                start = segments.length - 1;
                end = -1;
                step = -1;
                final Segment last = segments[start];
                final Vec3d dir = last.getVector();
				this.renderVertex(buf, edge, forward, last.getEnd(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
            }
            for (int i = start; i != end; i += step) {
                final Segment segment = segments[i];
                final Segment next = i + step >= segments.length || i + step < 0 ? segment : segments[i + step];
                final Vec3d dir = segment.getVector();
                final Vec3d pos = forward ? segment.getEnd() : segment.getStart();
				this.renderVertex(buf, edge, forward, pos, dir, next.getVector(), cordRadius, 0.4F);
            }
            if (edge == 0) {
                final Segment last = segments[segments.length - 1];
                final Vec3d dir = last.getVector();
                final Vec3d endPoint = last.getEnd();
				this.renderVertex(buf, 0, false, endPoint, dir, dir, cordRadius, 0);
				this.renderVertex(buf, 2, false, endPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
				this.renderVertex(buf, 2, true, endPoint, dir, dir, cordRadius, 0);
				this.renderVertex(buf, 0, true, endPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
            } else if (edge == 1) {
                final Segment first = segments[0];
                final Vec3d dir = first.getVector();
				this.renderVertex(buf, 2, forward, first.getStart(), dir, dir, cordRadius, HIGHLIGHT_ALPHA);
            }
        }
        final Segment first = segments[0];
        final Vec3d dir = first.getVector();
        final Vec3d startPoint = first.getStart();
		this.renderVertex(buf, 0, true, startPoint, dir, dir, cordRadius, 0);
		this.renderVertex(buf, 2, true, startPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
		this.renderVertex(buf, 0, false, startPoint, dir, dir, cordRadius, 0);
		this.renderVertex(buf, 0, true, startPoint, dir, dir, cordRadius, HIGHLIGHT_ALPHA);
    }*/

    private void renderVertex(final BufferBuilder buf, final int edge, final boolean forward, Vec3d pos, Vec3d dir, Vec3d toDir, final float cordRadius, final float alpha) {
        if (forward) {
            toDir = Mth.negate(toDir);
        } else {
            dir = Mth.negate(dir);
        }
        Vec3d up;
        final boolean colinear = dir.dotProduct(toDir) < -1 + 1e-6;
        if (colinear) {
            final double h = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
            if (h < 1e-6) {
                up = new Vec3d(-1, 0, 0);
            } else {
                up = new Vec3d(dir.x / h * -dir.y, h, dir.z / h * -dir.y).normalize();
            }
        } else {
            up = Mth.lerp(dir, toDir, 0.5F).normalize();
        }
        final Vec3d side = dir.crossProduct(up).normalize();
        if (edge < 2) {
            up = Mth.negate(up);
        }
        pos = pos.scale(0.0625F).add(up.scale(cordRadius + 0.01F).add(side.scale(cordRadius + 0.01F)));
        buf.pos(pos.x, pos.y, pos.z).color(0, 0, 0, alpha).endVertex();
    }

    private static class HitConnection extends Entity {
        @Nullable
        private HitResult result;

        private HitConnection(final World world) {
            super(EntityType.ITEM, world);
			this.setEntityId(-1);
        }

        @Override
        public boolean attackEntityFrom(final DamageSource source, final float amount) {
			this.processAction(PlayerAction.ATTACK);
            return false;
        }

        @Override
        public boolean processInitialInteract(final PlayerEntity player, final Hand hand) {
            if (hand == Hand.MAIN_HAND) {
				this.processAction(PlayerAction.INTERACT);
            }
            return false;
        }

        private void processAction(final PlayerAction action) {
            if (this.result != null) {
				this.result.connection.processClientAction(Minecraft.getInstance().player, action, this.result.intersection);
            }
        }

        @Override
        public ItemStack getPickedResult(final RayTraceResult target) {
            if (this.result == null) {
                return ItemStack.EMPTY;
            }
            return this.result.connection.getItemStack();
        }

        @Override
        protected void registerData() {}

        @Override
        protected void readAdditional(final CompoundNBT compound) {}

        @Override
        protected void writeAdditional(final CompoundNBT compound) {}

        @Override
        public IPacket<?> createSpawnPacket() {
            return null;
        }
    }

    private static final class HitResult {
        private final Connection connection;

        private final Intersection intersection;

        public HitResult(final Connection connection, final Intersection intersection) {
            this.connection = connection;
            this.intersection = intersection;
        }
    }
}
