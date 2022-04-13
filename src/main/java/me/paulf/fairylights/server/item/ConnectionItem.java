package me.paulf.fairylights.server.item;

import java.util.Optional;

import com.mojang.math.Vector3d;

import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.RegistryObject;

public abstract class ConnectionItem extends Item {
    private final RegistryObject<? extends ConnectionType<?>> type;

    public ConnectionItem(final Properties properties, final RegistryObject<? extends ConnectionType<?>> type) {
        super(properties);
        this.type = type;
    }

    public final ConnectionType<?> getConnectionType() {
        return (ConnectionType<?>) this.type.get();
    }
    
    /** useOn
     * 
     * When a connection item is used on a block, we need to spawn a new fastener block entity.
     */
    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Player user = context.getPlayer();
        if (user == null) {
            return super.useOn(context);
        }
        final Level world = context.getLevel();
        final Direction side = context.getClickedFace();
        final BlockPos clickPos = context.getClickedPos();
        final Block fastener = FLBlocks.FASTENER.get();
        final ItemStack stack = context.getItemInHand();
        if (this.isConnectionInOtherHand(world, user, stack)) {
            return InteractionResult.PASS;
        }
        final BlockState fastenerState = fastener.func_176223_P().func_206870_a(FastenerBlock.field_176387_N, side);
        final BlockState currentBlockState = world.func_180495_p(clickPos);
        final BlockItemUseContext blockContext = new BlockItemUseContext(context);
        final BlockPos placePos = blockContext.func_195995_a();
        if (currentBlockState.func_177230_c() == fastener) {
            if (!world.field_72995_K) {
                this.connect(stack, user, world, clickPos);
            }
            return InteractionResult.SUCCESS;
        } else if (blockContext.func_196011_b() && fastenerState.func_196955_c(world, placePos)) {
            if (!world.field_72995_K) {
                this.connect(stack, user, world, placePos, fastenerState);
            }
            return InteractionResult.SUCCESS;
        } else if (isFence(currentBlockState)) {
            final HangingEntity entity = FenceFastenerEntity.findHanging(world, clickPos);
            if (entity == null || entity instanceof FenceFastenerEntity) {
                if (!world.field_72995_K) {
                    this.connectFence(stack, user, world, clickPos, (FenceFastenerEntity) entity);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean isConnectionInOtherHand(final Level world, final PlayerEntity user, final ItemStack stack) {
        final Fastener<?> attacher = user.getCapability(CapabilityHandler.FASTENER_CAP).orElseThrow(IllegalStateException::new);
        return attacher.getFirstConnection().filter(connection -> {
            final TextComponent nbt = connection.serializeLogic();
            return nbt.isEmpty() ? stack.func_77942_o() : !NBTUtil.func_181123_a(nbt, stack.func_77978_p(), true);
        }).isPresent();
    }

    private void connect(final ItemStack stack, final Player user, final Level world, final BlockPos pos) {
        final TileEntity entity = world.func_175625_s(pos);
        if (entity != null) {
            entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> this.connect(stack, user, world, fastener));
        }
    }

    private void connect(final ItemStack stack, final Player user, final Level world, final BlockPos pos, final BlockState state) {
        if (world.func_180501_a(pos, state, 3)) {
            state.func_177230_c().func_180633_a(world, pos, state, user, stack);
            final SoundType sound = state.func_177230_c().getSoundType(state, world, pos, user);
            world.func_184148_a(null, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5,
                sound.func_185841_e(),
                SoundCategory.BLOCKS,
                (sound.func_185843_a() + 1) / 2,
                sound.func_185847_b() * 0.8F
            );
            final TileEntity entity = world.func_175625_s(pos);
            if (entity != null) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(destination -> this.connect(stack, user, world, destination, false));
            }
        }
    }

    public void connect(final ItemStack stack, final Player user, final Level world, final Fastener<?> fastener) {
        this.connect(stack, user, world, fastener, true);
    }

    public void connect(final ItemStack stack, final Player user, final Level world, final Fastener<?> fastener, final boolean playConnectSound) {
        user.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(attacher -> {
            boolean playSound = playConnectSound;
            final Optional<Connection> placing = attacher.getFirstConnection();
            if (placing.isPresent()) {
                final Connection conn = placing.get();
                if (conn.reconnect(fastener)) {
                    conn.onConnect(world, user, stack);
                    stack.func_190918_g(1);
                } else {
                    playSound = false;
                }
            } else {
                final TextComponent data = stack.func_77978_p();
                fastener.connect(world, attacher, this.getConnectionType(), data == null ? new TextComponent() : data, false);
            }
            if (playSound) {
                final Vector3d pos = fastener.getConnectionPoint();
                world.func_184148_a(null, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, FLSounds.CORD_CONNECT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        });
    }

    private void connectFence(final ItemStack stack, final Player user, final Level world, final BlockPos pos, FenceFastenerEntity fastener) {
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
        return state.func_185904_a().func_76220_a() && state.func_235714_a_(BlockTags.field_219748_G);
    }
}
