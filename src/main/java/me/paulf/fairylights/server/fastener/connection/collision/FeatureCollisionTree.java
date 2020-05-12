package me.paulf.fairylights.server.fastener.connection.collision;

import me.paulf.fairylights.server.fastener.connection.Feature;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class FeatureCollisionTree implements Collidable {
    private final FeatureType type;

    private final AxisAlignedBB[] tree;

    private final IntFunction<Feature> nodeToFeature;

    private FeatureCollisionTree(final FeatureType type, final AxisAlignedBB[] tree, final IntFunction<Feature> nodeToFeature) {
        this.type = type;
        this.tree = tree;
        this.nodeToFeature = nodeToFeature;
    }

    @Nullable
    @Override
    public Intersection intersect(final Vec3d origin, final Vec3d end) {
        return this.intersect(origin, end, 0);
    }

    @Nullable
    private Intersection intersect(final Vec3d origin, final Vec3d end, final int node) {
        final Vec3d result;
        if (this.tree[node].contains(origin)) {
            result = origin;
        } else {
            result = this.tree[node].rayTrace(origin, end).orElse(null);
        }
        // If there is no intersection then there is no child intersection
        if (result == null) {
            return null;
        }
        // Check if leaf
        final int nL = node * 2 + 1;
        if (nL >= this.tree.length || this.tree[nL] == null) {
            final Feature f = this.nodeToFeature.apply(node);
            if (f == null) {
                return null;
            }
            return new Intersection(result, this.tree[node], this.type, f);
        }
        // Intersect left
        final Intersection intersection = this.intersect(origin, end, nL);
        if (intersection != null) {
            return intersection;
        }
        // Intersect right
        return this.intersect(origin, end, node * 2 + 2);
    }

    public static <T> FeatureCollisionTree build(final FeatureType type, final T[] features, final Function<T, AxisAlignedBB> mapper, final IntFunction<Feature> nodeToFeature) {
        return build(type, features, mapper, nodeToFeature, 0, features.length - 1);
    }

    public static <T> FeatureCollisionTree build(final FeatureType type, final T[] features, final Function<T, AxisAlignedBB> mapper, final IntFunction<Feature> nodeToFeature, final int start, final int end) {
        final AxisAlignedBB[] tree = new AxisAlignedBB[end == 0 ? 1 : (1 << (Mth.log2(end - start) + 2)) - 1];
        tree[0] = build(features, mapper, tree, start, end, 0);
        return new FeatureCollisionTree(type, tree, nodeToFeature);
    }

    private static <T> AxisAlignedBB build(final T[] features, final Function<T, AxisAlignedBB> mapper, final AxisAlignedBB[] tree, final int min, final int max, final int node) {
        if (min > max) {
            throw new IllegalStateException(String.format("min > max, len: %d, tree: %s, min: %d, max: %d, node: %d", features.length, Arrays.toString(tree), min, max, node));
        }
        if (min == max) {
            return mapper.apply(features[min]);
        }
        final int mid = min + (max - min) / 2;
        final int nL = node * 2 + 1;
        final int nR = node * 2 + 2;
        return (tree[nL] = build(features, mapper, tree, min, mid, nL)).union(tree[nR] = build(features, mapper, tree, mid + 1, max, nR));
    }
}
