package me.paulf.fairylights.server.item;

import com.google.common.base.MoreObjects;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.ConnectionType;
import me.paulf.fairylights.server.fastener.connection.type.Connection;
import me.paulf.fairylights.server.sound.FLSounds;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ConnectionItem extends Item {
    public ConnectionItem(final Properties properties) {
        super(properties);
    }

    public abstract ConnectionType getConnectionType();

    @Override
    public ActionResultType onItemUse(final ItemUseContext context) {
        final PlayerEntity user = context.getPlayer();
        if (user == null) {
            return super.onItemUse(context);
        }
        final World world = context.getWorld();
        final Direction side = context.getFace();
        final BlockPos clickPos = context.getPos();
        final Block fastener = FLBlocks.FASTENER.get();
        final ItemStack stack = context.getItem();
        if (this.isConnectionInOtherHand(world, user, stack)) {
            return ActionResultType.PASS;
        }
        final BlockState fastenerState = fastener.getDefaultState().with(FastenerBlock.FACING, side);
        final BlockState currentBlockState = world.getBlockState(clickPos);
        final BlockItemUseContext blockContext = new BlockItemUseContext(context);
        final BlockPos placePos = blockContext.getPos();
        if (currentBlockState.getBlock() == fastener) {
            if (!world.isRemote) {
                this.connect(stack, user, world, clickPos);
            }
            return ActionResultType.SUCCESS;
        } else if (blockContext.canPlace() && fastenerState.isValidPosition(world, placePos)) {
            if (!world.isRemote) {
                this.connect(stack, user, world, placePos, fastenerState);
            }
            return ActionResultType.SUCCESS;
        } else if (isFence(currentBlockState)) {
            final HangingEntity entity = FenceFastenerEntity.findHanging(world, clickPos);
            if (entity == null || entity instanceof FenceFastenerEntity) {
                if (!world.isRemote) {
                    this.connectFence(stack, user, world, clickPos, (FenceFastenerEntity) entity);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    private boolean isConnectionInOtherHand(final World world, final PlayerEntity user, final ItemStack stack) {
        final Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
        final Connection connection = attacher.getFirstConnection();
        if (connection != null) {
            final CompoundNBT nbt = connection.serializeLogic();
            if (nbt.isEmpty()) {
                return stack.hasTag();
            }
            return !NBTUtil.areNBTEquals(nbt, stack.getTag(), true);
        }
        return false;
    }

    private void connect(final ItemStack stack, final PlayerEntity user, final World world, final BlockPos pos) {
        final TileEntity entity = world.getTileEntity(pos);
        if (entity != null) {
            entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> this.connect(stack, user, world, fastener));
        }
    }

    private void connect(final ItemStack stack, final PlayerEntity user, final World world, final BlockPos pos, final BlockState state) {
        if (world.setBlockState(pos, state, 3)) {
            state.getBlock().onBlockPlacedBy(world, pos, state, user, stack);
            final SoundType sound = state.getBlock().getSoundType(state, world, pos, user);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                sound.getPlaceSound(),
                SoundCategory.BLOCKS,
                (sound.getVolume() + 1) / 2,
                sound.getPitch() * 0.8F
            );
            final TileEntity entity = world.getTileEntity(pos);
            if (entity != null) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(destination -> this.connect(stack, user, world, destination, false));
            }
        }
    }

    public void connect(final ItemStack stack, final PlayerEntity user, final World world, final Fastener fastener) {
        this.connect(stack, user, world, fastener, true);
    }

    public void connect(final ItemStack stack, final PlayerEntity user, final World world, final Fastener<?> fastener, final boolean playConnectSound) {
        user.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(attacher -> {
            boolean playSound = playConnectSound;
            final Connection conn = attacher.getFirstConnection();
            if (conn == null) {
                final CompoundNBT data = MoreObjects.firstNonNull(stack.getTag(), new CompoundNBT());
                fastener.connectWith(world, attacher, this.getConnectionType(), data);
            } else if (conn.getDestination().isLoaded(world)) {
                final Connection c = conn.getDestination().get(world).reconnect(attacher, fastener);
                if (c == null) {
                    playSound = false;
                } else {
                    c.onConnect(world, user, stack);
                    stack.shrink(1);
                }
            }
            if (playSound) {
                final Vec3d pos = fastener.getConnectionPoint();
                world.playSound(null, pos.x, pos.y, pos.z, FLSounds.CORD_CONNECT.get(), SoundCategory.BLOCKS, 1, 1);
            }
        });
    }

    private void connectFence(final ItemStack stack, final PlayerEntity user, final World world, final BlockPos pos, FenceFastenerEntity fastener) {
        final boolean playConnectSound;
        if (fastener == null) {
            fastener = FenceFastenerEntity.create(world, pos);
            playConnectSound = false;
        } else {
            playConnectSound = true;
        }
        this.connect(stack, user, world, fastener.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new), playConnectSound);
    }

    public static boolean isFence(final BlockState state) {
        return state.getMaterial().isSolid() && state.isIn(BlockTags.FENCES);
    }
}
