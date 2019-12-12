package com.pau101.fairylights.server.item;

import com.google.common.base.MoreObjects;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.block.FLBlocks;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.sound.FLSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ItemConnection extends Item {
	public ItemConnection(Properties properties) {
		super(properties);
	}

	public abstract ConnectionType getConnectionType();

	@Override
	public ActionResultType onItemUse(final ItemUseContext context) {
		PlayerEntity user = context.getPlayer();
		if (user == null) {
			return super.onItemUse(context);
		}
		World world = context.getWorld();
		Direction side = context.getFace();
		BlockPos clickPos = context.getPos();
		Block fastener = FLBlocks.FASTENER.orElseThrow(IllegalStateException::new);
		ItemStack stack = context.getItem();
		if (isConnectionInOtherHand(world, user, stack)) {
			return ActionResultType.PASS;
		}
		BlockState fastenerState = fastener.getDefaultState().with(BlockFastener.FACING, side);
		BlockState currentBlockState = world.getBlockState(clickPos);
		BlockItemUseContext blockContext = new BlockItemUseContext(context);
		BlockPos placePos = blockContext.getPos();
		if (currentBlockState.getBlock() == fastener) {
			if (!world.isRemote) {
				connect(stack, user, world, clickPos);
			}
			return ActionResultType.SUCCESS;
		} else if (blockContext.canPlace() && fastenerState.isValidPosition(world, placePos)) {
			if (!world.isRemote) {
				connect(stack, user, world, placePos, fastenerState);
			}
			return ActionResultType.SUCCESS;
		} else if (isFence(currentBlockState)) {
			HangingEntity entity = EntityFenceFastener.findHanging(world, clickPos);
			if (entity == null || entity instanceof EntityFenceFastener) {
				if (!world.isRemote) {
					connectFence(stack, user, world, clickPos, (EntityFenceFastener) entity);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	private boolean isConnectionInOtherHand(World world, PlayerEntity user, ItemStack stack) {
		Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
		Connection connection = attacher.getFirstConnection();
		if (connection != null) {
			CompoundNBT nbt = connection.serializeLogic();
			if (nbt.isEmpty()) {
				return stack.hasTag();
			}
			return !NBTUtil.areNBTEquals(nbt, stack.getTag(), true);
		}
		return false;
	}

	private void connect(ItemStack stack, PlayerEntity user, World world, BlockPos pos) {
		// FIXME
		Fastener fastener = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
		connect(stack, user, world, fastener);
	}

	private void connect(ItemStack stack, PlayerEntity user, World world, BlockPos pos, BlockState state) {
		if (world.setBlockState(pos, state, 3)) {
			state.getBlock().onBlockPlacedBy(world, pos, state, user, stack);
			SoundType sound = state.getBlock().getSoundType(state, world, pos, user);
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				sound.getPlaceSound(),
				SoundCategory.BLOCKS,
				(sound.getVolume() + 1) / 2,
				sound.getPitch() * 0.8F
			);
			// FIXME
			Fastener destination = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
			connect(stack, user, world, destination, false);
		}
	}

	public void connect(ItemStack stack, PlayerEntity user, World world, Fastener fastener) {
		connect(stack, user, world, fastener, true);
	}

	public void connect(ItemStack stack, PlayerEntity user, World world, Fastener<?> fastener, boolean playConnectSound) {
		// FIXME
		Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
		Connection conn = attacher.getFirstConnection();
		if (conn == null) {
			CompoundNBT data = MoreObjects.firstNonNull(stack.getTag(), new CompoundNBT());
			fastener.connectWith(world, attacher, getConnectionType(), data);
		} else if (conn.getDestination().isLoaded(world)) {
			Connection c = conn.getDestination().get(world).reconnect(attacher, fastener);
			if (c == null) {
				playConnectSound = false;
			} else {
				c.onConnect(world, user, stack);
				stack.shrink(1);
			}
		}
		if (playConnectSound) {
			Vec3d pos = fastener.getConnectionPoint();
			world.playSound(null, pos.x, pos.y, pos.z, FLSounds.CORD_CONNECT.orElseThrow(IllegalStateException::new), SoundCategory.BLOCKS, 1, 1);
		}
	}

	private void connectFence(ItemStack stack, PlayerEntity user, World world, BlockPos pos, EntityFenceFastener fastener) {
		boolean playConnectSound;
		if (fastener == null) {
			fastener = EntityFenceFastener.create(world, pos);
			playConnectSound = false;
		} else {
			playConnectSound = true;
		}
		connect(stack, user, world, fastener.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new), playConnectSound);
	}

	public static boolean isFence(BlockState state) {
		return state.getMaterial().isSolid() && state.isIn(BlockTags.FENCES);
	}
}
