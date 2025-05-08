package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class SurroundingColumnStep extends PostProcessingStep {

    public final List<BlockState> blockStateList;
    public final int range;

    public SurroundingColumnStep(List<BlockState> blockStateList, int range) {
        this.blockStateList = blockStateList;
        this.range = range;
    }

    @Override
    public void handle(List<DungeonRoom> rooms) {
        BoundingBox reference = rooms.getFirst().getBoundingBoxes().getFirst();
        ServerLevel level = rooms.getFirst().getBranch().getFloor().getLevel();
        int minY = reference.minY();
        int minX = reference.minX();
        int maxX = reference.maxX();
        int minZ = reference.minZ();
        int maxZ = reference.maxZ();
        for (DungeonRoom room : rooms) {
            for (BoundingBox box : room.getBoundingBoxes()) {
                if (box.minY() < minY) minY = box.minY();
                if (box.minX() < minX) minX = box.minX();
                if (box.maxX() > maxX) maxX = box.maxX();
                if (box.minZ() < minZ) minZ = box.minZ();
                if (box.maxZ() < maxZ) maxZ = box.maxZ();
            }
        }

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int x = minX - range; x < maxX + range; x++) {
            for (int z = minZ - range; z < maxZ + range; z++) {
                for (int y = minY - blockStateList.size(); y < minY; y++) {
                    mutableBlockPos.set(x, y, z);
                    setBlockFast(level, mutableBlockPos, blockStateList.get(minY - y - 1));
                }
            }
        }
    }
}