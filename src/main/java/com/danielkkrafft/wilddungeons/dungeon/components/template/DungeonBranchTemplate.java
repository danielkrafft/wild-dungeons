package com.danielkkrafft.wilddungeons.dungeon.components.template;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonFloor;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

public record DungeonBranchTemplate(String name, DungeonRegistration.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) implements DungeonComponent {

    public static DungeonBranchTemplate build(String name, DungeonRegistration.DungeonLayout<DungeonRoomTemplate> roomTemplates, WeightedPool<DungeonMaterial> materials, WeightedTable<EntityType<?>> enemyTable, double difficulty) {
        return new DungeonBranchTemplate(name, roomTemplates, materials, enemyTable, difficulty);
    }

    public DungeonBranchTemplate pool(WeightedPool<DungeonBranchTemplate> pool, Integer weight) {pool.add(this, weight); return this;}

    public DungeonBranch placeInWorld(DungeonFloor floor, BlockPos origin) {
        DungeonBranch newBranch = new DungeonBranch(this.name, floor, origin);
        int tries = 0;
        while (tries < 50) {
            try {
                if (newBranch.generateDungeonBranch()){
                    return newBranch;
                }
            } catch (Exception e) {
                WildDungeons.getLogger().warn("Failed to generate branch {} on try {}", this.name, tries);
                e.printStackTrace();
                newBranch.destroy();
            }
            tries++;
        }
        WildDungeons.getLogger().warn("Failed to generate branch {} after 50 tries", this.name);
        return null;
        //todo if we fail 50 times, regen the previous branch too
    }
}
