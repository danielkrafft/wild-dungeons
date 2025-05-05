package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WDBedrockBlock extends Block {
    public static final IntegerProperty MIMIC = IntegerProperty.create("mimic", 0, 2000);//currently there are 1060 blocks in the game

    public WDBedrockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MIMIC, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MIMIC);
    }

    public static BlockState of(Block mimicBlock) {
        if (mimicBlock.defaultBlockState().isAir()) return Blocks.BARRIER.defaultBlockState();
        return WDBlocks.WD_BEDROCK.get().getStateDefinition().any().trySetValue(MIMIC, BuiltInRegistries.BLOCK.getId(mimicBlock));
    }
}
