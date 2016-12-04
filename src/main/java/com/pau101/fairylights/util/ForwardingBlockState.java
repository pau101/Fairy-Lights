package com.pau101.fairylights.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ForwardingBlockState implements IBlockState {
	private final IBlockState state;

	public ForwardingBlockState(IBlockState state) {
		this.state = Objects.requireNonNull(state, "state");
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, int id, int param) {
		return state.onBlockEventReceived(world, pos, id, param);
	}

	@Override
	public void neighborChanged(World world, BlockPos pos, Block block, BlockPos neighborPos) {
		state.neighborChanged(world, pos, block, neighborPos);
	}

	@Override
	public Material getMaterial() {
		return state.getMaterial();
	}

	@Override
	public boolean isFullBlock() {
		return state.isFullBlock();
	}

	@Override
	public boolean canEntitySpawn(Entity entity) {
		return state.canEntitySpawn(entity);
	}

	@Override
	public int getLightOpacity() {
		return state.getLightOpacity();
	}

	@Override
	public int getLightOpacity(IBlockAccess world, BlockPos pos) {
		return state.getLightOpacity(world, pos);
	}

	@Override
	public int getLightValue() {
		return state.getLightValue();
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		return state.getLightValue(world, pos);
	}

	@Override
	public boolean isTranslucent() {
		return state.isTranslucent();
	}

	@Override
	public boolean useNeighborBrightness() {
		return state.useNeighborBrightness();
	}

	@Override
	public MapColor getMapColor() {
		return state.getMapColor();
	}

	@Override
	public IBlockState withRotation(Rotation rot) {
		return state.withRotation(rot);
	}

	@Override
	public IBlockState withMirror(Mirror mirror) {
		return state.withMirror(mirror);
	}

	@Override
	public boolean isFullCube() {
		return state.isFullBlock();
	}

	@Override
	public EnumBlockRenderType getRenderType() {
		return state.getRenderType();
	}

	@Override
	public int getPackedLightmapCoords(IBlockAccess world, BlockPos pos) {
		return state.getPackedLightmapCoords(world, pos);
	}

	@Override
	public float getAmbientOcclusionLightValue() {
		return state.getAmbientOcclusionLightValue();
	}

	@Override
	public boolean isBlockNormalCube() {
		return state.isBlockNormalCube();
	}

	@Override
	public boolean isNormalCube() {
		return state.isNormalCube();
	}

	@Override
	public boolean canProvidePower() {
		return state.canProvidePower();
	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getWeakPower(world, pos, side);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return state.hasComparatorInputOverride();
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		return state.getComparatorInputOverride(world, pos);
	}

	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		return state.getBlockHardness(world, pos);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
		return state.getPlayerRelativeBlockHardness(player, world, pos);
	}

	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getStrongPower(world, pos, side);
	}

	@Override
	public EnumPushReaction getMobilityFlag() {
		return state.getMobilityFlag();
	}

	@Override
	public IBlockState getActualState(IBlockAccess world, BlockPos pos) {
		return state.getActualState(world, pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		return state.getSelectedBoundingBox(world, pos);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return state.shouldSideBeRendered(world, pos, facing);
	}

	@Override
	public boolean isOpaqueCube() {
		return state.isOpaqueCube();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockAccess world, BlockPos pos) {
		return state.getCollisionBoundingBox(world, pos);
	}

	@Override
	public void addCollisionBoxToList(World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> boxes, Entity entity) {
		state.addCollisionBoxToList(world, pos, aabb, boxes, entity);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
		return state.getBoundingBox(world, pos);
	}

	@Override
	public RayTraceResult collisionRayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		return state.collisionRayTrace(world, pos, start, end);
	}

	@Override
	public boolean isFullyOpaque() {
		return state.isFullyOpaque();
	}

	@Override
	public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.doesSideBlockRendering(world, pos, side);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.isSideSolid(world, pos, side);
	}

	@Override
	public Collection<IProperty<?>> getPropertyNames() {
		return state.getPropertyNames();
	}

	@Override
	public <T extends Comparable<T>> T getValue(IProperty<T> property) {
		return state.getValue(property);
	}

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return state.withProperty(property, value);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return state.cycleProperty(property);
	}

	@Override
	public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
		return state.getProperties();
	}

	@Override
	public Block getBlock() {
		return state.getBlock();
	}

	@Override
	public String toString() {
		return state.toString();
	}

	@Override
	public boolean func_191057_i() {
		return state.func_191057_i();
	}

	@Override
	public Vec3d func_191059_e(IBlockAccess world, BlockPos pos) {
		return state.func_191059_e(world, pos);
	}

	@Override
	public boolean func_191058_s() {
		return state.func_191058_s();
	}
}
