package me.paulf.fairylights.client;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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
import net.minecraft.client.Minecraft;
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
            final IRenderTypeBuffer buf = event.getBuffers();
            if (isFence) {
                this.drawFenceFastenerHighlight(player, (FenceFastenerEntity) ((EntityRayTraceResult) over).getEntity(), event.getMatrix(), buf.getBuffer(RenderType.getLines()), delta, dx, dy, dz);
            } else if (hit != null && hit.result != null) {
                if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
					this.renderHighlight(hit.result.connection, event.getMatrix(), buf.getBuffer(RenderType.getLines()));
                } else {
                    final AxisAlignedBB aabb = hit.result.intersection.getHitBox().offset(-dx, -dy, -dz).grow(0.002);
                    WorldRenderer.drawBoundingBox(event.getMatrix(), buf.getBuffer(RenderType.getLines()), aabb, 0, 0, 0, HIGHLIGHT_ALPHA);
                }
            }
        }
    }

    private void drawFenceFastenerHighlight(final PlayerEntity player, final FenceFastenerEntity fence, final MatrixStack matrix, final IVertexBuilder buf, final float delta, final double dx, final double dy, final double dz) {
        // Check if the server will allow interaction
        if (player.canEntityBeSeen(fence) || player.getDistanceSq(fence) <= 9) {
            final AxisAlignedBB selection = fence.getBoundingBox().offset(-dx, -dy, -dz).grow(0.002);
            WorldRenderer.drawBoundingBox(matrix, buf, selection, 0, 0, 0, HIGHLIGHT_ALPHA);
        }
    }

    private void renderHighlight(final Connection connection, final MatrixStack matrix, final IVertexBuilder buf) {
        final Catenary cat = connection.getCatenary();
        if (cat == null) {
            return;
        }
        final float cordRadius = connection.getRadius();
        for (int edge = 0; edge < 4; edge++) {
            final int start;
			int end;
			final int step;
			final boolean forward = edge % 2 == 0;
            if (forward) {
                start = 0;
                end = cat.getCount() - 1;
                step = 1;
                final float dirx = cat.getDx(start);
                final float diry = cat.getDy(start);
                final float dirz = cat.getDz(start);
				this.renderVertex(matrix, buf, edge, forward, cat.getX(start), cat.getY(start), cat.getZ(start), dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
            } else {
                start = cat.getCount() - 2;
                end = -1;
                step = -1;
                final float dirx = cat.getDx(start);
                final float diry = cat.getDy(start);
                final float dirz = cat.getDz(start);
				this.renderVertex(matrix, buf, edge, forward, cat.getX(start + 1), cat.getY(start + 1), cat.getZ(start + 1), dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
            }
            for (int i = start; i != end; i += step) {
                final int nexti = i + step >= cat.getCount() - 2 || i + step < 0 ? i : i + step;
                final float dirx = cat.getDx(i);
                final float diry = cat.getDy(i);
                final float dirz = cat.getDz(i);
                final float ndirx = cat.getDx(nexti);
                final float ndiry = cat.getDy(nexti);
                final float ndirz = cat.getDz(nexti);
                final float px = cat.getX(forward ? i + 1 : i);
                final float py = cat.getX(forward ? i + 1 : i);
                final float pz = cat.getX(forward ? i + 1 : i);
				this.renderVertex(matrix, buf, edge, forward, px, py, pz, dirx, diry, dirz, ndirx, ndiry, ndirz, cordRadius, 0.4F);
            }
            if (edge == 0) {
                final int last = cat.getCount() - 2;
                final float dirx = cat.getDx(last);
                final float diry = cat.getDy(last);
                final float dirz = cat.getDz(last);
                final float px = cat.getX(last + 1);
                final float py = cat.getY(last + 1);
                final float pz = cat.getZ(last + 1);
				this.renderVertex(matrix, buf, 0, false, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, 0);
				this.renderVertex(matrix, buf, 2, false, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
				this.renderVertex(matrix, buf, 2, true, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, 0);
				this.renderVertex(matrix, buf, 0, true, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
            } else if (edge == 1) {
                final float dirx = cat.getDx(0);
                final float diry = cat.getDy(0);
                final float dirz = cat.getDz(0);
                final float px = cat.getX(0);
                final float py = cat.getX(0);
                final float pz = cat.getX(0);
				this.renderVertex(matrix, buf, 2, forward, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
            }
        }
        final float dirx = cat.getDx(0);
        final float diry = cat.getDy(0);
        final float dirz = cat.getDz(0);
        final float px = cat.getX(0);
        final float py = cat.getX(0);
        final float pz = cat.getX(0);
		this.renderVertex(matrix, buf, 0, true, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, 0);
		this.renderVertex(matrix, buf, 2, true, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
		this.renderVertex(matrix, buf, 0, false, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, 0);
		this.renderVertex(matrix, buf, 0, true, px, py, pz, dirx, diry, dirz, dirx, diry, dirz, cordRadius, HIGHLIGHT_ALPHA);
    }

    private void renderVertex(final MatrixStack matrix, final IVertexBuilder buf, final int edge, final boolean forward, final float px, final float py, final float pz, float dirx, float diry, float dirz, float toDirx, float toDiry, float toDirz, final float cordRadius, final float alpha) {
        if (forward) {
            toDirx = -toDirx;
            toDiry = -toDiry;
            toDirz = -toDirz;
        } else {
            dirx = -dirx;
            diry = -diry;
            dirz = -dirz;
        }
        float upx, upy, upz;
        final boolean colinear = dirx * toDirx + diry * toDiry + dirz * toDirz < -1 + 1e-6F;
        if (colinear) {
            final float h = MathHelper.sqrt(dirx * dirx + dirz * dirz);
            if (h < 1e-6F) {
                upx = -1;
                upy = 0;
                upz = 0;
            } else {
                upx = dirx / h * -diry;
                upy = h;
                upz = dirz / h * -diry;
            }
        } else {
            upx = (dirx + toDirx) / 2.0F;
            upy = (diry + toDiry) / 2.0F;
            upz = (dirz + toDirz) / 2.0F;
        }
        float n = 1.0F / MathHelper.sqrt(upx * upx + upy * upy + upz * upz);
        upx *= n;
        upy *= n;
        upz *= n;
        float sidex = diry * upz - dirz * upy;
        float sidey = dirz * upx - dirx * upz;
        float sidez = dirx * upy - diry * upx;
        n = 1.0F / MathHelper.sqrt(sidex * sidex + sidey * sidey + sidez * sidez);
        sidex *= n;
        sidey *= n;
        sidez *= n;
        if (edge < 2) {
            upx = -upx;
            upy = -upy;
            upz = -upz;
        }
        final float r = cordRadius * 0.01F;
        buf.pos(matrix.getLast().getMatrix(), px + upx * r + sidex * r, py + upy * r + sidey * r, pz + upz * r + sidez * r).color(0, 0, 0, alpha).endVertex();
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
