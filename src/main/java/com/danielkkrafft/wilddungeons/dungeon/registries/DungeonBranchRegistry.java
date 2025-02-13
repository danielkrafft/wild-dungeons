package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.DIFFICULTY_SCALING;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.MATERIAL;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.*;
import static com.danielkkrafft.wilddungeons.util.WeightedPool.combine;
import static com.mojang.datafixers.util.Pair.of;

public class DungeonBranchRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonBranchTemplate> DUNGEON_BRANCH_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonBranchTemplate> dungeonBranches = new ArrayList<>();

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
                    .addSimple(BOSS));

    public static final DungeonBranchTemplate OVERWORLD_STARTER_BRANCH = create("OVERWORLD_STARTER_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(OVERWORLD_START))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0);

    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_0 = create("OVERWORLD_FREE_STUFF_BRANCH_0")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(OVERWORLD_LOOT_ROOM_POOL, 1)
                    .add(OVERWORLD_SHOP_ROOM_POOL, 1)
                    .addSimple(OVERWORLD_TRANSITION_ROOM))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0)
            .set(DIFFICULTY_SCALING, 1.0);
    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_1 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0,"OVERWORLD_FREE_STUFF_BRANCH_1")
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_1)
            .set(DIFFICULTY_SCALING, 1.0);
    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_2 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0,"OVERWORLD_FREE_STUFF_BRANCH_2")
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_2)
            .set(DIFFICULTY_SCALING, 1.0);

    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_0 = create("OVERWORLD_SPRAWL_0")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(combine(of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 15))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_1 = copyOf(OVERWORLD_SPRAWL_0,"OVERWORLD_SPRAWL_1")
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_1);
    public static final DungeonBranchTemplate OVERWORLD_SPRAWL_2 = copyOf(OVERWORLD_SPRAWL_0,"OVERWORLD_SPRAWL_2")
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_2);
    public static final DungeonBranchTemplate OVERWORLD_ENDING_BRANCH = create("OVERWORLD_ENDING_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(OVERWORLD_EXIT_ROOM))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_2);

    public static final DungeonBranchTemplate SANDY_STARTER_BRANCH = copyOf(OVERWORLD_STARTER_BRANCH, "SANDY_STARTER_BRANCH")
            .set(MATERIAL, SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_FREE_STUFF_BRANCH_0 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0, "SANDY_FREE_STUFF_BRANCH_0")
            .set(MATERIAL, SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_SPRAWL_0 = copyOf(OVERWORLD_SPRAWL_0, "SANDY_SPRAWL_0")
            .set(MATERIAL, SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate SANDY_ENDING_BRANCH = copyOf(OVERWORLD_ENDING_BRANCH, "SANDY_ENDING_BRANCH")
            .set(MATERIAL, SANDY_MATERIAL_POOL);

    public static final DungeonBranchTemplate RED_SANDY_STARTER_BRANCH = copyOf(OVERWORLD_STARTER_BRANCH, "RED_SANDY_STARTER_BRANCH")
            .set(MATERIAL, RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_FREE_STUFF_BRANCH_0 = copyOf(OVERWORLD_FREE_STUFF_BRANCH_0, "RED_SANDY_FREE_STUFF_BRANCH_0")
            .set(MATERIAL, RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_SPRAWL_0 = copyOf(OVERWORLD_SPRAWL_0, "RED_SANDY_SPRAWL_0")
            .set(MATERIAL, RED_SANDY_MATERIAL_POOL);
    public static final DungeonBranchTemplate RED_SANDY_ENDING_BRANCH = copyOf(OVERWORLD_ENDING_BRANCH, "RED_SANDY_ENDING_BRANCH")
            .set(MATERIAL, RED_SANDY_MATERIAL_POOL);

    public static final DungeonBranchTemplate VILLAGE_PATH_BRANCH = create("VILLAGE_PATH_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(VILLAGE_PATH_POOL, 10))
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .setLimitedRooms(List.of(of(VILLAGE_CENTER,1),of(VILLAGE_FORGE,1)))
            ;
    public static final DungeonBranchTemplate PIGLIN_FACTORY_START_BRANCH = create("PIGLIN_FACTORY_START_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_CAVE_ENTRANCE_ROOM));

    public static final DungeonBranchTemplate PIGLIN_FACTORY_CAVE_BRANCH = create("PIGLIN_FACTORY_CAVE_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL, 10));
    public static final DungeonBranchTemplate PIGLIN_FACTORY_CAVE_END_BRANCH = create("PIGLIN_FACTORY_CAVE_END_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL, 5)
                    .addSimple(NETHER_CAVE_END_ROOM));

    public static final DungeonBranchTemplate PIGLIN_FACTORY_PIPEWORKS_BRANCH = create("PIGLIN_FACTORY_PIPEWORKS_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_PIPEWORKS_3)
                    .add(PIGLIN_FACTORY_PIPEWORKS_ROOM_POOL, 10)
                    .addSimple(NETHER_PIPEWORKS_TO_FACTORY));
    public static final DungeonBranchTemplate PIGLIN_FACTORY_PIPEWORKS_SIDE_BRANCH = create("PIGLIN_FACTORY_PIPEWORKS_SIDE_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
//                    .addSimple(NETHER_PIPEWORKS_3)
                    .add(PIGLIN_FACTORY_PIPEWORKS_ROOM_POOL, 5)
                    .addSimple(NETHER_PIPEWORKS_COMBAT_0))
            .setRootOriginBranchIndex(2);

    public static DungeonBranchTemplate copyOf(DungeonBranchTemplate branch, String name) {
        DungeonBranchTemplate copy = DungeonBranchTemplate.copyOf(branch, name);
        dungeonBranches.add(copy);
        return copy;
    }

    public static DungeonBranchTemplate create(String name) {
        DungeonBranchTemplate branch = DungeonBranchTemplate.create(name);
        dungeonBranches.add(branch);
        return branch;
    }

    public static void setupBranches() {
        dungeonBranches.forEach(DUNGEON_BRANCH_REGISTRY::add);
    }

}