package me.paulf.fairylights.server.fastener.connection.collision;

import me.paulf.fairylights.server.fastener.connection.Feature;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.util.Mth;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Function;

public final class FeatureCollisionTree implements Collidable {
	private final FeatureType type;

	private final AxisAlignedBB[] tree;

	private final Feature[] nodeToFeature;

	private FeatureCollisionTree(FeatureType type, AxisAlignedBB[] tree, Feature[] nodeToFeature) {
		this.type = type;
		this.tree = tree;
		this.nodeToFeature = nodeToFeature;
	}

	@Nullable
	@Override
	public Intersection intersect(Vec3d origin, Vec3d end) {
		return intersect(origin, end, 0);
	}

	@Nullable
	private Intersection intersect(Vec3d origin, Vec3d end, int node) {
		Vec3d result;
		if (tree[node].contains(origin)) {
			result = origin;
		} else {
			result = tree[node].rayTrace(origin, end).orElse(null);
		}
		// If there is no intersection then there is no child intersection
		if (result == null) {
			return null;
		}
		// Check if leaf
		int nL = node * 2 + 1;
		if (nL >= tree.length || tree[nL] == null) {
			return new Intersection(result, tree[node], type, nodeToFeature[node]);
		}
		// Intersect left
		Intersection intersection = intersect(origin, end, nL);
		if (intersection != null) {
			return intersection;
		}
		// Intersect right
		return intersect(origin, end, node * 2 + 2);
	}

	public static <T extends Feature> FeatureCollisionTree build(FeatureType type, T[] features, Function<T, AxisAlignedBB> mapper) {
		return build(type, features, mapper, 0, features.length - 1);
	}

	public static <T extends Feature> FeatureCollisionTree build(FeatureType type, T[] features, Function<T, AxisAlignedBB> mapper, int start, int end) {
		AxisAlignedBB[] tree = new AxisAlignedBB[end == 0 ? 1 : (1 << (Mth.log2(end - start) + 2)) - 1];
		Feature[] treeFeatures = new Feature[tree.length];
		tree[0] = build(features, mapper, tree, treeFeatures, start, end, 0);
		return new FeatureCollisionTree(type, tree, treeFeatures);
	}

	private static <T extends Feature> AxisAlignedBB build(T[] features, Function<T, AxisAlignedBB> mapper, AxisAlignedBB[] tree, Feature[] treeFeatures, int min, int max, int node) {
		if (min > max) {
			throw new IllegalStateException(String.format("min > max, len: %d, tree: %s, min: %d, max: %d, node: %d", features.length, Arrays.toString(tree), min, max, node));
		}
		if (min == max) {
			T obj = features[min];
			treeFeatures[node] = obj;
			return mapper.apply(obj);
		}
		int mid = min + (max - min) / 2, nL = node * 2 + 1, nR = node * 2 + 2;
		return (tree[nL] = build(features, mapper, tree, treeFeatures, min, mid, nL)).union(tree[nR] = build(features, mapper, tree, treeFeatures, mid + 1, max, nR));
	}
}
