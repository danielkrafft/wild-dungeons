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

    public static final DungeonBranchTemplate STARTER_BRANCH = build(
            "STARTER_BRANCH",
            new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(START),
            null,
            null,
            1.0);
    public static final DungeonBranchTemplate TEST_BRANCH = build(
            "TEST_BRANCH",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(
                            of(SMALL_ROOM_POOL, 15),
                            of(MEDIUM_ROOM_POOL, 15),
                            of(SECRET_POOL, 2),
                            of(COMBAT_ROOM_POOL, 5),
                            of(PARKOUR_POOL, 5),
                            of(LOOT_POOL, 5),
                            of(REST_POOL, 5)),
                            30)
                    .addSimple(SHOP_1),
            null,
            null,
            1.0);
    public static final DungeonBranchTemplate ENDING_BRANCH = build(
            "ENDING_BRANCH",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(
                            of(SMALL_ROOM_POOL, 15),
                            of(MEDIUM_ROOM_POOL, 15),
                            of(SECRET_POOL, 2),
                            of(COMBAT_ROOM_POOL, 5),
                            of(PARKOUR_POOL, 5),
                            of(LOOT_POOL, 5),
                            of(REST_POOL, 5)),
                            10),
            null,
            null,
            1.0);

    public static final DungeonBranchTemplate OVERWORLD_STARTER_BRANCH = build(
            "OVERWORLD_STARTER_BRANCH",
            new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(OVERWORLD_START),
            OVERWORLD_MATERIAL_POOL_0,
            null,
            1.0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_0 = build(
            "OVERWORLD_SPRAWL_0",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 10),
            OVERWORLD_MATERIAL_POOL_0,
            null,
            1.0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_1 = build(
            "OVERWORLD_SPRAWL_1",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 15)
                    .add(combine(of(OVERWORLD_SHOP_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_LOOT_ROOM_POOL, 100)), 1),
            OVERWORLD_MATERIAL_POOL_1,
            null,
            1.0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_2 = build(
            "OVERWORLD_SPRAWL_2",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 15)
                    .add(combine(of(OVERWORLD_LOOT_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_REST_ROOM_POOL, 100)), 1),
            OVERWORLD_MATERIAL_POOL_1,
            null,
            1.0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_3 = build(
            "OVERWORLD_SPRAWL_3",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 20)
                    .add(combine(of(OVERWORLD_LOOT_ROOM_POOL, 100)), 2),
            OVERWORLD_MATERIAL_POOL_2,
            null,
            1.0);
    public static final DungeonBranchTemplate OVERWORLD_ENDING_BRANCH = build(
            "OVERWORLD_ENDING_BRANCH",
            new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPACER_ROOM_POOL, 100)), 2)
                    .add(combine(of(OVERWORLD_REST_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_LOOT_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_SPACER_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_SHOP_ROOM_POOL, 100)), 1)
                    .add(combine(of(OVERWORLD_SPACER_ROOM_POOL, 100)), 2)
                    .addSimple(OVERWORLD_EXIT_ROOM),
            OVERWORLD_MATERIAL_POOL_2,
            null,
            1.0);


    public static void setupBranches(){
        DUNGEON_BRANCH_REGISTRY.add(STARTER_BRANCH);
        DUNGEON_BRANCH_REGISTRY.add(TEST_BRANCH);
        DUNGEON_BRANCH_REGISTRY.add(ENDING_BRANCH);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_STARTER_BRANCH);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_SPRAWL_0);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_SPRAWL_1);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_SPRAWL_2);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_SPRAWL_3);
        DUNGEON_BRANCH_REGISTRY.add(OVERWORLD_ENDING_BRANCH);
    }
}