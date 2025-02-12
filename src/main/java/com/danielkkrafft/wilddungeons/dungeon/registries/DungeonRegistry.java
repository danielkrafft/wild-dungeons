package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import java.util.ArrayList;

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

    public static DungeonTemplate TEST_DUNGEON = DungeonTemplate.create("test_dungeon");

    public static DungeonTemplate OVERWORLD_BASIC_DUNGEON = create("mega_dungeon")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(OVERWORLD_FLOOR_POOL, 1))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0)
            .setDisplayName("MEGA DUNGEON")
            .setIcon("1-1")
            .setPrimaryColor(0xFF44cc00)
            .setSecondaryColor(0xFFdde63e)
            .setTargetTime(12000)
            .setTargetDeaths(0)
            .setTargetScore(15000)
            .setExitBehavior(DESTROY)
            .setNextDungeon(OVERWORLD_DUNGEON_POOL);

    public static DungeonTemplate PIGLIN_FACTORY_DUNGEON = create("piglin_factory")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(new WeightedPool<DungeonFloorTemplate>().add(PIGLIN_FACTORY_FLOOR, 1), 1))
            .setMaterials(new WeightedPool<DungeonMaterial>().add(PIGLIN_FACTORY_MATERIAL, 1))
            .setDisplayName("PIGLIN FACTORY")
            .setIcon("2-1")
            .setPrimaryColor(0xFFde1616)
            .setSecondaryColor(0xFFb83f1a)
            .setTargetTime(12000)
            .setTargetDeaths(0)
            .setTargetScore(15000)
            .setExitBehavior(DESTROY)
            .setNextDungeon(NETHER_DUNGEON_POOL);

    public static DungeonTemplate VILLAGE_DUNGEON = create("village_dungeon")
            .setFloorTemplates(new DungeonLayout<DungeonFloorTemplate>()
                    .add(VILLAGE_FLOOR_POOL, 1))
            .setMaterials(VILLAGE_MATERIAL_POOL)
            .setBedrockShell(false)
            .setDisplayName("VILLAGE DUNGEON")
            .setIcon("1-1")
            .setPrimaryColor(0xFF44cc00)
            .setSecondaryColor(0xFFdde63e)
            .setTargetTime(12000)
            .setTargetDeaths(0)
            .setTargetScore(15000)
            .setExitBehavior(DESTROY)
            .setNextDungeon(VILLAGE_DUNGEON_POOL);


    public static DungeonTemplate create(String name){
        DungeonTemplate dungeon = DungeonTemplate.create(name);
        dungeons.add(dungeon);
        return dungeon;
    }

    public static void setupDungeons() {
        dungeons.forEach(DUNGEON_REGISTRY::add);
    }
}