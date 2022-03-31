package me.paulf.fairylights.server.block;

import me.paulf.fairylights.server.ServerEventHandler;
import me.paulf.fairylights.server.block.entity.FastenerBlockEntity;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.connection.HangingLightsConnection;
import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.paulf.fairylights.server.jingle.JingleLibrary;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public final class FastenerBlock extends DirectionalBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.field_208197_x;

    private static final VoxelShape NORTH_AABB = Block.func_208617_a(6.0D, 6.0D, 12.0D, 10.0D, 10.0D, 16.0D);

    private static final VoxelShape SOUTH_AABB = Block.func_208617_a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 4.0D);

    private static final VoxelShape WEST_AABB = Block.func_208617_a(12.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    private static final VoxelShape EAST_AABB = Block.func_208617_a(0.0D, 6.0D, 6.0D, 4.0D, 10.0D, 10.0D);

    private static final VoxelShape DOWN_AABB = Block.func_208617_a(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    private static final VoxelShape UP_AABB = Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D);

    public FastenerBlock(final Block.Properties properties) {
        super(properties);
        this.func_180632_j(this.field_176227_L.func_177621_b()
            .func_206870_a(field_176387_N, Direction.NORTH)
            .func_206870_a(TRIGGERED, false)
        );
    }

    @Override
    protected void func_206840_a(final StateContainer.Builder<Block, BlockState> builder) {
        builder.func_206894_a(field_176387_N, TRIGGERED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState func_185499_a(final BlockState state, final Rotation rot) {
        return state.func_206870_a(field_176387_N, rot.func_185831_a(state.func_177229_b(field_176387_N)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState func_185471_a(final BlockState state, final Mirror mirrorIn) {
        return state.func_206870_a(field_176387_N, mirrorIn.func_185803_b(state.func_177229_b(field_176387_N)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape func_220053_a(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context) {
        switch (state.func_177229_b(field_176387_N)) {
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
            case DOWN:
                return DOWN_AABB;
            case UP:
            default:
                return UP_AABB;
        }
    }

    @Override
    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return new FastenerBlockEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void func_196243_a(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean isMoving) {
        if (state.func_177230_c() != newState.func_177230_c()) {
            final TileEntity entity = world.func_175625_s(pos);
            if (entity instanceof FastenerBlockEntity) {
                entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(f -> f.dropItems(world, pos));
            }
            super.func_196243_a(state, world, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean func_196260_a(final BlockState state, final IWorldReader world, final BlockPos pos) {
        final Direction facing = state.func_177229_b(field_176387_N);
        final BlockPos attachedPos = pos.func_177972_a(facing.func_176734_d());
        final BlockState attachedState = world.func_180495_p(attachedPos);
        return attachedState.func_177230_c().func_203417_a(BlockTags.field_206952_E) || attachedState.func_224755_d(world, attachedPos, facing) || facing == Direction.UP && attachedState.func_235714_a_(BlockTags.field_219757_z);
    }

    @Nullable
    @Override
    public BlockState func_196258_a(final BlockItemUseContext context) {
        BlockState result = this.func_176223_P();
        final IWorldReader world = context.func_195991_k();
        final BlockPos pos = context.func_195995_a();
        for (final Direction dir : context.func_196009_e()) {
            result = result.func_206870_a(field_176387_N, dir.func_176734_d());
            if (result.func_196955_c(world, pos)) {
                return result;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState func_196271_a(final BlockState state, final Direction facing, final BlockState facingState, final IWorld world, final BlockPos currentPos, final BlockPos facingPos) {
        if (facing.func_176734_d() == state.func_177229_b(field_176387_N) && !state.func_196955_c(world, currentPos)) {
            return Blocks.field_150350_a.func_176223_P();
        }
        return super.func_196271_a(state, facing, facingState, world, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void func_220082_b(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean isMoving) {
        if (oldState.func_177230_c() != state.func_177230_c()) {
            if (world.func_175640_z(pos.func_177972_a(state.func_177229_b(field_176387_N).func_176734_d()))) {
                world.func_180501_a(pos, state.func_206870_a(TRIGGERED, true), 3);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void func_220069_a(final BlockState state, final World world, final BlockPos pos, final Block blockIn, final BlockPos fromPos, final boolean isMoving) {
        if (state.func_196955_c(world, pos)) {
            final boolean receivingPower = world.func_175640_z(pos);
            final boolean isPowered = state.func_177229_b(TRIGGERED);
            if (receivingPower && !isPowered) {
                world.func_205220_G_().func_205360_a(pos, this, 2);
                world.func_180501_a(pos, state.func_206870_a(TRIGGERED, true), 4);
            } else if (!receivingPower && isPowered) {
                world.func_180501_a(pos, state.func_206870_a(TRIGGERED, false), 4);
            }
        } else {
            final TileEntity entity = world.func_175625_s(pos);
            func_220059_a(state, world, pos, entity);
            world.func_217377_a(pos, false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean func_149740_M(final BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int func_180641_l(final BlockState state, final World world, final BlockPos pos) {
        final TileEntity entity = world.func_175625_s(pos);
        if (entity == null) return super.func_180641_l(state, world, pos);
        return entity.getCapability(CapabilityHandler.FASTENER_CAP).map(f -> f.getAllConnections().stream()).orElse(Stream.empty())
            .filter(HangingLightsConnection.class::isInstance)
            .map(HangingLightsConnection.class::cast)
            .mapToInt(c -> (int) Math.ceil(c.getJingleProgress() * 15))
            .max().orElse(0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void func_225534_a_(final BlockState state, final ServerWorld world, final BlockPos pos, final Random random) {
        this.jingle(world, pos);
    }

    private void jingle(final World world, final BlockPos pos) {
        final TileEntity entity = world.func_175625_s(pos);
        if (!(entity instanceof FastenerBlockEntity)) {
            return;
        }
        entity.getCapability(CapabilityHandler.FASTENER_CAP).ifPresent(fastener -> fastener.getAllConnections().stream()
            .filter(HangingLightsConnection.class::isInstance)
            .map(HangingLightsConnection.class::cast)
            .filter(conn -> conn.canCurrentlyPlayAJingle() && conn.isDestination(new BlockFastenerAccessor(fastener.getPos())) && world.func_180495_p(fastener.getPos()).func_177229_b(TRIGGERED))
            .findFirst().ifPresent(conn -> ServerEventHandler.tryJingle(world, conn, JingleLibrary.RANDOM))
        );
    }

    public Vector3d getOffset(final Direction facing, final float offset) {
        return getFastenerOffset(facing, offset);
    }

    public static Vector3d getFastenerOffset(final Direction facing, final float offset) {
        double x = offset, y = offset, z = offset;
        switch (facing) {
            case DOWN:
                y += 0.75F;
            case UP:
                x += 0.375F;
                z += 0.375F;
                break;
            case WEST:
                x += 0.75F;
            case EAST:
                z += 0.375F;
                y += 0.375F;
                break;
            case NORTH:
                z += 0.75F;
            case SOUTH:
                x += 0.375F;
                y += 0.375F;
        }
        return new Vector3d(x, y, z);
    }
}
