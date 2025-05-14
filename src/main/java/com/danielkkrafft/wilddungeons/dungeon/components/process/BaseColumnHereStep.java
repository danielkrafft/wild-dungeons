package com.danielkkrafft.wilddungeons.dungeon.components.process;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Arrays;
import java.util.List;

public class BaseColumnHereStep extends PostProcessingStep {

    public final List<BlockState> blockStateList;

    public BaseColumnHereStep(List<BlockState> blockStateList) {
        this.blockStateList = blockStateList;
    }

    @Override
    public void handle(List<DungeonRoom> rooms) {
        BlockState[] blockStates = new BlockState[EmptyGenerator.GEN_DEPTH];
        Arrays.fill(blockStates, Blocks.AIR.defaultBlockState());

        BoundingBox reference = rooms.getFirst().getBoundingBoxes().getFirst();
        int minY = reference.minY();

        for (int y = minY - blockStateList.size(); y < minY; y++) {
            blockStates[y] = blockStateList.get(minY - y - 1);
        }

        rooms.getFirst().getBranch().getFloor().baseColumn = blockStates;
    }
}