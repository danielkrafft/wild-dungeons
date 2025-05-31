package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry.*;

public class DungeonPoolRegistry {//is this redundant? We have RiftPoolRegistry and this seems outdated and has no uses
    public static final WeightedPool<DungeonTemplate> TEST_DUNGEON_POOL = new WeightedPool<DungeonTemplate>()
            .add(TEST_DUNGEON, 1);;
    public static final WeightedPool<DungeonTemplate> OVERWORLD_DUNGEON_POOL = new WeightedPool<DungeonTemplate>()
            .add(MEGA_DUNGEON, 1);
    public static final WeightedPool<DungeonTemplate> VILLAGE_DUNGEON_POOL = new WeightedPool<DungeonTemplate>()
            .add(VILLAGE_DUNGEON, 1);
    public static final WeightedPool<DungeonTemplate> NETHER_DUNGEON_POOL = new WeightedPool<DungeonTemplate>()
            .add(PIGLIN_FACTORY_DUNGEON, 1);
}
