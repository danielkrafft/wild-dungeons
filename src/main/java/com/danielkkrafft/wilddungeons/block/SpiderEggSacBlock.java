package com.danielkkrafft.wilddungeons.block;

//import com.danielkkrafft.wilddungeons.entity.Spiderling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiderEggSacBlock extends TransparentBlock {
    public static IntegerProperty EGGS = IntegerProperty.create("egg", 0,7);
    public static EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EGGS);
        builder.add(FACING);
    }

    public SpiderEggSacBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Direction playerDirection = context.getHorizontalDirection().getOpposite();
        state = state.setValue(FACING, playerDirection);
        if (context.getLevel().isClientSide()) return state;
        int eggs = context.getLevel().random.nextInt(8);
        state = state.setValue(EGGS, eggs);
        return state;
    }

    //TODO - Uncomment when Spiderlings are fixed for 1.21.11
//    private void SpawnSpiders(Level level, BlockPos pos, Entity entity) {
//        int amount = 1 + level.random.nextInt(3);
//        for (int i = 0; i < amount; i++) {
//            Spiderling spiderling = WDEntities.SPIDERLING.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
//            if (spiderling != null) {
//                BlockPos spawnPos = pos.offset(level.random.nextInt(3)-1, 0, level.random.nextInt(3)-1);
//                spiderling.absSnapTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
//                level.addFreshEntity(spiderling);
//                if (entity instanceof LivingEntity livingEntity)
//                   spiderling.setTarget(livingEntity);
//            }
//        }
//    }

    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                switch (state.getValue(FACING)) {
                    case NORTH:
                        return state.setValue(FACING, Direction.SOUTH);
                    case SOUTH:
                        return state.setValue(FACING, Direction.NORTH);
                    default:
                        return state;
                }
            case FRONT_BACK:
                switch (state.getValue(FACING)) {
                    case EAST:
                        return state.setValue(FACING, Direction.WEST);
                    case WEST:
                        return state.setValue(FACING, Direction.EAST);
                    default:
                        return state;
                }
            default:
                return super.mirror(state, mirror);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(4, 0, 4, 12, 10, 12);
    }


    //TODO - Fix for 1.21.11 and ensure Spiderlings are fixed for 1.21.11
//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//        if (willHarvest) {
//            SpawnSpiders(level, pos, player);
//        }
//        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
//    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockstate.isFaceSturdy(level, blockpos, Direction.UP);
    }
}
