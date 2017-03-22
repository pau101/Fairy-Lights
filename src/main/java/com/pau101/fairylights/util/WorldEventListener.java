package com.pau101.fairylights.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public interface WorldEventListener extends IWorldEventListener {
	@Override
	default void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {}

	@Override
	default void notifyLightSet(BlockPos pos) {}

	@Override
	default void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}

	@Override
	default void playSoundToAllNearExcept(EntityPlayer player, SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

	@Override
	default void playRecord(SoundEvent sound, BlockPos pos) {}

	@Override
	default void spawnParticle(int particleID, boolean ignoreRange, double x, double y, double z, double dx, double dy, double dz, int... parameters) {}

	@Override
	default void spawnParticle(int particleID, boolean ignoreRange, boolean respectMinimalParticles, double x, double y, double z, double dx, double dy, double dz, int... parameters) {}

	@Override
	default void onEntityAdded(Entity entity) {}

	@Override
	default void onEntityRemoved(Entity entity) {}

	@Override
	default void broadcastSound(int soundID, BlockPos pos, int data) {}

	@Override
	default void playEvent(EntityPlayer player, int type, BlockPos pos, int data) {}

	@Override
	default void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}
