package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration.DungeonLayout;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.process.BaseColumnHereStep;
import com.danielkkrafft.wilddungeons.dungeon.components.process.CreateBorderStep;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonBranchTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonMaterialPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomPoolRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.*;
import static com.danielkkrafft.wilddungeons.dungeon.registries.LootTableRegistry.METRO_LOOT_TABLE;
import static com.danielkkrafft.wilddungeons.dungeon.registries.LootTableRegistry.SEWER_LOOT_TABLE;
import static com.danielkkrafft.wilddungeons.util.WeightedPool.combine;
import static com.mojang.datafixers.util.Pair.of;

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

    public static final DungeonBranchTemplate PIGLIN_FACTORY_SIDE_FREE_PERK = create("PIGLIN_FACTORY_SIDE_FREE_PERK")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_FACTORY_FREE_PERK_1)
            )
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL);

    public static final DungeonBranchTemplate NETHER_DRAGON_BOSS_BRANCH = create("NETHER_DRAGON_BOSS_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(NETHER_FACTORY_BOSS_ROOM))
            .setRootOriginBranchIndex(6)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.NETHER_DRAGON);

    public static final DungeonBranchTemplate VILLAGE_SEWER_START_BRANCH = create("VILLAGE_SEWER_START_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(VILLAGE_SEWER_START))
            .set(MATERIAL, VILLAGE_SEWER_MATERIAL_POOL)
            .set(HAS_BEDROCK_SHELL, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_OVERFLOW);

    public static final DungeonBranchTemplate VILLAGE_SEWER_ALL = create("VILLAGE_SEWER_ALL")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(VILLAGE_SEWER_POOL,10)
                    .addSimple(VILLAGE_SEWER_PERK))
            .set(MATERIAL, VILLAGE_SEWER_MATERIAL_POOL)
            .set(HierarchicalProperty.ENEMY_TABLE, EnemyTableRegistry.BASIC_ENEMY_TABLE)
            .set(LOOT_TABLE,SEWER_LOOT_TABLE)
            .set(ROOM_TARGET_Y, 50)
            .set(MAX_Y, 100)
            .set(FLOOR_DISTANCE_WEIGHT, 400)
            .set(HAS_BEDROCK_SHELL, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_OVERFLOW);

    public static final DungeonBranchTemplate VILLAGE_SEWER_ENDING_BRANCH = create("VILLAGE_SEWER_ENDING_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(VILLAGE_SEWER_13)
                    .addSimple(VILLAGE_PIPE_TO_METRO)
                    .addSimple(VILLAGE_PIPE_TO_METRO))
            .set(MATERIAL, VILLAGE_SEWER_MATERIAL_POOL)
            .set(FLOOR_DISTANCE_WEIGHT, 400)
            .set(HAS_BEDROCK_SHELL, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_OVERFLOW);

    private static List<BlockState> CreateLayeredColumn() {
        List<BlockState> column = new ArrayList<>();

        // Adds the 5 layers of water
        column.addAll(Collections.nCopies(5, Blocks.WATER.defaultBlockState()));

        // Adds the 4 layers of sand
        column.addAll(Collections.nCopies(4, Blocks.SAND.defaultBlockState()));

        // Adds the 3 layers of sandstone
        column.addAll(Collections.nCopies(3, Blocks.SANDSTONE.defaultBlockState()));

        // Adds the 6 layers of stone
        column.addAll(Collections.nCopies(6, Blocks.STONE.defaultBlockState()));

        // Adds the 8 layers of bedrock
        column.addAll(Collections.nCopies(8, Blocks.BEDROCK.defaultBlockState()));

        return column;
    }

    public static final DungeonBranchTemplate VILLAGE_METRO_START_BRANCH = create("VILLAGE_METRO_START_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(VILLAGE_METRO_CENTER)
            )
            .set(PRE_WORLD_GEN_PROCESSING_STEPS, List.of(new BaseColumnHereStep(CreateLayeredColumn())))
            .set(PRE_ROOM_GEN_PROCESSING_STEPS, List.of(new CreateBorderStep(3)))
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(BLOCKING_MATERIAL_INDEX, 2)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_THE_CAPITAL);

    public static final DungeonBranchTemplate VILLAGE_METRO_STREETS_BRANCH = create("VILLAGE_METRO_STREETS_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(VILLAGE_WIDE_PATH_POOL,7)
            )
            .addLimitedRoom(VILLAGE_METRO_WIDE_CROSSROADS, 2)
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(BLOCKING_MATERIAL_INDEX, 2)
            .set(FLOOR_DISTANCE_WEIGHT, 0)
            .set(BRANCH_DISTANCE_WEIGHT, -1000)
            .set(CHEST_SPAWN_CHANCE, 1.0)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .set(PRE_ROOM_GEN_PROCESSING_STEPS, List.of(new CreateBorderStep(0)))
            .set(PLACE_ANYWHERE, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_THE_CAPITAL)
            .setRootOriginBranchIndex(4);

    public static final DungeonBranchTemplate VILLAGE_MEDIUM_BRANCH = create("VILLAGE_MEDIUM_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(VILLAGE_MEDIUM_PLOTS,20)
            )
            .addLimitedRoom(VILLAGE_METRO_WIDE_CROSSROADS, 2)
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(BLOCKING_MATERIAL_INDEX, 2)
            .set(BRANCH_DISTANCE_WEIGHT, -50)
            .set(FLOOR_DISTANCE_WEIGHT, 0)
            .set(CHEST_SPAWN_CHANCE, 1.0)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .set(PRE_ROOM_GEN_PROCESSING_STEPS, List.of(new CreateBorderStep(0)))
            .set(PLACE_ANYWHERE, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_THE_CAPITAL)
            .setRootOriginBranchIndex(4);

    public static final DungeonBranchTemplate VILLAGE_SMALL_BRANCH = create("VILLAGE_SMALL_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .add(VILLAGE_SMALL_PLOTS,40)
            )
            .addLimitedRoom(VILLAGE_METRO_WIDE_CROSSROADS, 2)
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(BLOCKING_MATERIAL_INDEX, 2)
            .set(BRANCH_DISTANCE_WEIGHT, -50)
            .set(FLOOR_DISTANCE_WEIGHT, 0)
            .set(CHEST_SPAWN_CHANCE, 1.0)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .set(PRE_ROOM_GEN_PROCESSING_STEPS, List.of(new CreateBorderStep(0)))
            .set(PLACE_ANYWHERE, true)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_THE_CAPITAL)
            .setRootOriginBranchIndex(4);

    public static final DungeonBranchTemplate VILLAGE_METRO_ENDING_BRANCH = create("VILLAGE_METRO_ENDING_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(VILLAGE_METRO_TOWER_START)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .addSimple(VILLAGE_METRO_TOWER_PERK)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .addSimple(VILLAGE_METRO_MOBFIA_TWR_CEO_OFFICE)
            )
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_CUBICLES, 2)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_LIBRARY, 2)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_SEVERANCE, 2)
            .addLimitedRoom(VILLAGE_METRO_MOBFIA_TWR_OFFICE, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_SEVERANCE_HALLS, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_JAIL, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_LOBBY, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_VAULT, 2)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_ANGEL_INVESTOR_SAFE)
            .setRootOriginBranchIndex(4);

    public static final DungeonBranchTemplate VILLAGE_METRO_ENDING_GAUNTLET_BRANCH = create("VILLAGE_METRO_ENDING_GAUNTLET_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(VILLAGE_METRO_TOWER_START)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .addSimple(VILLAGE_METRO_TOWER_PERK)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .add(VILLAGE_TOWER_FLOORS, 1)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .addSimple(VILLAGE_METRO_TOWER_PERK)
                    .addSimple(VILLAGE_METRO_TOWER_STAIRS)
                    .addSimple(VILLAGE_METRO_MOBFIA_TWR_CEO_OFFICE)
            )
            .set(MATERIAL, VILLAGE_MATERIAL_POOL)
            .set(LOOT_TABLE,METRO_LOOT_TABLE)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_CUBICLES, 2)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_LIBRARY, 2)
            .addLimitedRoom(VILLAGE_METRO_CHLTER121_TWR_SEVERANCE, 2)
            .addLimitedRoom(VILLAGE_METRO_MOBFIA_TWR_OFFICE, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_SEVERANCE_HALLS, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_JAIL, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_LOBBY, 2)
            .addLimitedRoom(VILLAGE_METRO_FLYINGNOKK_TWR_VAULT, 2)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_ANGEL_INVESTOR_SAFE)
            .setRootOriginBranchIndex(6);
    public static final DungeonBranchTemplate VILLAGE_METRO_STREETS_GAUNTLET_BRANCH = copyOf(VILLAGE_METRO_STREETS_BRANCH, "VILLAGE_METRO_STREETS_GAUNTLET_BRANCH")
            .setRootOriginBranchIndex(6);
    public static final DungeonBranchTemplate VILLAGE_MEDIUM_GAUNTLET_BRANCH = copyOf(VILLAGE_MEDIUM_BRANCH, "VILLAGE_MEDIUM_GAUNTLET_BRANCH")
            .setRootOriginBranchIndex(6);
    public static final DungeonBranchTemplate VILLAGE_SMALL_GAUNTLET_BRANCH = copyOf(VILLAGE_SMALL_BRANCH, "VILLAGE_SMALL_GAUNTLET_BRANCH")
            .setRootOriginBranchIndex(6);

    public static final DungeonBranchTemplate GAUNTLET_SCIFI_LOOT_BRANCH = create("GAUNTLET_SCIFI_LOOT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(SCIFI_ARENA_LOOT_CHOICE_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_SCIFI_COMBAT_BRANCH = create("GAUNTLET_SCIFI_COMBAT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(SCIFI_ARENA_COMBAT_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_SCIFI_EXIT_BRANCH = create("GAUNTLET_SCIFI_EXIT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(SCIFI_ARENA_EXIT_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_TRIAL_LOOT_BRANCH = create("GAUNTLET_TRIAL_LOOT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(TRIAL_ARENA_LOOT_CHOICE_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_TRIAL_COMBAT_BRANCH = create("GAUNTLET_TRIAL_COMBAT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(TRIAL_ARENA_COMBAT_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_TRIAL_EXIT_BRANCH = create("GAUNTLET_TRIAL_EXIT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(TRIAL_ARENA_EXIT_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_GENERAL_LOOT_BRANCH = create("GAUNTLET_GENERAL_LOOT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(GENERAL_ARENA_LOOT_CHOICE_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_GENERAL_COMBAT_BRANCH = create("GAUNTLET_GENERAL_COMBAT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(GENERAL_ARENA_COMBAT_ROOM));

    public static final DungeonBranchTemplate GAUNTLET_GENERAL_EXIT_BRANCH = create("GAUNTLET_GENERAL_EXIT_BRANCH")
            .setRoomTemplates(new DungeonLayout<DungeonRoomTemplate>()
                    .addSimple(GENERAL_ARENA_EXIT_ROOM));

    public static DungeonBranchTemplate copyOf(DungeonBranchTemplate branch, String name) {
        DungeonBranchTemplate copy = DungeonBranchTemplate.copyOf(branch, name);
        DUNGEON_BRANCH_REGISTRY.add(copy);
        return copy;
    }

    public static DungeonBranchTemplate create(String name) {
        DungeonBranchTemplate branch = DungeonBranchTemplate.create(name);
        DUNGEON_BRANCH_REGISTRY.add(branch);
        return branch;
    }

}