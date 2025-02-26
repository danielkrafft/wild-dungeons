package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonBranch;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRoom;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import java.util.ArrayList;
import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
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
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0)
            .set(INTENSITY, 0);

    public static final DungeonBranchTemplate OVERWORLD_FREE_STUFF_BRANCH_0 = create("OVERWORLD_FREE_STUFF_BRANCH_0")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(OVERWORLD_LOOT_ROOM_POOL, 1)
                    .add(OVERWORLD_SHOP_ROOM_POOL, 1)
                    .addSimple(OVERWORLD_TRANSITION_ROOM))
            .set(MATERIAL, OVERWORLD_MATERIAL_POOL_0)
            .set(DIFFICULTY_SCALING, 1.0)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL);
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
    public static final DungeonBranchTemplate OVERWORLD_TRIAL_BRANCH = create("OVERWORLD_TRIAL")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(TRIAL_ENTRY)
                    .addSimple(TRIAL_TRANSITION)
                    .add(new WeightedPool<DungeonRoomTemplate>().add(TRIAL_HALL, 1), 5)
                    .addSimple(TRIAL_SIDE_LOOT)
                    .add(new WeightedPool<DungeonRoomTemplate>().add(TRIAL_SIDE_LOOT_PIECE, 1), 4)
                    .addSimple(TRIAL_BOSS_ENTRY)
                    .addSimple(TRIAL_BOSS)
            )
            .set(MATERIAL, new WeightedPool<DungeonMaterial>().add(DungeonMaterialRegistry.TRIAL_MATERIAL, 1))
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.MOONLIGHT_SONATA_1ST);
    public static final DungeonBranchTemplate OVERWORLD_TRIAL_EXIT_BRANCH = create("OVERWORLD_TRIAL_EXIT")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(TRIAL_EXIT));
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
            .set(MATERIAL, VILLAGE_MATERIAL_POOL);
    public static final DungeonBranchTemplate PIGLIN_FACTORY_START_BRANCH = create("PIGLIN_FACTORY_START_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_CAVE_ENTRANCE_ROOM));

    public static final DungeonBranchTemplate PIGLIN_FACTORY_CAVE_BRANCH = create("PIGLIN_FACTORY_CAVE_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL, 8))
            .set(INTENSITY, 1);
    public static final DungeonBranchTemplate PIGLIN_FACTORY_CAVE_END_BRANCH = create("PIGLIN_FACTORY_CAVE_END_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL, 8)
                    .addSimple(NETHER_CAVE_END_ROOM)
            ).set(INTENSITY, 2);

    public static final DungeonBranchTemplate PIGLIN_FACTORY_PIPEWORKS_FREE_CHEST_BRANCH = create("PIGLIN_FACTORY_PIPEWORKS_FREE_CHEST_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_PIPEWORKS_FREE_CHEST)
            ).setRootOriginBranchIndex(2)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL)
            .set(INTENSITY, 1);

    public static final DungeonBranchTemplate PIGLIN_FACTORY_PIPEWORKS_FREE_PERK_BRANCH = create("PIGLIN_FACTORY_PIPEWORKS_FREE_PERK_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_PIPEWORKS_FREE_PERK)
            ).setRootOriginBranchIndex(2)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL)
            .set(INTENSITY, 1);

    public static final DungeonBranchTemplate PIGLIN_FACTORY_PIPEWORKS_BRANCH = create("PIGLIN_FACTORY_PIPEWORKS_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_PIPEWORKS_3)
                    .add(PIGLIN_FACTORY_PIPEWORKS_ROOM_POOL, 10)
                    .addSimple(NETHER_PIPEWORKS_3)
                    .addSimple(NETHER_PIPEWORKS_TO_FACTORY)
            )
            .setRootOriginBranchIndex(2)
            .set(INTENSITY, 2);

    public static final DungeonBranchTemplate PIGLIN_FACTORY_BRANCH = create("PIGLIN_FACTORY_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_SPRAWL_ROOM_POOL, 20)
                    .addSimple(NETHER_FACTORY_TRI_BRANCH)
            )
            .set(INTENSITY, 2)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PIGLIN_FACTORY);

    public static final DungeonBranchTemplate PIGLIN_FACTORY_SIDE_BRANCH = create("PIGLIN_FACTORY_SIDE_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(PIGLIN_FACTORY_SPRAWL_ROOM_POOL, 10)
                    .addSimple(NETHER_FACTORY_KEY_COMBAT)
            )
            .setRootOriginBranchIndex(6)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PIGLIN_FACTORY)
            .set(INTENSITY, 2);

    public static final DungeonBranchTemplate NETHER_DRAGON_BOSS_BRANCH = create("NETHER_DRAGON_BOSS_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_FACTORY_BOSS_ROOM))
            .setRootOriginBranchIndex(6)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.NETHER_DRAGON);

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