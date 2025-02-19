package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry.*;

public class DungeonPoolRegistry {
    public static final WeightedPool<DungeonTemplate> TEST_DUNGEON_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonTemplate> OVERWORLD_DUNGEON_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonTemplate> VILLAGE_DUNGEON_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonTemplate> NETHER_DUNGEON_POOL = new WeightedPool<>();

    public static void setupDungeonPools(){
        TEST_DUNGEON_POOL.add(TEST_DUNGEON, 1);
        OVERWORLD_DUNGEON_POOL.add(OVERWORLD_BASIC_DUNGEON, 1);
        NETHER_DUNGEON_POOL.add(PIGLIN_FACTORY_DUNGEON, 1);
        VILLAGE_DUNGEON_POOL.add(VILLAGE_DUNGEON, 1);
    }
}
