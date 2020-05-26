package me.paulf.fairylights.server.block;

import me.paulf.fairylights.server.block.entity.*;
import me.paulf.fairylights.server.item.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

import javax.annotation.*;

public class LightBlock extends HorizontalFaceBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape MIN_FLOOR_ANCHOR_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

    private final VoxelShape floorShape, eastWallShape, westWallShape, northWallShape, southWallShape, ceilingShape;

    private final LightVariant variant;

    public LightBlock(final Properties properties, final LightVariant variant) {
        super(properties);
        this.variant = variant;
        final float w = this.variant.getWidth();
        final float h = this.variant.getHeight();
        final double w0 = 0.5D - w * 0.5D;
        final double w1 = 0.5D + w * 0.5D;
        this.floorShape = VoxelShapes.create(w0, 0.0D, w0, w1, h, w1);
        if (this.variant.getPlacement() == LightVariant.Placement.UPRIGHT) {
            final double t = 0.125D;
            final double u = 0.775D;
            this.eastWallShape = VoxelShapes.create(w0 - t, u - h, w0, w1 - t, u, w1);
            this.westWallShape = VoxelShapes.create(w0 + t, u - h, w0, w1 + t, u, w1);
            this.southWallShape = VoxelShapes.create(w0, u - h, w0 - t, w1, u, w1 - t);
            this.northWallShape = VoxelShapes.create(w0, u - h, w0 + t, w1, u, w1 + t);
        } else {
            this.eastWallShape = VoxelShapes.create(0.0D, w0, w0, h, w1, w1);
            this.westWallShape = VoxelShapes.create(1.0D - h, w0, w0, 1.0D, w1, w1);
            this.southWallShape = VoxelShapes.create(w0, w0, 0.0D, w1, w1, h);
            this.northWallShape = VoxelShapes.create(w0, w0, 1.0D - h, w1, w1, 1.0D);
        }
        this.ceilingShape = VoxelShapes.create(w0, 1.0D - h, w0, w1, 1.0D, w1);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(FACE, AttachFace.WALL).with(LIT, true));
    }

    public LightVariant getVariant() {
        return this.variant;
    }

    @Override
    public int getLightValue(final BlockState state) {
        return state.get(LIT) ? super.getLightValue(state) : 0;
    }

    @Override
    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return new LightBlockEntity();
    }

    @Override
    public boolean isValidPosition(final BlockState state, final IWorldReader world, final BlockPos pos) {
        final Direction facing = HorizontalFaceBlock.getFacing(state);
        final BlockPos anchorPos = pos.offset(facing.getOpposite());
        final BlockState anchorState = world.getBlockState(anchorPos);
        final VoxelShape shape = anchorState.getCollisionShape(world, anchorPos);
        if (state.get(FACE) == AttachFace.FLOOR) {
            return !VoxelShapes.compare(shape.project(facing.getOpposite()), MIN_FLOOR_ANCHOR_SHAPE, IBooleanFunction.ONLY_SECOND);
        }
        return Block.doesSideFillSquare(shape, facing);
    }

    // 'super' but opposite facing for y axis placement
    @Nullable
    public BlockState getStateForPlacement(final BlockItemUseContext context) {
        for (final Direction dir : context.getNearestLookingDirections()) {
            final BlockState state;
            if (dir.getAxis() == Direction.Axis.Y) {
                state = this.getDefaultState()
                    .with(FACE, dir == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                    .with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
            } else {
                state = this.getDefaultState()
                    .with(FACE, AttachFace.WALL)
                    .with(HORIZONTAL_FACING, dir.getOpposite());
            }
            if (state.isValidPosition(context.getWorld(), context.getPos())) {
                return state;
            }
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        final DyeColor color = LightItem.getLightColor(stack);
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).setColor(color);
        }
    }

    @Override
    public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).interact(world, pos, state, player, hand, hit);
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
        switch (state.get(FACE)) {
            default:
            case FLOOR:
                return this.floorShape;
            case WALL:
                switch (state.get(HORIZONTAL_FACING)) {
                    default:
                    case EAST:
                        return this.eastWallShape;
                    case WEST:
                        return this.westWallShape;
                    case SOUTH:
                        return this.southWallShape;
                    case NORTH:
                        return this.northWallShape;
                }
            case CEILING:
                return this.ceilingShape;
        }
    }

    @Override
    public ItemStack getItem(final IBlockReader world, final BlockPos pos, final BlockState state) {
        final TileEntity entity = world.getTileEntity(pos);
        final ItemStack stack = new ItemStack(this.variant.getItem());
        LightItem.setLightColor(stack, entity instanceof LightBlockEntity ? ((LightBlockEntity) entity).getColor() : DyeColor.YELLOW);
        return stack;
    }

    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACE, HORIZONTAL_FACING, LIT);
    }
}
