package me.paulf.fairylights.server.connection;

import me.paulf.fairylights.server.collision.CollidableList;
import me.paulf.fairylights.server.collision.FeatureCollisionTree;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.feature.FeatureType;
import me.paulf.fairylights.server.feature.HangingFeature;
import me.paulf.fairylights.util.AABBBuilder;
import me.paulf.fairylights.util.Catenary;
import me.paulf.fairylights.util.Curve;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class HangingFeatureConnection<F extends HangingFeature> extends Connection {
    protected static final FeatureType FEATURE = FeatureType.register("feature");

    protected F[] features = this.createFeatures(0);

    public HangingFeatureConnection(final ConnectionType<? extends HangingFeatureConnection<F>> type, final Level world, final Fastener<?> fastener, final UUID uuid) {
        super(type, world, fastener, uuid);
    }

    public final F[] getFeatures() {
        return this.features;
    }

    @Override
    protected void onCalculateCatenary(final boolean relocated) {
        this.updateFeatures(relocated);
    }

    protected void updateFeatures(final boolean relocated) {
        final Curve catenary = this.getCatenary();
        final float spacing = this.getFeatureSpacing();
        final float totalLength = catenary.getLength();
        if (totalLength > 2.0F * Connection.MAX_LENGTH) {
            this.onBeforeUpdateFeatures();
            this.features = this.createFeatures(0);
            this.onAfterUpdateFeatures();
            return;
        }
        final F[] prev = this.features;
        final List<F> features = new ArrayList<>();
        this.onBeforeUpdateFeatures();
        catenary.visitPoints(spacing, true, (index, x, y, z, yaw, pitch) -> {
            final F feature;
            if (!relocated && prev != null && index < prev.length && this.canReuse(prev[index], index)) {
                feature = prev[index];
                feature.set(new Vec3(x, y, z), yaw, pitch);
            } else {
                feature = this.createFeature(index, new Vec3(x, y, z), yaw, pitch);
            }
            this.updateFeature(feature);
            features.add(feature);
        });
        this.features = features.toArray(this.createFeatures(features.size()));
        this.onAfterUpdateFeatures();
    }

    protected boolean canReuse(final F feature, final int index) {
        return true;
    }

    protected abstract F[] createFeatures(int length);

    protected abstract F createFeature(int index, Vec3 point, float yaw, final float pitch);

    protected abstract float getFeatureSpacing();

    protected void onBeforeUpdateFeatures() {}

    protected void updateFeature(final F feature) {}

    protected void onAfterUpdateFeatures() {}

    @Override
    public void addCollision(final CollidableList.Builder collision, final Vec3 origin) {
        super.addCollision(collision, origin);
        if (this.features.length > 0) {
            final MatrixStack matrix = new MatrixStack();
            collision.add(FeatureCollisionTree.build(FEATURE, this.features, f -> {
                final Vec3 pos = f.getPoint();
                final double x = origin.x + pos.x;
                final double y = origin.y + pos.y;
                final double z = origin.z + pos.z;
                matrix.push();
                if (f.parallelsCord()) {
                    matrix.rotate(-f.getYaw(), 0.0F, 1.0F, 0.0F);
                    matrix.rotate(f.getPitch(), 0.0F, 0.0F, 1.0F);
                }
                matrix.translate(0.0F, -f.getDescent(), 0.0F);
                final AABBBuilder bounds = new AABBBuilder();
                final AABB bb = f.getBounds().inflate(0.01D);
                final Vec3[] verts = {
                    new Vec3(bb.minX, bb.minY, bb.minZ),
                    new Vec3(bb.maxX, bb.minY, bb.minZ),
                    new Vec3(bb.maxX, bb.minY, bb.minZ),
                    new Vec3(bb.minX, bb.minY, bb.maxZ),
                    new Vec3(bb.minX, bb.maxY, bb.minZ),
                    new Vec3(bb.maxX, bb.maxY, bb.minZ),
                    new Vec3(bb.maxX, bb.maxY, bb.maxZ),
                    new Vec3(bb.minX, bb.maxY, bb.maxZ)
                };
                for (final Vec3 vert : verts) {
                    bounds.include(matrix.transform(vert));
                }
                matrix.pop();
                return bounds.add(x, y, z).build();
            }));
        }
    }
}
