package me.paulf.fairylights.client;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import me.paulf.fairylights.util.Curve;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public final class ClientEventHandler {
    private static final float HIGHLIGHT_ALPHA = 0.4F;

    @Nullable
    public static Connection getHitConnection() {
        final net.minecraft.world.phys.HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult) {
            final Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof HitConnection) {
                return ((HitConnection) entity).result.connection;
            }
        }
        return null;
    }

    public void renderOverlay(final ForgeGui gui, final GuiGraphics poseStack, final float partialTick, final int screenWidth, final int screenHeight) {
        final Connection conn = getHitConnection();
        if (!(conn instanceof HangingLightsConnection)) {
            return;
        }
        final Jingle jingle = ((HangingLightsConnection) conn).getPlayingJingle();
        if (jingle == null) {
            return;
        }
        final List<String> lines = List.of(
            "Song: " + jingle.getTitle(),
            "Artist: " + jingle.getArtist());
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (!Strings.isNullOrEmpty(line)) {
                final int lineHeight = gui.getFont().lineHeight;
                final int textWidth = gui.getFont().width(line);
                final int y = 2 + lineHeight * i;
                poseStack.fill(1, y - 1, 2 + textWidth + 1, y + lineHeight - 1, 0x90505050);
                poseStack.drawString(gui.getFont(), line, 2, y, 0xe0e0e0);
            }
        }
    }

    public static void updateHitConnection() {
        final Minecraft mc = Minecraft.getInstance();
        final Entity viewer = mc.getCameraEntity();
        if (mc.hitResult != null && mc.level != null && viewer != null) {
            final HitResult result = getHitConnection(mc.level, viewer);
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
    private static HitResult getHitConnection(final Level world, final Entity viewer) {
        final AABB bounds = new AABB(viewer.blockPosition()).inflate(Connection.MAX_LENGTH + 1.0D);
        final Set<Fastener<?>> fasteners = collectFasteners(world, bounds);
        return getHitConnection(viewer, bounds, fasteners);
    }

    private static Set<Fastener<?>> collectFasteners(final Level world, final AABB bounds) {
        final Set<Fastener<?>> fasteners = Sets.newLinkedHashSet();
        final CollectFastenersEvent event = new CollectFastenersEvent(world, bounds, fasteners);
        world.getEntitiesOfClass(FenceFastenerEntity.class, bounds)
            .forEach(event::accept);
        final int minX = Mth.floor(bounds.minX / 16.0D);
        final int maxX = Mth.ceil(bounds.maxX / 16.0D);
        final int minZ = Mth.floor(bounds.minZ / 16.0D);
        final int maxZ = Mth.ceil(bounds.maxZ / 16.0D);
        final ChunkSource provider = world.getChunkSource();
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                final LevelChunk chunk = provider.getChunk(x, z, false);
                if (chunk != null) {
                    event.accept(chunk);
                }
            }
        }
        MinecraftForge.EVENT_BUS.post(event);
        return fasteners;
    }

    @Nullable
    private static HitResult getHitConnection(final Entity viewer, final AABB bounds, final Set<Fastener<?>> fasteners) {
        if (fasteners.isEmpty()) {
            return null;
        }
        final Vec3 origin = viewer.getEyePosition(1);
        final Vec3 look = viewer.getLookAngle();
        final double reach = Minecraft.getInstance().gameMode.getPickRange();
        final Vec3 end = origin.add(look.x * reach, look.y * reach, look.z * reach);
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
    public void drawBlockHighlight(final RenderHighlightEvent.Entity event) {
        final Entity entity = event.getTarget().getEntity();
        final Vec3 pos = event.getCamera().getPosition();
        final MultiBufferSource buf = event.getMultiBufferSource();
        if (entity instanceof FenceFastenerEntity) {
            this.drawFenceFastenerHighlight((FenceFastenerEntity) entity, event.getPoseStack(), buf.getBuffer(RenderType.lines()), event.getPartialTick(), pos.x, pos.y, pos.z);
        } else if (entity instanceof final HitConnection hit) {
            if (hit.result.intersection.getFeatureType() == Connection.CORD_FEATURE) {
                final PoseStack matrix = event.getPoseStack();
                matrix.pushPose();
                final Vec3 p = hit.result.connection.getFastener().getConnectionPoint();
                matrix.translate(p.x - pos.x, p.y - pos.y, p.z - pos.z);
                this.renderHighlight(hit.result.connection, matrix, buf.getBuffer(RenderType.lines()));
                matrix.popPose();
            } else {
                final AABB bb = hit.result.intersection.getHitBox().move(-pos.x, -pos.y, -pos.z).inflate(0.002D);
                LevelRenderer.renderLineBox(event.getPoseStack(), buf.getBuffer(RenderType.lines()), bb, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
            }
        }
    }

    private void drawFenceFastenerHighlight(final FenceFastenerEntity fence, final PoseStack matrix, final VertexConsumer buf, final float delta, final double dx, final double dy, final double dz) {
        final Player player = Minecraft.getInstance().player;
        // Check if the server will allow interaction
        if (player != null && (player.hasLineOfSight(fence) || player.distanceToSqr(fence) <= 9.0D)) {
            final AABB selection = fence.getBoundingBox().move(-dx, -dy, -dz).inflate(0.002D);
            LevelRenderer.renderLineBox(matrix, buf, selection, 0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA);
        }
    }

    private void renderHighlight(final Connection connection, final PoseStack matrix, final VertexConsumer buf) {
        final Curve cat = connection.getCatenary();
        if (cat == null) {
            return;
        }
        final Vector3f p = new Vector3f();
        final Vector3f v1 = new Vector3f();
        final Vector3f v2 = new Vector3f();
        final LineBuilder builder = new LineBuilder(matrix, buf);
        final float r = connection.getRadius() + 0.01F;
        for (int edge = 0; edge < 4; edge++) {
            p.set(cat.getX(0), cat.getY(0), cat.getZ(0));
            v1.set(cat.getDx(0), cat.getDy(0), cat.getDz(0));
            v1.normalize();
            v2.set(-v1.x(), -v1.y(), -v1.z());
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(builder, (n + 1) / 2 % 4, p, v1, v2, r);
            }
            this.addVertex(builder, edge, p, v1, v2, r);
            for (int i = 1; i < cat.getCount() - 1; i++) {
                p.set(cat.getX(i), cat.getY(i), cat.getZ(i));
                v2.set(-cat.getDx(i), -cat.getDy(i), -cat.getDz(i));
                v2.normalize();
                this.addVertex(builder, edge, p, v1, v2, r);
                this.addVertex(builder, edge, p, v1, v2, r);
                v1.set(-v2.x(), -v2.y(), -v2.z());
            }
            p.set(cat.getX(), cat.getY(), cat.getZ());
            v2.set(-v1.x(), -v1.y(), -v1.z());
            this.addVertex(builder, edge, p, v1, v2, r);
            for (int n = 0; edge == 0 && n < 8; n++) {
                this.addVertex(builder, (n + 1) / 2 % 4, p, v1, v2, r);
            }
        }
    }

    static class LineBuilder {
        final PoseStack matrix;
        final VertexConsumer buf;
        Vector3f last;

        LineBuilder(PoseStack matrix, VertexConsumer buf) {
            this.matrix = matrix;
            this.buf = buf;
        }

        void accept(Vector3f pos) {
            if (this.last == null) {
                this.last = pos;
            } else {
                Vector3f n = new Vector3f(pos);
                n.sub(this.last);
                n.normalize();
                n = this.matrix.last().normal().transform(n);
                this.buf.vertex(this.matrix.last().pose(), this.last.x(), this.last.y(), this.last.z())
                    .color(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA)
                    .normal(n.x(), n.y(), n.z())
                    .endVertex();
                this.buf.vertex(this.matrix.last().pose(), pos.x(), pos.y(), pos.z())
                    .color(0.0F, 0.0F, 0.0F, HIGHLIGHT_ALPHA)
                    .normal(n.x(), n.y(), n.z())
                    .endVertex();
                this.last = null;
            }
        }
    }

    private void addVertex(final LineBuilder builder, final int edge, final Vector3f p, final Vector3f v1, final Vector3f v2, final float r) {
        builder.accept(this.get(edge, p, v1, v2, r));
    }

    private Vector3f get(final int edge, final Vector3f p, final Vector3f v1, final Vector3f v2, final float r) {
        final Vector3f up = new Vector3f();
        final Vector3f side = new Vector3f();
        // if collinear
        if (v1.dot(v2) < -(1.0F - 1.0e-2F)) {
            final float h = Mth.sqrt(v1.x() * v1.x() + v1.z() * v1.z());
            // if vertical
            if (h < 1.0e-2F) {
                up.set(-1.0F, 0.0F, 0.0F);
            } else {
                up.set(-v1.x() / h * -v1.y(), -h, -v1.z() / h * -v1.y());
            }
        } else {
            up.set(v2.x(), v2.y(), v2.z());
            up.lerp(v1, 0.5F);
        }
        up.normalize();
        side.set(v1.x(), v1.y(), v1.z());
        side.cross(up);
        side.normalize();
        side.mul(edge == 0 || edge == 3 ? -r : r);
        up.mul(edge < 2 ? -r : r);
        up.add(side);
        up.add(p);
        return up;
    }

    static class HitConnection extends Entity {
        final ClientEventHandler.HitResult result;

        HitConnection(final Level world, final ClientEventHandler.HitResult result) {
            super(EntityType.ITEM, world);
            this.setId(-1);
            this.result = result;
            this.setPos(result.intersection.getResult());
        }

        @Override
        public boolean hurt(final DamageSource source, final float amount) {
            if (source.getEntity() == Minecraft.getInstance().player) {
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
        public ItemStack getPickedResult(net.minecraft.world.phys.HitResult target) {
            return this.result.connection.getItemStack();
        }

        @Override
        protected void defineSynchedData() {
        }

        @Override
        protected void addAdditionalSaveData(final CompoundTag compound) {
        }

        @Override
        protected void readAdditionalSaveData(final CompoundTag compound) {
        }

        @Override
        public Packet<ClientGamePacketListener> getAddEntityPacket() {
            return new Packet<>() {
                @Override
                public void write(final FriendlyByteBuf buf) {
                    
                }

                @Override
                public void handle(final ClientGamePacketListener p_131342_)
                {

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
