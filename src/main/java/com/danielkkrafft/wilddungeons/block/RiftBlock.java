package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.dungeon.Dungeon;
import com.danielkkrafft.wilddungeons.dungeon.Dungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RiftBlock extends Block implements EntityBlock {
    public RiftBlock(Properties properties) {
        super(properties);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftBlockEntity(pos, state);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ServerPlayer serverplayer) {
            RiftBlockEntity riftBlockEntity = (RiftBlockEntity) level.getBlockEntity(pos);
            if (riftBlockEntity == null || riftBlockEntity.destination == null) {return;}

            Dungeon dungeon = Dungeons.DUNGEONS.get(riftBlockEntity.destination);
            dungeon.startDungeonDimension(serverplayer.getServer());
            dungeon.enterDungeon(serverplayer);

        }
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return true;
    }
}
