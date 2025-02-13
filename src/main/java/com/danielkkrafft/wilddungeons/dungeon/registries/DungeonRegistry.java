package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import java.util.ArrayList;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry.OVERWORLD_FLOOR_POOL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorPoolRegistry.VILLAGE_FLOOR_POOL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonFloorRegistry.PIGLIN_FACTORY_FLOOR;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.OVERWORLD_MATERIAL_POOL_0;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.VILLAGE_MATERIAL_POOL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialRegistry.PIGLIN_FACTORY_MATERIAL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession.DungeonExitBehavior.DESTROY;

public class DungeonRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonTemplate> DUNGEON_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonTemplate> dungeons = new ArrayList<>();

    public static DungeonTemplate TEST_DUNGEON = create("test_dungeon");

    public static DungeonTemplate OVERWORLD_BASIC_DUNGEON = create("mega_dungeon")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(OVERWORLD_FLOOR_POOL, 1))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0)
            .set(DISPLAY_NAME, "MEGA DUNGEON")
            .set(ICON, "1-1")
            .set(PRIMARY_COLOR, 0xFF44cc00)
            .set(SECONDARY_COLOR, 0xFFdde63e)
            .set(TARGET_TIME, 12000)
            .set(TARGET_DEATHS, 0)
            .set(TARGET_SCORE, 15000)
            .set(EXIT_BEHAVIOR, DESTROY)
            .set(NEXT_DUNGEON, OVERWORLD_DUNGEON_POOL);

    public static DungeonTemplate PIGLIN_FACTORY_DUNGEON = create("piglin_factory")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(new WeightedPool<DungeonFloorTemplate>().add(PIGLIN_FACTORY_FLOOR, 1), 1))
            .set(MATERIAL, new WeightedPool<DungeonMaterial>().add(PIGLIN_FACTORY_MATERIAL, 1))
            .set(DISPLAY_NAME, "PIGLIN FACTORY")
            .set(ICON, "2-1")
            .set(PRIMARY_COLOR, 0xFFde1616)
            .set(SECONDARY_COLOR, 0xFFb83f1a)
            .set(TARGET_TIME, 12000)
            .set(TARGET_DEATHS, 0)
            .set(TARGET_SCORE, 15000)
            .set(EXIT_BEHAVIOR, DESTROY)
            .set(NEXT_DUNGEON, NETHER_DUNGEON_POOL)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.NETHER_CAVES)
            .set(INTENSITY, 0);

    public static DungeonTemplate VILLAGE_DUNGEON = create("village_dungeon")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(VILLAGE_FLOOR_POOL, 1))
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(HAS_BEDROCK_SHELL, false)
            .set(DISPLAY_NAME, "VILLAGE DUNGEON")
            .set(ICON, "1-2")
            .set(PRIMARY_COLOR, 0xFF44cc00)
            .set(SECONDARY_COLOR, 0xFFdde63e)
            .set(TARGET_TIME, 12000)
            .set(TARGET_DEATHS, 0)
            .set(TARGET_SCORE, 15000)
            .set(EXIT_BEHAVIOR, DESTROY)
            .set(NEXT_DUNGEON, VILLAGE_DUNGEON_POOL);


    public static DungeonTemplate create(String name){
        DungeonTemplate dungeon = DungeonTemplate.create(name);
        dungeons.add(dungeon);
        return dungeon;
    }

    public static void setupDungeons() {
        dungeons.forEach(DUNGEON_REGISTRY::add);
    }
}