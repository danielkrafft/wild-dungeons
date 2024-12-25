package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public class DungeonFloor {
    public List<DungeonBranch> dungeonBranches = new ArrayList<>();
    public DungeonComponents.DungeonFloorTemplate floorTemplate;
    public ServerLevel level;
    public BlockPos origin;

    public DungeonFloor(DungeonComponents.DungeonFloorTemplate floorTemplate, ServerLevel level, BlockPos origin) {
        this.floorTemplate = floorTemplate;
        this.level = level;
        this.origin = origin;
        generateDungeonFloor();
    }

    private void generateDungeonFloor() {
        int tries = 0;
        while (dungeonBranches.size() < floorTemplate.branchCount() && tries < floorTemplate.branchCount() * 2) {
            populateNextBranch();
            tries++;
        }
        WildDungeons.getLogger().info("PLACED {} BRANCHES IN {} TRIES", dungeonBranches.size(), tries);
    }

    private void populateNextBranch() {

        DungeonComponents.DungeonBranchTemplate nextBranch;
        if (dungeonBranches.isEmpty()) {
            nextBranch = floorTemplate.startingBranch();
        } else if (dungeonBranches.size() == floorTemplate.branchCount() - 1) {
            nextBranch = floorTemplate.endingBranch();
        } else {
            nextBranch = floorTemplate.branchPool().getRandom();
        }

        dungeonBranches.add(nextBranch.placeInWorld(this, level, origin));
    }
}