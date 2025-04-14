package com.danielkkrafft.wilddungeons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EmeraldPileBlock extends Block {
    public static final int MAX_EMERALD_COUNT = 40;
    public static final IntegerProperty EMERALD_COUNT = IntegerProperty.create("emerald_count", 0, MAX_EMERALD_COUNT);
    public static final IntegerProperty MODEL = IntegerProperty.create("model", 1, 7);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape ONE_AABB = Block.box(3, 0, 3, 12, 1, 12);
    protected static final VoxelShape TWO_AABB = Block.box(2, 0, 2, 13, 1, 13);
    protected static final VoxelShape THREE_AABB = Block.box(2, 0, 2, 13, 1, 13);
    protected static final VoxelShape FOUR_AABB = Block.box(2, 0, 2, 13, 3, 13);
    protected static final VoxelShape FIVE_AABB = Block.box(1, 0, 1, 15, 5, 15);
    protected static final VoxelShape SIX_AABB = Block.box(3, 0, 3, 13, 9, 13);
    protected static final VoxelShape SEVEN_AABB = Block.box(2, 0, 2, 14, 12, 14);

    public EmeraldPileBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMERALD_COUNT, 1).setValue(MODEL, 1));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            int count = blockstate.getValue(EMERALD_COUNT)+1;
            int model = switch (count) {
                case 1 -> 1;
                case 2 -> 2;
                case 3, 4 -> 3;
                case 5, 6, 7, 8, 9 -> 4;
                case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> 5;
                case 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 -> 6;
                default -> 7;
            };
            return blockstate.setValue(MODEL, model).setValue(EMERALD_COUNT, Math.min(MAX_EMERALD_COUNT, count));
        } else {
            return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection());
        }
    }

    protected boolean canBeReplaced(@NotNull BlockState state, BlockPlaceContext useContext) {
        return !useContext.isSecondaryUseActive() && useContext.getItemInHand().is(this.asItem()) && state.getValue(EMERALD_COUNT) < MAX_EMERALD_COUNT || super.canBeReplaced(state, useContext);
    }

    protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getVoxelShape(state.getValue(FACING), state.getValue(MODEL));
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return getVoxelShape(state.getValue(FACING), state.getValue(MODEL));
    }

    private VoxelShape getVoxelShape(Direction direction, int model) {
        VoxelShape shape = switch (model) {
            case 1 -> ONE_AABB;
            case 2 -> TWO_AABB;
            case 3 -> THREE_AABB;
            case 4 -> FOUR_AABB;
            case 5 -> FIVE_AABB;
            case 6 -> SIX_AABB;
            default -> SEVEN_AABB;
        };
        return shape;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMERALD_COUNT);
        builder.add(MODEL);
        builder.add(FACING);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (willHarvest) {
            int count = state.getValue(EMERALD_COUNT);
            for (int i = 0; i < count; i++) {
                Block.popResource(level, pos, Items.EMERALD.getDefaultInstance());
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);

    }
}