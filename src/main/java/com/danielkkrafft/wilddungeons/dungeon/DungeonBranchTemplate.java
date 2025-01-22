package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponent;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

public record DungeonBranchTemplate(String name, DungeonRegistry.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) implements DungeonComponent {

    public static DungeonBranchTemplate build(String name, DungeonRegistry.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) {
        return new DungeonBranchTemplate(name, roomTemplates, materials, enemyTable, difficulty);
    }

    public DungeonBranchTemplate pool(WeightedPool<DungeonBranchTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

    public DungeonBranch placeInWorld(DungeonFloor floor, BlockPos origin) {
        return new DungeonBranch(this.name, floor, origin);
    }
}
