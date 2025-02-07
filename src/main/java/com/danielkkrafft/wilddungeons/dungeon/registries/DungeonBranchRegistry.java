package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.*;
import static com.danielkkrafft.wilddungeons.util.WeightedPool.*;
import static com.mojang.datafixers.util.Pair.*;

public class DungeonBranchRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonBranchTemplate> DUNGEON_BRANCH_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonBranchTemplate STARTER_BRANCH = create("STARTER_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(START));

    public static final DungeonBranchTemplate TEST_BRANCH = create("TEST_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(
                                    of(SMALL_ROOM_POOL, 15),
                                    of(MEDIUM_ROOM_POOL, 15),
                                    of(SECRET_POOL, 2),
                                    of(COMBAT_ROOM_POOL, 5),
                                    of(PARKOUR_POOL, 5),
                                    of(LOOT_POOL, 5),
                                    of(REST_POOL, 5)),
                            30)
                    .addSimple(SHOP_1));

    public static final DungeonBranchTemplate ENDING_BRANCH = create("ENDING_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(
                                    of(SMALL_ROOM_POOL, 15),
                                    of(MEDIUM_ROOM_POOL, 15),
                                    of(SECRET_POOL, 2),
                                    of(COMBAT_ROOM_POOL, 5),
                                    of(PARKOUR_POOL, 5),
                                    of(LOOT_POOL, 5),
                                    of(REST_POOL, 5)),
                            10));

    public static final DungeonBranchTemplate OVERWORLD_STARTER_BRANCH = create("OVERWORLD_STARTER_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(OVERWORLD_START))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0);

    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_0 = create("OVERWORLD_FREE_STUFF_BRANCH_0")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(OVERWORLD_LOOT_ROOM_POOL, 1)
                    .add(OVERWORLD_SHOP_ROOM_POOL, 1)
                    .addSimple(OVERWORLD_TRANSITION_ROOM))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0)
            .setDifficultyScaling(1.0f);
    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_1 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0,"OVERWORLD_FREE_STUFF_BRANCH_1")
            .setMaterials(OVERWORLD_MATERIAL_POOL_1)
            .setDifficultyScaling(1.0f);
    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_2 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0,"OVERWORLD_FREE_STUFF_BRANCH_2")
            .setMaterials(OVERWORLD_MATERIAL_POOL_2)
            .setDifficultyScaling(1.0f);

    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_0 = create("OVERWORLD_SPRAWL_0")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 15))
            .setMaterials(OVERWORLD_MATERIAL_POOL_0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_1 = copyOf(OVERWORLD_SPRAWL_0,"OVERWORLD_SPRAWL_1")
            .setMaterials(OVERWORLD_MATERIAL_POOL_1);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_2 = copyOf(OVERWORLD_SPRAWL_0,"OVERWORLD_SPRAWL_2")
            .setMaterials(OVERWORLD_MATERIAL_POOL_2);
    public static final DungeonBranchTemplate OVERWORLD_ENDING_BRANCH = create("OVERWORLD_ENDING_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(OVERWORLD_EXIT_ROOM))
            .setMaterials(OVERWORLD_MATERIAL_POOL_2);

    public static final DungeonBranchTemplate SANDY_STARTER_BRANCH = copyOf(OVERWORLD_STARTER_BRANCH, "SANDY_STARTER_BRANCH")
            .setMaterials(SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_FREE_STUFF_BRANCH_0 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0, "SANDY_FREE_STUFF_BRANCH_0")
            .setMaterials(SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_SPRAWL_0 = copyOf(OVERWORLD_SPRAWL_0, "SANDY_SPRAWL_0")
            .setMaterials(SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_ENDING_BRANCH = copyOf(OVERWORLD_ENDING_BRANCH, "SANDY_ENDING_BRANCH")
            .setMaterials(SANDY_MATERIAL_POOL);

    public static final DungeonBranchTemplate RED_SANDY_STARTER_BRANCH = copyOf(OVERWORLD_STARTER_BRANCH, "RED_SANDY_STARTER_BRANCH")
            .setMaterials(RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_FREE_STUFF_BRANCH_0 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0, "RED_SANDY_FREE_STUFF_BRANCH_0")
            .setMaterials(RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_SPRAWL_0 = copyOf(OVERWORLD_SPRAWL_0, "RED_SANDY_SPRAWL_0")
            .setMaterials(RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_ENDING_BRANCH = copyOf(OVERWORLD_ENDING_BRANCH, "RED_SANDY_ENDING_BRANCH")
            .setMaterials(RED_SANDY_MATERIAL_POOL);

    public static void setupBranches() {
        add(STARTER_BRANCH);
        add(TEST_BRANCH);
        add(ENDING_BRANCH);
        add(OVERWORLD_STARTER_BRANCH);
        add(OVERWORLD_SPRAWL_0);
        add(OVERWORLD_SPRAWL_1);
        add(OVERWORLD_SPRAWL_2);
        add(OVERWORLD_ENDING_BRANCH);
        add(OVERWORLD_FREE_STUFF_BRANCH_0);
        add(OVERWORLD_FREE_STUFF_BRANCH_1);
        add(OVERWORLD_FREE_STUFF_BRANCH_2);
        add(SANDY_STARTER_BRANCH);
        add(SANDY_FREE_STUFF_BRANCH_0);
        add(SANDY_SPRAWL_0);
        add(SANDY_ENDING_BRANCH);
        add(RED_SANDY_STARTER_BRANCH);
        add(RED_SANDY_FREE_STUFF_BRANCH_0);
        add(RED_SANDY_SPRAWL_0);
        add(RED_SANDY_ENDING_BRANCH);
    }

    public static void add(DungeonBranchTemplate branch) {
        DUNGEON_BRANCH_REGISTRY.add(branch);
    }
}