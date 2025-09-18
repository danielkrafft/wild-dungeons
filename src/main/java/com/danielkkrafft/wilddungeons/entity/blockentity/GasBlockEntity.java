package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GasBlockEntity extends BlockEntity{
    public GasBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.TOXIC_GAS_ENTITY.get(), pos, blockState);
    }

    private int tickAge = 20;

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GasBlockEntity gasBlockEntity) {
        BlockState aboveState = level.getBlockState(blockPos.above());
        if (aboveState.is(WDBlocks.TOXIC_GAS.get())) return;
        gasBlockEntity.tickAge++;
    }

    public int getTickAge() {
        return tickAge;
    }

    public void setTickAge(int i) {
     this.tickAge = i;
    }

    public float getRenderRandomness() {
        BlockPos pos = this.getBlockPos();
        return (pos.atY(0).asLong() % 10);//we want Ypos 0 so that it doesnt jump around as it moves upwards
    }
}
