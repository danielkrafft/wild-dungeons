package com.danielkkrafft.wilddungeons.entity.blockentity;

//import com.danielkkrafft.wilddungeons.registry.WDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ConnectionBlockEntity extends BlockEntity {
    public String unblockedBlockstate;
    public String pool;
    public String type;

    public ConnectionBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityType.ENCHANTING_TABLE, pos, blockState); //TODO - Fix, this ain't an enchanting table
    }

//    public ConnectionBlockEntity(BlockPos pos, BlockState blockState) {
//        super(WDBlockEntities.CONNECTION_BLOCK_ENTITY.get(), pos, blockState);
//        this.unblockedBlockstate = "minecraft:air";
//        this.pool = "all";
//        this.type = "both";
//    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.unblockedBlockstate = input.getStringOr("unblockedBlockstate", "");
        this.pool = input.getStringOr("pool", "");
        this.type = input.getStringOr("type", "");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("unblockedBlockstate", this.unblockedBlockstate);
        output.putString("pool", this.pool);
        output.putString("type", this.type);;
    }
}
