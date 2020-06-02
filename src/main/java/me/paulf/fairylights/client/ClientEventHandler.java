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
import me.paulf.fairylights.server.fastener.connection.collision.Collidable;
import me.paulf.fairylights.server.fastener.connection.collision.Intersection;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.fastener.connection.type.hanginglights.HangingLightsConnection;
import me.paulf.fairylights.server.jingle.Jingle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
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
    public static Connection getHitConnection() {
        final RayTraceResult result = Minecraft.getInstance().objectMouseOver;
        if (result instanceof EntityRayTraceResult) {
            final Entity entity = ((EntityRayTraceResult) result).getEntity();
            if (entity instanceof HitConnection) {
                return ((HitConnection) entity).result.connection;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.world != null && !mc.isGamePaused()) {
            this.getBlockEntities(mc.world).stream()
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
        final Connection conn = getHitConnection();
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

    public static void updateHitConnection() {
        final Minecraft mc = Minecraft.getInstance();
        final Entity viewer = mc.getRenderViewEntity();
        if (mc.objectMouseOver != null && mc.world != null && viewer != null) {
            final HitResult result = getHitConnection(mc.world, viewer);
            if (result != null) {
                final Vec3d eyes = viewer.getEyePosition(1.0F);
                if (result.intersection.getResult().distanceTo(eyes) < mc.objectMouseOver.getHitVec().distanceTo(eyes)) {
                    mc.objectMouseOver = new EntityRayTraceResult(new HitConnection(mc.world, result));
                    mc.pointedEntity = null;
                }
            }
        }
    }

    @Nullable
    private static HitResult getHitConnection(final World world, final Entity viewer) {
        final AxisAlignedBB bounds = new AxisAlignedBB(viewer.getPosition()).grow(Connection.MAX_LENGTH + 1.0D);
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
                final Collidable collision = connection.getCollision();
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

    public static RayTraceResult drawSelectionBox(final RayTraceResult target, final WorldRenderer context, final ActiveRenderInfo info, final float delta, final MatrixStack matrix, final IRenderTypeBuffer buffers) {
        if (target.getType() == RayTraceResult.Type.ENTITY) {
            MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightEntity(context, info, target, delta, matrix, buffers));
        }
        return target;
    }

    @SubscribeEvent
    public void drawBlockHighlight(final DrawHighlightEvent.HighlightEntity event) {
        final Entity entity = event.getTarget().getEntity();
        final Vec3d pos = event.getInfo().getProjectedView();
        final IRenderTypeBuffer buf = event.getBuffers();
        if (entity instanceof FenceFastenerEntity) {
            this.drawFenceFastenerHighlight((FenceFastenerEntity) entity, event.getMatrix(), buf.getBuffer(RenderType.getLines()), event.getPartialTicks(), pos.x, pos.y, pos.z);
        } else if (entity instanceof HitConnection) {
            final HitConnection hit = (HitConnection) entity;
            if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
                final MatrixStack matrix = event.getMatrix();
                matrix.push();
                final Vec3d p = hit.result.connection.getFastener().getConnectionPoint();
                matrix.translate(p.x - pos.x, p.y - pos.y, p.z - pos.z);
                this.renderHighlight(hit.result.connection, matrix, buf.getBuffer(RenderType.getLines()));
                matrix.pop();
            } else {
                final AxisAlignedBB bb = hit.result.intersection.getHitBox().offset(-pos.x, -pos.y, -pos.z).grow(0.002D);
                WorldRenderer.drawBoundingBox(event.getMatrix(), buf.getBuffer(RenderType.getLines()), bb, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
            }
        }
    }

    private void drawFenceFastenerHighlight(final FenceFastenerEntity fence, final MatrixStack matrix, final IVertexBuilder buf, final float delta, final double dx, final double dy, final double dz) {
        final PlayerEntity player = Minecraft.getInstance().player;
        // Check if the server will allow interaction
        if (player != null && (player.canEntityBeSeen(fence) || player.getDistanceSq(fence) <= 9.0D)) {
            final AxisAlignedBB selection = fence.getBoundingBox().offset(-dx, -dy, -dz).grow(0.002D);
            WorldRenderer.drawBoundingBox(matrix, buf, selection, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
        }
    }

    private void renderHighlight(final Connection connection, final MatrixStack matrix, final IVertexBuilder buf) {
        final Catenary cat = connection.getCatenary();
        if (cat == null) {
            return;
        }
        final Vector3f p = new Vector3f();
        final Vector3f v1 = new Vector3f();
        final Vector3f v2 = new Vector3f();
        final float r = connection.getRadius() + 0.01F;
        for (int edge = 0; edge < 4; edge++) {
            p.set(cat.getX(0), cat.getY(0), cat.getZ(0));
            v1.set(cat.getDx(0), cat.getDy(0), cat.getDz(0));
            v1.normalize();
            v2.set(-v1.getX(), -v1.getY(), -v1.getZ());
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(matrix, buf, (n + 1) / 2 % 4, p, v1, v2, r);
            }
            this.addVertex(matrix, buf, edge, p, v1, v2, r);
            for (int i = 1; i < cat.getCount() - 1; i++) {
                p.set(cat.getX(i), cat.getY(i), cat.getZ(i));
                v2.set(-cat.getDx(i), -cat.getDy(i), -cat.getDz(i));
                v2.normalize();
                this.addVertex(matrix, buf, edge, p, v1, v2, r);
                this.addVertex(matrix, buf, edge, p, v1, v2, r);
                v1.set(-v2.getX(), -v2.getY(), -v2.getZ());
            }
            p.set(cat.getX(), cat.getY(), cat.getZ());
            v2.set(-v1.getX(), -v1.getY(), -v1.getZ());
            this.addVertex(matrix, buf, edge, p, v1, v2, r);
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(matrix, buf, (n + 1) / 2 % 4, p, v1, v2, r);
            }
        }
    }

    private void addVertex(final MatrixStack matrix, final IVertexBuilder buf, final int edge, final Vector3f p, final Vector3f v1, final Vector3f v2, final float r) {
        final Vector3f up = new Vector3f();
        final Vector3f side = new Vector3f();
        // if collinear
        if (v1.dot(v2) < -(1.0F - 1.0e-2F)) {
            final float h = MathHelper.sqrt(v1.getX() * v1.getX() + v1.getZ() * v1.getZ());
            // if vertical
            if (h < 1.0e-2F) {
                up.set(-1.0F, 0.0F, 0.0F);
            } else {
                up.set(-v1.getX() / h * -v1.getY(), -h, -v1.getZ() / h * -v1.getY());
            }
        } else {
            up.set(v2.getX(), v2.getY(), v2.getZ());
            up.lerp(v1, 0.5F);
        }
        up.normalize();
        side.set(v1.getX(), v1.getY(), v1.getZ());
        side.cross(up);
        side.normalize();
        side.mul(edge == 0 || edge == 3 ? -r : r);
        up.mul(edge < 2 ? -r : r);
        up.add(side);
        up.add(p);
        buf.pos(matrix.getLast().getMatrix(), up.getX(), up.getY(), up.getZ()).color(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA).endVertex();
    }

    static class HitConnection extends Entity {
        final HitResult result;

        HitConnection(final World world, final HitResult result) {
            super(EntityType.ITEM, world);
            this.setEntityId(-1);
            this.result = result;
        }

        @Override
        public boolean attackEntityFrom(final DamageSource source, final float amount) {
            if (source.getTrueSource() == Minecraft.getInstance().player) {
                this.processAction(PlayerAction.ATTACK);
                return true;
            }
            return false;
        }

        @Override
        public boolean processInitialInteract(final PlayerEntity player, final Hand hand) {
            if (player == Minecraft.getInstance().player) {
                this.processAction(PlayerAction.INTERACT);
                return true;
            }
            return false;
        }

        private void processAction(final PlayerAction action) {
            this.result.connection.processClientAction(Minecraft.getInstance().player, action, this.result.intersection);
        }

        @Override
        public ItemStack getPickedResult(final RayTraceResult target) {
            return this.result.connection.getItemStack();
        }

        @Override
        protected void registerData() {}

        @Override
        protected void writeAdditional(final CompoundNBT compound) {}

        @Override
        protected void readAdditional(final CompoundNBT compound) {}

        @Override
        public IPacket<?> createSpawnPacket() {
            return new IPacket<INetHandler>() {
                @Override
                public void readPacketData(final PacketBuffer buf) {
                }

                @Override
                public void writePacketData(final PacketBuffer buf) {
                }

                @Override
                public void processPacket(final INetHandler handler) {
                }
            };
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
