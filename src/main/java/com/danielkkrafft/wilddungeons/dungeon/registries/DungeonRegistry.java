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

    public static DungeonTemplate OVERWORLD_BASIC_DUNGEON = DungeonTemplate.create("mega_dungeon")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(OVERWORLD_FLOOR_POOL, 2))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0)
            .setDisplayName("MEGA DUNGEON")
            .setIcon("1-1")
            .setPrimaryColor(0xFF44cc00)
            .setSecondaryColor(0xFFdde63e)
            .setTargetTime(12000)
            .setTargetDeaths(0)
            .setTargetScore(100000)
            .setExitBehavior(DESTROY)
            .setNextDungeon(OVERWORLD_DUNGEON_POOL);

    public static void setupDungeons() {
        DUNGEON_REGISTRY.add(TEST_DUNGEON);
        DUNGEON_REGISTRY.add(OVERWORLD_BASIC_DUNGEON);
    }
}