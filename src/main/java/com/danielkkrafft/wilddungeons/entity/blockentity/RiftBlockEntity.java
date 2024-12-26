package com.danielkkrafft.wilddungeons.entity.blockentity;

import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RiftBlockEntity extends BlockEntity {
    public String destination;
    public boolean open = false;

    public RiftBlockEntity(BlockPos pos, BlockState blockState) {
        super(WDBlockEntities.RIFT_BLOCK_ENTITY.get(), pos, blockState);
        destination = "random";
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.destination = tag.getString("destination");
        this.open = tag.getBoolean("open");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("destination", this.destination);
        tag.putBoolean("open", this.open);
    }
}
