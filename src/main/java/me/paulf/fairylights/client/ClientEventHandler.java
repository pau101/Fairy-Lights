package me.paulf.fairylights.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.collision.Collidable;
import me.paulf.fairylights.server.collision.Intersection;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.connection.PlayerAction;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.CollectFastenersEvent;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.jingle.Jingle;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ClientEventHandler {
    private static final float HIGHLIGHT_ALPHA = 0.4F;

    @Nullable
    public static Connection getHitConnection() {
        final HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult) {
            final Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof HitConnection) {
                return ((HitConnection) entity).result.connection;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.level != null && !mc.isPaused()) {
            this.getBlockEntities(mc.level).stream()
                .filter(FastenerBlockEntity.class::isInstance)
                .map(FastenerBlockEntity.class::cast)
                .flatMap(f -> f.getCapability(CapabilityHandler.FASTENER_CAP).map(Stream::of).orElse(Stream.empty()))
                .forEach(Fastener::update);
        }
    }

    private Collection<BlockEntity> getBlockEntities(final Level world) {
        try {
            return new ArrayList<>(world.blockEntityTickers);
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
            lines.add("Song: " + jingle.getTitle());
            lines.add("Artist: " + jingle.getArtist());
        }
    }

    public static void updateHitConnection() {
        final Minecraft mc = Minecraft.getInstance();
        final Entity viewer = mc.getCameraEntity();
        if (mc.hitResult != null && mc.level != null && viewer != null) {
            final HitResultConnectionIntersection result = getHitConnection(mc.level, viewer);
            if (result != null) {
                final Vec3 eyes = viewer.getEyePosition(1.0F);
                if (result.intersection.getResult().distanceTo(eyes) < mc.hitResult.getLocation().distanceTo(eyes)) {
                    mc.hitResult = new EntityHitResult(new HitConnection(mc.level, result));
                    mc.crosshairPickEntity = null;
                }
            }
        }
    }

    @Nullable
    private static HitResultConnectionIntersection getHitConnection(final Level world, final Entity viewer) {
        final AABB bounds = new AABB(viewer.blockPosition()).inflate(Connection.MAX_LENGTH + 1.0D);
        final Set<Fastener<?>> fasteners = collectFasteners(world, bounds);
        return getHitConnection(viewer, bounds, fasteners);
    }

    private static Set<Fastener<?>> collectFasteners(final Level world, final AABB bounds) {
        final Set<Fastener<?>> fasteners = Sets.newLinkedHashSet();
        final CollectFastenersEvent event = new CollectFastenersEvent(world, bounds, fasteners);
        world.func_217357_a(FenceFastenerEntity.class, bounds)
            .forEach(event::accept);
        final int minX = MathHelper.func_76128_c(bounds.field_72340_a / 16.0D);
        final int maxX = MathHelper.func_76143_f(bounds.field_72336_d / 16.0D);
        final int minZ = MathHelper.func_76128_c(bounds.field_72339_c / 16.0D);
        final int maxZ = MathHelper.func_76143_f(bounds.field_72334_f / 16.0D);
        final AbstractChunkProvider provider = world.func_72863_F();
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                final Chunk chunk = provider.func_217205_a(x, z, false);
                if (chunk != null) {
                    event.accept(chunk);
                }
            }
        }
        MinecraftForge.EVENT_BUS.post(event);
        return fasteners;
    }

    @Nullable
    private static HitResultConnectionIntersection getHitConnection(final Entity viewer, final AxisAlignedBB bounds, final Set<Fastener<?>> fasteners) {
        if (fasteners.isEmpty()) {
            return null;
        }
        final Vector3d origin = viewer.func_174824_e(1);
        final Vector3d look = viewer.func_70676_i(1);
        final double reach = Minecraft.func_71410_x().field_71442_b.func_78757_d();
        final Vector3d end = origin.func_72441_c(look.field_72450_a * reach, look.field_72448_b * reach, look.field_72449_c * reach);
        Connection found = null;
        Intersection rayTrace = null;
        double distance = Double.MAX_VALUE;
        for (final Fastener<?> fastener : fasteners) {
            for (final Connection connection : fastener.getOwnConnections()) {
                if (connection.getDestination().getType() == FastenerType.PLAYER) {
                    continue;
                }
                final Collidable collision = connection.getCollision();
                final Intersection result = collision.intersect(origin, end);
                if (result != null) {
                    final double dist = result.getResult().func_72438_d(origin);
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
        return new HitResultConnectionIntersection(found, rayTrace);
    }

    @SubscribeEvent
    public void drawBlockHighlight(final DrawHighlightEvent.HighlightEntity event) {
        final Entity entity = event.getTarget().func_216348_a();
        final Vector3d pos = event.getInfo().func_216785_c();
        final IRenderTypeBuffer buf = event.getBuffers();
        if (entity instanceof FenceFastenerEntity) {
            this.drawFenceFastenerHighlight((FenceFastenerEntity) entity, event.getMatrix(), buf.getBuffer(RenderType.func_228659_m_()), event.getPartialTicks(), pos.field_72450_a, pos.field_72448_b, pos.field_72449_c);
        } else if (entity instanceof HitConnection) {
            final HitConnection hit = (HitConnection) entity;
            if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
                final MatrixStack matrix = event.getMatrix();
                matrix.func_227860_a_();
                final Vector3d p = hit.result.connection.getFastener().getConnectionPoint();
                matrix.func_227861_a_(p.field_72450_a - pos.field_72450_a, p.field_72448_b - pos.field_72448_b, p.field_72449_c - pos.field_72449_c);
                this.renderHighlight(hit.result.connection, matrix, buf.getBuffer(RenderType.func_228659_m_()));
                matrix.func_227865_b_();
            } else {
                final AxisAlignedBB bb = hit.result.intersection.getHitBox().func_72317_d(-pos.field_72450_a, -pos.field_72448_b, -pos.field_72449_c).func_186662_g(0.002D);
                WorldRenderer.func_228430_a_(event.getMatrix(), buf.getBuffer(RenderType.func_228659_m_()), bb, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
            }
        }
    }

    private void drawFenceFastenerHighlight(final FenceFastenerEntity fence, final MatrixStack matrix, final IVertexBuilder buf, final float delta, final double dx, final double dy, final double dz) {
        final PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
        // Check if the server will allow interaction
        if (player != null && (player.func_70685_l(fence) || player.func_70068_e(fence) <= 9.0D)) {
            final AxisAlignedBB selection = fence.func_174813_aQ().func_72317_d(-dx, -dy, -dz).func_186662_g(0.002D);
            WorldRenderer.func_228430_a_(matrix, buf, selection, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
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
            p.func_195905_a(cat.getX(0), cat.getY(0), cat.getZ(0));
            v1.func_195905_a(cat.getDx(0), cat.getDy(0), cat.getDz(0));
            v1.func_229194_d_();
            v2.func_195905_a(-v1.func_195899_a(), -v1.func_195900_b(), -v1.func_195902_c());
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(matrix, buf, (n + 1) / 2 % 4, p, v1, v2, r);
            }
            this.addVertex(matrix, buf, edge, p, v1, v2, r);
            for (int i = 1; i < cat.getCount() - 1; i++) {
                p.func_195905_a(cat.getX(i), cat.getY(i), cat.getZ(i));
                v2.func_195905_a(-cat.getDx(i), -cat.getDy(i), -cat.getDz(i));
                v2.func_229194_d_();
                this.addVertex(matrix, buf, edge, p, v1, v2, r);
                this.addVertex(matrix, buf, edge, p, v1, v2, r);
                v1.func_195905_a(-v2.func_195899_a(), -v2.func_195900_b(), -v2.func_195902_c());
            }
            p.func_195905_a(cat.getX(), cat.getY(), cat.getZ());
            v2.func_195905_a(-v1.func_195899_a(), -v1.func_195900_b(), -v1.func_195902_c());
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
        if (v1.func_195903_b(v2) < -(1.0F - 1.0e-2F)) {
            final float h = MathHelper.func_76129_c(v1.func_195899_a() * v1.func_195899_a() + v1.func_195902_c() * v1.func_195902_c());
            // if vertical
            if (h < 1.0e-2F) {
                up.func_195905_a(-1.0F, 0.0F, 0.0F);
            } else {
                up.func_195905_a(-v1.func_195899_a() / h * -v1.func_195900_b(), -h, -v1.func_195902_c() / h * -v1.func_195900_b());
            }
        } else {
            up.func_195905_a(v2.func_195899_a(), v2.func_195900_b(), v2.func_195902_c());
            up.func_229190_a_(v1, 0.5F);
        }
        up.func_229194_d_();
        side.func_195905_a(v1.func_195899_a(), v1.func_195900_b(), v1.func_195902_c());
        side.func_195896_c(up);
        side.func_229194_d_();
        side.func_195898_a(edge == 0 || edge == 3 ? -r : r);
        up.func_195898_a(edge < 2 ? -r : r);
        up.func_229189_a_(side);
        up.func_229189_a_(p);
        buf.func_227888_a_(matrix.func_227866_c_().func_227870_a_(), up.func_195899_a(), up.func_195900_b(), up.func_195902_c()).func_227885_a_(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA).func_181675_d();
    }

    static class HitConnection extends Entity {
        final HitResultConnectionIntersection result;

        HitConnection(final Level world, final HitResultConnectionIntersection result) {
            super(EntityType.ITEM, world);
            this.setId(-1);
            this.result = result;
        }

        @Override
        public boolean hurt(final DamageSource source, final float amount) {
            if (source.getDirectEntity() == Minecraft.getInstance().player) {
                this.processAction(PlayerAction.ATTACK);
                return true;
            }
            return false;
        }

        @Override
        public InteractionResult interact(final Player player, final InteractionHand hand) {
            if (player == Minecraft.getInstance().player) {
                this.processAction(PlayerAction.INTERACT);
                return InteractionResult.SUCCESS;
            }
            return super.interact(player, hand);
        }

        private void processAction(final PlayerAction action) {
            this.result.connection.processClientAction(Minecraft.getInstance().player, action, this.result.intersection);
        }

        @Override
        public ItemStack getPickedResult(final HitResult target) {
            return this.result.connection.getItemStack();
        }

        @Override
        protected void defineSynchedData() {}

        @Override
        protected void readAdditionalSaveData(final CompoundTag compound) {}

        @Override
        protected void addAdditionalSaveData(final CompoundTag compound) {}

        @Override
        public Packet<?> getAddEntityPacket() {
            return new Packet<PacketListener>() {
                @Override
                public void write(final FriendlyByteBuf buf) {
                }

//                @Override
//                public void func_148840_b(final FriendlyByteBuf buf) {
//                }

                @Override
                public void handle(final PacketListener handler) {
                }
            };
        }
    }

    private static final class HitResultConnectionIntersection {
        private final Connection connection;

        private final Intersection intersection;

        public HitResultConnectionIntersection(final Connection connection, final Intersection intersection) {
            this.connection = connection;
            this.intersection = intersection;
        }
    }
}
