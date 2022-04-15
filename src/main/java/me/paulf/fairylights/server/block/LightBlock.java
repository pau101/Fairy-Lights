package me.paulf.fairylights.server.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import me.paulf.fairylights.server.block.entity.LightBlockEntity;
import me.paulf.fairylights.server.item.DyeableItem;
import me.paulf.fairylights.server.item.LightVariant;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LightBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty LIT = BlockStateProperties.field_208190_q;

    private static final VoxelShape MIN_ANCHOR_SHAPE = Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

    private final VoxelShape floorShape, eastWallShape, westWallShape, northWallShape, southWallShape, ceilingShape;

    private final LightVariant<?> variant;

    public LightBlock(final Properties properties, final LightVariant<?> variant) {
        super(properties.func_222380_e());
        this.variant = variant;
        final AABB bb = this.variant.getBounds();
        final double w = Math.max(bb.func_216364_b(), bb.func_216362_d());
        final double w0 = 0.5D - w * 0.5D;
        final double w1 = 0.5D + w * 0.5D;
        if (variant.isOrientable()) {
            this.floorShape = VoxelShapes.func_197873_a(w0, 0.0D, w0, w1, -bb.field_72338_b, w1);
            this.eastWallShape = VoxelShapes.func_197873_a(0.0D, w0, w0, -bb.field_72338_b, w1, w1);
            this.westWallShape = VoxelShapes.func_197873_a(1.0D + bb.field_72338_b, w0, w0, 1.0D, w1, w1);
            this.southWallShape = VoxelShapes.func_197873_a(w0, w0, 0.0D, w1, w1, -bb.field_72338_b);
            this.northWallShape = VoxelShapes.func_197873_a(w0, w0, 1.0D + bb.field_72338_b, w1, w1, 1.0D);
            this.ceilingShape = VoxelShapes.func_197873_a(w0, 1.0D + bb.field_72338_b, w0, w1, 1.0D, w1);
        } else {
            final double t = 0.125D;
            final double u = 11.0D / 16.0D;
            this.floorShape = VoxelShapes.func_197873_a(w0, 0.0D, w0, w1, bb.func_216360_c() - this.variant.getFloorOffset(), w1);
            this.eastWallShape = VoxelShapes.func_197873_a(w0 - t, u + bb.field_72338_b, w0, w1 - t, u + bb.field_72337_e, w1);
            this.westWallShape = VoxelShapes.func_197873_a(w0 + t, u + bb.field_72338_b, w0, w1 + t, u + bb.field_72337_e, w1);
            this.southWallShape = VoxelShapes.func_197873_a(w0, u + bb.field_72338_b, w0 - t, w1, u + bb.field_72337_e, w1 - t);
            this.northWallShape = VoxelShapes.func_197873_a(w0, u  + bb.field_72338_b, w0 + t, w1, u + bb.field_72337_e, w1 + t);
            this.ceilingShape = VoxelShapes.func_197873_a(w0, 1.0D + bb.field_72338_b - 4.0D / 16.0D, w0, w1, 1.0D, w1);
        }
        this.func_180632_j(this.field_176227_L.func_177621_b().func_206870_a(field_185512_D, Direction.NORTH).func_206870_a(field_196366_M, AttachFace.WALL).func_206870_a(LIT, true));
    }

    public LightVariant<?> getVariant() {
        return this.variant;
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
    public boolean func_196260_a(final BlockState state, final IWorldReader world, final BlockPos pos) {
        final Direction facing = HorizontalFaceBlock.func_196365_i(state);
        final BlockPos anchorPos = pos.func_177972_a(facing.func_176734_d());
        final VoxelShape shape = world.func_180495_p(anchorPos).func_196952_d(world, anchorPos);
        if (state.func_177229_b(field_196366_M) != AttachFace.WALL) {
            return !VoxelShapes.func_197879_c(shape.func_212434_a(facing), MIN_ANCHOR_SHAPE, IBooleanFunction.field_223232_c_);
        }
        return Block.func_208061_a(shape, facing);
    }

    // 'super' but opposite facing for y axis placement
    @Nullable
    public BlockState func_196258_a(final BlockItemUseContext context) {
        for (final Direction dir : context.func_196009_e()) {
            final BlockState state;
            if (dir.func_176740_k() == Direction.Axis.Y) {
                state = this.func_176223_P()
                    .func_206870_a(field_196366_M, dir == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                    .func_206870_a(field_185512_D, context.func_195992_f().func_176734_d());
            } else {
                state = this.func_176223_P()
                    .func_206870_a(field_196366_M, AttachFace.WALL)
                    .func_206870_a(field_185512_D, dir.func_176734_d());
            }
            if (state.func_196955_c(context.func_195991_k(), context.func_195995_a())) {
                return state;
            }
        }
        return null;
    }

    @Override
    public void func_180633_a(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack) {
        super.func_180633_a(world, pos, state, placer, stack);
        final TileEntity entity = world.func_175625_s(pos);
        if (entity instanceof LightBlockEntity) {
            final ItemStack lightItem = stack.func_77946_l();
            lightItem.func_190920_e(1);
            ((LightBlockEntity) entity).setItemStack(lightItem);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> func_220076_a(final BlockState state, final LootContext.Builder builder) {
        final TileEntity entity = builder.func_216019_b(LootParameters.field_216288_h);
        if (entity instanceof LightBlockEntity) {
            return Collections.singletonList(((LightBlockEntity) entity).getLight().getItem().func_77946_l());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType func_225533_a_(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult hit) {
        final TileEntity entity = world.func_175625_s(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).interact(world, pos, state, player, hand, hit);
            return ActionResultType.SUCCESS;
        }
        return super.func_225533_a_(state, world, pos, player, hand, hit);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void func_180655_c(final BlockState state, final World world, final BlockPos pos, final Random rng) {
        super.func_180655_c(state, world, pos, rng);
        final TileEntity entity = world.func_175625_s(pos);
        if (entity instanceof LightBlockEntity) {
            ((LightBlockEntity) entity).animateTick();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape func_220053_a(final BlockState state, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
        switch (state.func_177229_b(field_196366_M)) {
            default:
            case FLOOR:
                return this.floorShape;
            case WALL:
                switch (state.func_177229_b(field_185512_D)) {
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
    public ItemStack func_185473_a(final IBlockReader world, final BlockPos pos, final BlockState state) {
        final TileEntity entity = world.func_175625_s(pos);
        if (entity instanceof LightBlockEntity) {
            return ((LightBlockEntity) entity).getLight().getItem().func_77946_l();
        }
        final ItemStack stack = new ItemStack(this);
        DyeableItem.setColor(stack, DyeColor.YELLOW);
        return stack;
    }

    @Override
    protected void func_206840_a(final StateContainer.Builder<Block, BlockState> builder) {
        builder.func_206894_a(field_196366_M, field_185512_D, LIT);
    }
}
