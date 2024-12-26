package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.RiftBlockEntity;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.player.WDPlayerManager;
import com.danielkkrafft.wilddungeons.registry.WDDimensions;
import com.danielkkrafft.wilddungeons.util.CommandUtil;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.world.dimension.tools.InfiniverseAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonFloor {
    public List<DungeonBranch> dungeonBranches = new ArrayList<>();
    public DungeonComponents.DungeonFloorTemplate floorTemplate;
    public ServerLevel level;
    public BlockPos origin;
    public ResourceKey<Level> LEVEL_KEY;
    public BlockPos spawnPoint;
    public DungeonSession session;
    public final int id;

    public DungeonFloor(DungeonComponents.DungeonFloorTemplate floorTemplate, DungeonSession session, BlockPos origin, int id, List<String> destinations) {
        this.floorTemplate = floorTemplate;
        this.id = id;
        this.LEVEL_KEY = buildFloorLevelKey(session.entrance, this);
        ServerLevel newLevel = InfiniverseAPI.get().getOrCreateLevel(session.server, LEVEL_KEY, () -> WDDimensions.createLevel(session.server));
        this.level = newLevel;
        this.origin = origin;
        this.session = session;
        generateDungeonFloor();
        this.spawnPoint = this.dungeonBranches.getFirst().spawnPoint;
        BlockPos exitRiftPos = this.dungeonBranches.getFirst().dungeonRooms.getFirst().rifts.getFirst();
        RiftBlockEntity riftBlockEntity = (RiftBlockEntity) this.level.getBlockEntity(exitRiftPos);
        if (riftBlockEntity != null) {
            riftBlockEntity.destination = "exit";
        }

        BlockPos enterRiftPos = this.dungeonBranches.getLast().dungeonRooms.getLast().rifts.getLast();
        RiftBlockEntity enterRiftBlockEntity = (RiftBlockEntity) this.level.getBlockEntity(enterRiftPos);
        if (enterRiftBlockEntity != null) {
            enterRiftBlockEntity.destination = destinations.get(RandomUtil.randIntBetween(0, destinations.size()-1));
        }


    }

    public void shutdown() {
        InfiniverseAPI.get().markDimensionForUnregistration(session.server, this.LEVEL_KEY);
        FileUtil.deleteDirectoryContents(FileUtil.getWorldPath().resolve("dimensions").resolve(WildDungeons.MODID).resolve(this.LEVEL_KEY.location().getPath()), true);
    }

    public static ResourceKey<Level> buildFloorLevelKey(BlockPos entrance, DungeonFloor floor) {
        return ResourceKey.create(Registries.DIMENSION, WildDungeons.rl(floor.floorTemplate.name() + "_" + floor.id + entrance.getX() + entrance.getY() + entrance.getZ()));
    }

    private void generateDungeonFloor() {
        int tries = 0;
        while (dungeonBranches.size() < floorTemplate.branchCount() && tries < floorTemplate.branchCount() * 2) {
            populateNextBranch();
            if (dungeonBranches.getLast().dungeonRooms.isEmpty()) {break;}
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