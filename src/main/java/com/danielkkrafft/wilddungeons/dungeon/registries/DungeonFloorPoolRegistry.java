package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorRegistry.*;

public class DungeonFloorPoolRegistry {
    public static final WeightedPool<DungeonFloorTemplate> TEST_FLOOR_POOL = new WeightedPool<DungeonFloorTemplate>()
            .add(TEST_FLOOR, 1);
    public static final WeightedPool<DungeonFloorTemplate> OVERWORLD_FLOOR_POOL = new WeightedPool<DungeonFloorTemplate>()
            .add(MEGA_DUNGEON_FLOOR, 15);
    public static final WeightedPool<DungeonFloorTemplate> VILLAGE_FLOOR_POOL = new WeightedPool<DungeonFloorTemplate>()
            .add(VILLAGE_DUNGEON_FLOOR, 1);
    public static final WeightedPool<DungeonFloorTemplate> WEAPON_GAUNTLET_POOL = new WeightedPool<DungeonFloorTemplate>()
            .add(GAUNTLET_TRIAL_FLOOR, 1)
            .add(GAUNTLET_SCIFI_FLOOR, 1)
            .add(GAUNTLET_GENERAL_FLOOR, 1);
}