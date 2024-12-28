package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectionBlockEntity extends BlockEntity {
    public String unblockedBlockstate;
    public String pool;
    public String type;

    public ConnectionBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.CONNECTION_BLOCK_ENTITY.get(), pos, blockState);
        this.unblockedBlockstate = "minecraft:air";
        this.pool = "all";
        this.type = "both";
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.unblockedBlockstate = tag.getString("unblockedBlockstate");
        this.pool = tag.getString("pool");
        this.type = tag.getString("type");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("unblockedBlockstate", this.unblockedBlockstate);
        tag.putString("pool", this.pool);
        tag.putString("type", this.type);;
    }
}
