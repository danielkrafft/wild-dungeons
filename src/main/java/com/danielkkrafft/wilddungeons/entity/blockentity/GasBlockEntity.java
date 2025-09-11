package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GasBlockEntity extends BlockEntity{
    public GasBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.TOXIC_GAS_ENTITY.get(), pos, blockState);
    }

    private int tickAge = 0;

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GasBlockEntity gasBlockEntity) {
        gasBlockEntity.tickAge++;
    }

    public int getTickAge() {
        return tickAge;
    }
}
