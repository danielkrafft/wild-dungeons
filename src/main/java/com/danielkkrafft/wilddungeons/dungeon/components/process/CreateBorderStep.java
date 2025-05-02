package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class CreateBorderStep extends PostProcessingStep {
    int stoneDepth = 3;

    @Override
    public void handle(List<DungeonRoom> rooms) {
        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (DungeonRoom room : rooms) {
            for (BoundingBox box : room.getBoundingBoxes()) {
                for (int x = box.minX(); x <= box.maxX(); x++) {
                    for (int z = box.minZ(); z <= box.maxZ(); z++) {
                        for (int y = box.minY()-1; y >= box.minY()-1-stoneDepth; y--) {
                            blockPos.set(x, y, z);
                            setBlockFast(level, blockPos, Blocks.STONE.defaultBlockState());
                        }
                        blockPos.set(x, box.minY()-2-stoneDepth, z);
                        setBlockFast(level, blockPos, Blocks.BEDROCK.defaultBlockState());
                    }
                }
                for (int x = box.minX()-1; x <= box.maxX()+1; x++) {
                    for (int z = box.minZ()-1; z <= box.maxZ()+1; z++) {
                        int y0 = room.getConnectionPoints().getFirst().getRealBoundingBox().minY(); // TODO this is super lazy and wouldn't work if rooms didn't have even connection points
                        for (int y = y0; y >= y0-5; y--) {
                            if (z == box.minZ()-1 || z == box.maxZ()+1 || x == box.minX()-1 || x == box.maxX()+1) {
                                blockPos.set(x, y, z);
                                setBlockFast(level, blockPos, Blocks.POLISHED_ANDESITE.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }
}
