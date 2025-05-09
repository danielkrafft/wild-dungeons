package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.List;

public abstract class PostProcessingStep {
    public abstract void handle(List<DungeonRoom> rooms);

    public void setBlockFast(ServerLevel level, BlockPos pos, BlockState state) {
        ChunkAccess chunk = level.getChunk(pos);
        LevelChunkSection levelchunksection = chunk.getSection(chunk.getSectionIndex(pos.getY()));
        levelchunksection.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        level.getChunkSource().getLightEngine().updateSectionStatus(pos, false);
    }
}
