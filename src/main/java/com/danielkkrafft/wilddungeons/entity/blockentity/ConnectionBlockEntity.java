package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectionBlockEntity extends BlockEntity {
    public String occupiedBlockstate;
    public String unoccupiedBlockstate;
    public String pool;

    public ConnectionBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.CONNECTION_BLOCK_ENTITY.get(), pos, blockState);
        this.occupiedBlockstate = "minecraft:air";
        this.unoccupiedBlockstate = "minecraft:stone_bricks";
        this.pool = "all";
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.occupiedBlockstate = tag.getString("occupiedBlockstate");
        this.unoccupiedBlockstate = tag.getString("unoccupiedBlockstate");
        this.pool = tag.getString("pool");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("occupiedBlockstate", this.occupiedBlockstate);
        tag.putString("unoccupiedBlockstate", this.unoccupiedBlockstate);
        tag.putString("pool", this.pool);
    }
}
