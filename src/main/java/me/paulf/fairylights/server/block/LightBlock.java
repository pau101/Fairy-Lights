package me.paulf.fairylights.server.block;

import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.item.LightItem;
import me.paulf.fairylights.server.item.LightVariant;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LightBlock extends HorizontalFaceBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final VoxelShape shape;// = Block.makeCuboidShape(2, 2, 2, 14, 14, 14);

    private final LightVariant variant;

    public LightBlock(final Properties properties, final LightVariant variant) {
        super(properties);
        this.variant = variant;
        this.shape = VoxelShapes.create(0.5F - variant.getWidth() * 0.5F, 0.0F, 0.5F - variant.getWidth() * 0.5F, 0.5F + variant.getWidth() * 0.5F, variant.getHeight(), 0.5F + variant.getWidth() * 0.5F);
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
        return state.get(FACE) == AttachFace.FLOOR ? func_220055_a(world, pos.down(), Direction.UP) : super.isValidPosition(state, world, pos);
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
    public boolean onBlockActivated(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        final TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).interact(world, pos, state, player, hand, hit);
            return true;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
        final float w = this.variant.getWidth();
        final float h = this.variant.getHeight();
        switch (state.get(FACE)) {
            default:
                return this.shape;
            case WALL:
                switch (this.variant.getPlacement()) {
                    default:
                        return this.shape;
                    case ONWARD:
                    case OUTWARD:
                        switch (state.get(HORIZONTAL_FACING)) {
                            case EAST:
                                return VoxelShapes.create(
                                    0.0D, 0.5D - w * 0.5D, 0.5D - w * 0.5D,
                                    h, 0.5D + w * 0.5D, 0.5D + w * 0.5D
                                );
                            case WEST:
                                return VoxelShapes.create(
                                    1.0D - h, 0.5D - w * 0.5D, 0.5D - w * 0.5D,
                                    1.0D, 0.5D + w * 0.5D, 0.5D + w * 0.5D
                                );
                            case SOUTH:
                                return VoxelShapes.create(
                                    0.5D - w * 0.5D, 0.5D - w * 0.5D, 0.0D,
                                    0.5D + w * 0.5D, 0.5D + w * 0.5D, h
                                );
                            case NORTH:
                                return VoxelShapes.create(
                                    0.5D - w * 0.5D, 0.5D - w * 0.5D, 1.0D - h,
                                    0.5D + w * 0.5D, 0.5D + w * 0.5D, 1.0D
                                );
                        }
                }
            case CEILING:
                return this.shape.withOffset(0.0D, 1.0D - this.variant.getHeight(), 0.0D);
        }
    }

    @Override
    public ItemStack getItem(final IBlockReader world, final BlockPos pos, final BlockState state) {
        final TileEntity entity = world.getTileEntity(pos);
        final ItemStack stack = new ItemStack(this.variant.getItem());
        stack.getOrCreateTag().putByte("color", (byte) (entity instanceof LightBlockEntity ? ((LightBlockEntity) entity).getColor() : DyeColor.YELLOW).getId());
        return stack;
    }

    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACE, HORIZONTAL_FACING, LIT);
    }
}
