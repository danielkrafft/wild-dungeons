package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorRegistry.*;

public class DungeonFloorPoolRegistry {
    public static final WeightedPool<DungeonFloorTemplate> TEST_FLOOR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonFloorTemplate> OVERWORLD_FLOOR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonFloorTemplate> VILLAGE_FLOOR_POOL = new WeightedPool<>();

    public static void setupFloorPools(){
        TEST_FLOOR_POOL.add(TEST_FLOOR, 1);
        OVERWORLD_FLOOR_POOL
                .add(MEGA_DUNGEON_FLOOR, 15);
        VILLAGE_FLOOR_POOL
                .add(VILLAGE_FLOOR, 1);
    }
}
