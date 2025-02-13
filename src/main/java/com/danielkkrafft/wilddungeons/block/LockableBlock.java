package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.dungeon.components.room.TargetPurgeRoom;
import com.danielkkrafft.wilddungeons.item.DungeonKeyItem;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockableBlock extends Block {
    public static BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static DirectionProperty FACING = BlockStateProperties.FACING;
    public static final VoxelShape LOCKED_NORTH_SHAPE = Block.box(2, 2, 0, 14, 16, 2);
    public static final VoxelShape LOCKED_SOUTH_SHAPE = Block.box(2, 2, 14, 14, 16, 16);
    public static final VoxelShape LOCKED_EAST_SHAPE = Block.box(14, 2, 2, 16, 16, 14);
    public static final VoxelShape LOCKED_WEST_SHAPE = Block.box(0, 2, 2, 2, 16, 14);
    public static final VoxelShape UNLOCKED_NORTH_SHAPE = Block.box(2, 0, 0, 14, 16, 2);
    public static final VoxelShape UNLOCKED_SOUTH_SHAPE = Block.box(2, 0, 14, 14, 16, 16);
    public static final VoxelShape UNLOCKED_EAST_SHAPE = Block.box(14, 0, 2, 16, 16, 14);
    public static final VoxelShape UNLOCKED_WEST_SHAPE = Block.box(0, 0, 2, 2, 16, 14);

    public LockableBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOCKED);
        builder.add(FACING);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        Boolean locked = state.getValue(LOCKED);
        if (!locked) {
            return ItemInteractionResult.FAIL;
        }
        if (stack.getItem() instanceof DungeonKeyItem) {
            stack.shrink(1);
            level.setBlock(pos, state.setValue(LOCKED, false), 2);
            level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);

            ServerPlayer serverPlayer = (ServerPlayer) player;
            WDPlayer wdPlayer = WDPlayerManager.getInstance().getOrCreateServerWDPlayer(serverPlayer);
            if (wdPlayer.getCurrentDungeon() != null) {
                if (wdPlayer.getCurrentRoom() instanceof TargetPurgeRoom enemyPurgeRoom) {
                        enemyPurgeRoom.discardByBlockPos(pos);
                }
            }

            return ItemInteractionResult.SUCCESS;
        } else {
            return ItemInteractionResult.FAIL;
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
        return blockstate.setValue(LOCKED, true);
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        Boolean locked = state.getValue(LOCKED);
        return getVoxelShape(direction, locked);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        Boolean locked = state.getValue(LOCKED);
        return getVoxelShape(direction, locked);
    }


    private VoxelShape getVoxelShape(Direction direction, Boolean locked) {
        return switch (direction) {
            case SOUTH -> locked ? LOCKED_SOUTH_SHAPE : UNLOCKED_SOUTH_SHAPE;
            case EAST -> locked ? LOCKED_EAST_SHAPE : UNLOCKED_EAST_SHAPE;
            case WEST -> locked ? LOCKED_WEST_SHAPE : UNLOCKED_WEST_SHAPE;
            default -> locked ? LOCKED_NORTH_SHAPE : UNLOCKED_NORTH_SHAPE;
        };
    }

}
