package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.entity.PrimalCreeper;
import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PutridEggBlock extends Block {

    private static final VoxelShape SHAPE;

    public PutridEggBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, level, pos, explosion);
        PrimalCreeper creeper = new PrimalCreeper(WDEntities.PRIMAL_CREEPER.get(), level);
        creeper.setPos(pos.getCenter());
        level.addFreshEntity(creeper);
    }
    static {
        SHAPE = Block.box((double)3.5F, (double)0.0F, (double)3.5F, (double)12.5F, (double)14.0F, (double)12.5F);
    }

}
