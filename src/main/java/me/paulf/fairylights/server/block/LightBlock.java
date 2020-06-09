package me.paulf.fairylights.server.block;

import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.item.ColorLightItem;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LightBlock extends HorizontalFaceBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape MIN_ANCHOR_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

    private final VoxelShape floorShape, eastWallShape, westWallShape, northWallShape, southWallShape, ceilingShape;

    private final LightVariant<?> variant;

    public LightBlock(final Properties properties, final LightVariant<?> variant) {
        super(properties.noDrops());
        this.variant = variant;
        final AxisAlignedBB bb = variant == SimpleLightVariant.FAIRY ? this.variant.getBounds().grow(0.044D) : this.variant.getBounds();
        final double w = Math.max(bb.getXSize(), bb.getZSize());
        final double w0 = 0.5D - w * 0.5D;
        final double w1 = 0.5D + w * 0.5D;
        if (variant.isOrientable()) {
            this.floorShape = VoxelShapes.create(w0, 0.0D, w0, w1, -bb.minY, w1);
            this.eastWallShape = VoxelShapes.create(0.0D, w0, w0, -bb.minY, w1, w1);
            this.westWallShape = VoxelShapes.create(1.0D + bb.minY, w0, w0, 1.0D, w1, w1);
            this.southWallShape = VoxelShapes.create(w0, w0, 0.0D, w1, w1, -bb.minY);
            this.northWallShape = VoxelShapes.create(w0, w0, 1.0D + bb.minY, w1, w1, 1.0D);
            this.ceilingShape = VoxelShapes.create(w0, 1.0D + bb.minY, w0, w1, 1.0D, w1);
        } else {
            final double t = 0.125D;
            final double u = 0.65D;
            this.floorShape = VoxelShapes.create(w0, 0.0D, w0, w1, bb.getYSize(), w1);
            this.eastWallShape = VoxelShapes.create(w0 - t, u + bb.minY, w0, w1 - t, u + bb.maxY, w1);
            this.westWallShape = VoxelShapes.create(w0 + t, u + bb.minY, w0, w1 + t, u + bb.maxY, w1);
            this.southWallShape = VoxelShapes.create(w0, u + bb.minY, w0 - t, w1, u + bb.maxY, w1 - t);
            this.northWallShape = VoxelShapes.create(w0, u  + bb.minY, w0 + t, w1, u + bb.maxY, w1 + t);
            this.ceilingShape = VoxelShapes.create(w0, 1.0D + bb.minY - 0.25D, w0, w1, 1.0D, w1);
        }
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(FACE, AttachFace.WALL).with(LIT, true));
    }

    public LightVariant<?> getVariant() {
        return this.variant;
    }

    @SuppressWarnings("deprecation")
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
        final VoxelShape shape = world.getBlockState(anchorPos).getCollisionShape(world, anchorPos);
        if (state.get(FACE) != AttachFace.WALL) {
            return !VoxelShapes.compare(shape.project(facing), MIN_ANCHOR_SHAPE, IBooleanFunction.ONLY_SECOND);
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
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            final ItemStack lightItem = stack.copy();
            lightItem.setCount(1);
            ((LightBlockEntity) entity).setItemStack(lightItem);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(final BlockState state, final LootContext.Builder builder) {
        final TileEntity entity = builder.get(LootParameters.BLOCK_ENTITY);
        if (entity instanceof LightBlockEntity) {
            return Collections.singletonList(((LightBlockEntity) entity).getLight().getItem().copy());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).interact(world, pos, state, player, hand, hit);
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(final BlockState state, final World world, final BlockPos pos, final Random rng) {
        super.animateTick(state, world, pos, rng);
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).animateTick();
        }
    }

    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItem(final IBlockReader world, final BlockPos pos, final BlockState state) {
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            return ((LightBlockEntity) entity).getLight().getItem().copy();
        }
        final ItemStack stack = new ItemStack(this);
        ColorLightItem.setColor(stack, DyeColor.YELLOW);
        return stack;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACE, HORIZONTAL_FACING, LIT);
    }
}
