package com.pau101.fairylights.server.item;

import com.google.common.base.Objects;
import com.pau101.fairylights.FairyLights;
import com.pau101.fairylights.server.block.BlockFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;
import com.pau101.fairylights.server.entity.EntityFenceFastener;
import com.pau101.fairylights.server.fastener.Fastener;
import com.pau101.fairylights.server.fastener.connection.ConnectionType;
import com.pau101.fairylights.server.fastener.connection.type.Connection;
import com.pau101.fairylights.server.sound.FLSounds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public abstract class ItemConnection extends Item {
	public ItemConnection() {
		setMaxStackSize(16);
	}

	public abstract ConnectionType getConnectionType();

	@Override
	public EnumActionResult onItemUse(EntityPlayer user, World world, BlockPos pos, EnumHand hand, EnumFacing side, float facing, float hitX, float hitY) {
		ItemStack stack = user.getHeldItem(hand);
		if (isConnectionInOtherHand(world, user, stack)) {
			return EnumActionResult.PASS;
		}
		if (user.canPlayerEdit(pos, side, stack) && world.getBlockState(pos).getBlock() == FairyLights.fastener) {
			if (!world.isRemote) {
				connect(stack, user, world, pos);
			}
			return EnumActionResult.SUCCESS;
		} else if (FairyLights.fastener.canPlaceBlockOnSide(world, pos, side)) {
			pos = pos.offset(side);
			if (user.canPlayerEdit(pos, side, stack)) {
				if (FairyLights.fastener.canPlaceBlockAt(world, pos)) {
					if (!world.isRemote) {
						connect(stack, user, world, pos, side);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		} else if (user.canPlayerEdit(pos, side, stack) && isFence(world.getBlockState(pos), world.getTileEntity(pos))) {
			EntityHanging entity = EntityFenceFastener.findHanging(world, pos);
			if (entity == null || entity instanceof EntityFenceFastener) {
				if (!world.isRemote) {
					connectFence(stack, user, world, pos, (EntityFenceFastener) entity);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	private boolean isConnectionInOtherHand(World world, EntityPlayer user, ItemStack stack) {
		Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP, null);
		Connection connection = attacher.getFirstConnection();
		if (connection != null) {
			NBTTagCompound nbt = connection.serializeLogic();
			if (nbt.hasNoTags()) {
				return stack.hasTagCompound();
			}
			return !NBTUtil.areNBTEquals(nbt, stack.getTagCompound(), true);
		}
		return false;
	}

	private void connect(ItemStack stack, EntityPlayer user, World world, BlockPos pos) {
		Fastener fastener = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null);
		connect(stack, user, world, fastener);
	}

	private void connect(ItemStack stack, EntityPlayer user, World world, BlockPos pos, EnumFacing facing) {
		IBlockState state =  FairyLights.fastener.getDefaultState().withProperty(BlockFastener.FACING, facing);
		if (world.setBlockState(pos, state, 3)) {
			FairyLights.fastener.onBlockPlacedBy(world, pos, state, user, stack);
			SoundType sound = FairyLights.fastener.getSoundType(state, world, pos, user);
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				sound.getPlaceSound(),
				SoundCategory.BLOCKS,
				(sound.getVolume() + 1) / 2,
				sound.getPitch() * 0.8F
			);
			Fastener destination = world.getTileEntity(pos).getCapability(CapabilityHandler.FASTENER_CAP, null);
			connect(stack, user, world, destination, false);
		}
	}

	public void connect(ItemStack stack, EntityPlayer user, World world, Fastener fastener) {
		connect(stack, user, world, fastener, true);
	}

	public void connect(ItemStack stack, EntityPlayer user, World world, Fastener fastener, boolean playConnectSound) {
		Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP, null);
		Connection conn = attacher.getFirstConnection();
		if (conn == null) {
			NBTTagCompound data = Objects.firstNonNull(stack.getTagCompound(), new NBTTagCompound());
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
			world.playSound(null, pos.xCoord, pos.yCoord, pos.zCoord, FLSounds.CORD_CONNECT, SoundCategory.BLOCKS, 1, 1);
		}
	}

	private void connectFence(ItemStack stack, EntityPlayer user, World world, BlockPos pos, EntityFenceFastener fastener) {
		boolean playConnectSound;
		if (fastener == null) {
			fastener = EntityFenceFastener.create(world, pos);
			playConnectSound = false;
		} else {
			playConnectSound = true;
		}
		connect(stack, user, world, fastener.getCapability(CapabilityHandler.FASTENER_CAP, null), playConnectSound);
	}

	public static boolean isFence(IBlockState state, TileEntity entity) {
		Block block = state.getBlock();
		if (block instanceof BlockFence) {
			return true;
		}
		if (!state.getMaterial().isSolid()) {
			return false;
		}
		AxisAlignedBB bounds = null;
		try {
			bounds = block.getDefaultState().getCollisionBoundingBox(new IsolatedBlock(state, entity), IsolatedBlock.POS);
		} catch (Exception e) {
			// Safeguard against theoretical special cases
		}
		// Check if x/z bounds are within in a centered 5x5 square.
		return bounds != null && bounds.minX > 0.34375F && bounds.minZ > 0.34375F && bounds.maxX < 0.65625F && bounds.maxZ < 0.65625F;
	}

	private static class IsolatedBlock implements IBlockAccess {
		public static final BlockPos POS = BlockPos.ORIGIN;

		private final IBlockState state;

		private final TileEntity entity;

		public IsolatedBlock(IBlockState state, TileEntity entity) {
			this.state = state;
			this.entity = entity;
		}

		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			return POS.equals(pos) ? entity : null;
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return 0;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			return POS.equals(pos) ? state : Blocks.AIR.getDefaultState();
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return !POS.equals(pos) || state.getBlock().isAir(state, this, pos);
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return Biomes.DEFAULT;
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return WorldType.CUSTOMIZED;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
			return POS.equals(pos) && state.isSideSolid(this, pos, side);
		}
	}
}
