package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.EnemyTableRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior.*;
import static com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession.DungeonExitBehavior.*;

public class DungeonRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonTemplate> DUNGEON_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static DungeonTemplate TEST_DUNGEON = DungeonTemplate.build(
            "test_dungeon",
            NONE,
            new DungeonLayout<DungeonFloorTemplate>()
                    .add(TEST_FLOOR_POOL, 1),
            ALL_MATERIAL_POOL,
            BASIC_ENEMY_TABLE,
            1.0,
            1.1,
            DESTROY,
            TEST_DUNGEON_POOL);

    public static DungeonTemplate OVERWORLD_BASIC_DUNGEON = DungeonTemplate.build(
            "overworld_basic",
            NONE,
            new DungeonLayout<DungeonFloorTemplate>()
                    .add(OVERWORLD_FLOOR_POOL, 1),
            OVERWORLD_MATERIAL_POOL_0,
            BASIC_ENEMY_TABLE,
            1.0,
            1.1,
            RANDOMIZE,
            OVERWORLD_DUNGEON_POOL);

    public static void setupDungeons(){
        DUNGEON_REGISTRY.add(TEST_DUNGEON);
        DUNGEON_REGISTRY.add(OVERWORLD_BASIC_DUNGEON);
    }
}
