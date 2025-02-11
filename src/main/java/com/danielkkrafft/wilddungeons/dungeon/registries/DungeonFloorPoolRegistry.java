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
                .add(OVERWORLD_BASIC_FLOOR, 15);
//                .add(OVERWORLD_SANDY_FLOOR, 4)
//                .add(OVERWORLD_RED_SANDY_FLOOR, 1);
        VILLAGE_FLOOR_POOL
                .add(VILLAGE_FLOOR, 1);
    }
}
