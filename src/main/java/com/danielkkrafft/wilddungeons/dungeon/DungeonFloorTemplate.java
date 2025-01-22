package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonComponent;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

public record DungeonFloorTemplate(String name, DungeonRegistry.DungeonLayout<DungeonBranchTemplate> branchTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) implements DungeonComponent {

    public static DungeonFloorTemplate build(String name, DungeonRegistry.DungeonLayout<DungeonBranchTemplate> branchTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) {
        return new DungeonFloorTemplate(name, branchTemplates, materials, enemyTable, difficulty);
    }

    public DungeonFloorTemplate pool(WeightedPool<DungeonFloorTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

    public DungeonFloor placeInWorld(DungeonSession session, BlockPos position, int index, WeightedPool<String> destinations) {
        WildDungeons.getLogger().info("PLACING FLOOR: {}", this.name());
        return new DungeonFloor(this.name, session.getSessionKey(), position, index, destinations);
    }
}
