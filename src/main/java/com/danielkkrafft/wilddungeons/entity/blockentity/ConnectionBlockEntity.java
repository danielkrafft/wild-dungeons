package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectionBlockEntity extends BlockEntity {
    public String lockedBlockstate;
    public String unlockedBlockstate;
    public String pool;

    public ConnectionBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.CONNECTION_BLOCK_ENTITY.get(), pos, blockState);
        this.lockedBlockstate = "minecraft:stone_bricks";
        this.unlockedBlockstate = "minecraft:air";
        this.pool = "all";
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.lockedBlockstate = tag.getString("lockedBlockstate");
        this.unlockedBlockstate = tag.getString("unlockedBlockstate");
        this.pool = tag.getString("pool");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("lockedBlockstate", this.lockedBlockstate);
        tag.putString("unlockedBlockstate", this.unlockedBlockstate);
        tag.putString("pool", this.pool);
    }
}
