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

    public static DungeonTemplate TEST_DUNGEON = DungeonTemplate.create("test_dungeon");

    public static DungeonTemplate OVERWORLD_BASIC_DUNGEON = DungeonTemplate.create("overworld_basic")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(OVERWORLD_FLOOR_POOL, 1))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0)
            .setExitBehavior(RANDOMIZE)
            .setNextDungeon(OVERWORLD_DUNGEON_POOL);

    public static void setupDungeons() {
        DUNGEON_REGISTRY.add(TEST_DUNGEON);
        DUNGEON_REGISTRY.add(OVERWORLD_BASIC_DUNGEON);
    }
}