package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonFloorTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonBranchRegistry.*;

public class DungeonFloorRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonFloorTemplate TEST_FLOOR = DungeonFloorTemplate.build(
            "test_floor",//must be lowercase or the game will crash
            new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(STARTER_BRANCH)
                    .add(new WeightedPool<DungeonBranchTemplate>()
                            .add(TEST_BRANCH,1),1)
                    .addSimple(ENDING_BRANCH),
            null,
            null,
            1.0f);
    public static final DungeonFloorTemplate OVERWORLD_BASIC_FLOOR = DungeonFloorTemplate.build(
            "overworld_basic_floor",
            new DungeonLayout<DungeonBranchTemplate>()
                    .addSimple(OVERWORLD_STARTER_BRANCH)
                    .addSimple(OVERWORLD_SPRAWL_0)
                    .addSimple(OVERWORLD_SPRAWL_1)
                    .addSimple(OVERWORLD_SPRAWL_2)
                    .addSimple(OVERWORLD_SPRAWL_3)
                    .addSimple(OVERWORLD_ENDING_BRANCH),
            null,
            null,
            1.0f);

    public static void setupFloors(){
        DUNGEON_FLOOR_REGISTRY.add(TEST_FLOOR);
        DUNGEON_FLOOR_REGISTRY.add(OVERWORLD_BASIC_FLOOR);
    }
}
