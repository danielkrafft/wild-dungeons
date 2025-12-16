package com.danielkkrafft.wilddungeons.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DetoniteClusterBlock extends AmethystBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<DetoniteClusterBlock> CODEC = RecordCodecBuilder.mapCodec((p_308798_) -> p_308798_.group(Codec.FLOAT.fieldOf("height").forGetter((p_304411_) -> p_304411_.height), Codec.FLOAT.fieldOf("aabb_offset").forGetter((p_304908_) -> p_304908_.aabbOffset), propertiesCodec()).apply(p_308798_, DetoniteClusterBlock::new));
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    private final float height;
    private final float aabbOffset;
    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;

    public MapCodec<DetoniteClusterBlock> codec() {
        return CODEC;
    }

    public DetoniteClusterBlock(float height, float aabbOffset, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(FACING, Direction.UP));
        this.upAabb = Block.box((double)aabbOffset, (double)0.0F, (double)aabbOffset, (double)(16.0F - aabbOffset), (double)height, (double)(16.0F - aabbOffset));
        this.downAabb = Block.box((double)aabbOffset, (double)(16.0F - height), (double)aabbOffset, (double)(16.0F - aabbOffset), (double)16.0F, (double)(16.0F - aabbOffset));
        this.northAabb = Block.box((double)aabbOffset, (double)aabbOffset, (double)(16.0F - height), (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset), (double)16.0F);
        this.southAabb = Block.box((double)aabbOffset, (double)aabbOffset, (double)0.0F, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset), (double)height);
        this.eastAabb = Block.box((double)0.0F, (double)aabbOffset, (double)aabbOffset, (double)height, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset));
        this.westAabb = Block.box((double)(16.0F - height), (double)aabbOffset, (double)aabbOffset, (double)16.0F, (double)(16.0F - aabbOffset), (double)(16.0F - aabbOffset));
        this.height = height;
        this.aabbOffset = aabbOffset;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = (Direction)state.getValue(FACING);
        switch (direction) {
            case NORTH:
                return this.northAabb;
            case SOUTH:
                return this.southAabb;
            case EAST:
                return this.eastAabb;
            case WEST:
                return this.westAabb;
            case DOWN:
                return this.downAabb;
            case UP:
            default:
                return this.upAabb;
        }
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = (Direction)state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return direction == ((Direction)state.getValue(FACING)).getOpposite() && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(FACING, context.getClickedFace());
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    protected FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED, FACING});
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.FACING;
    }
}
