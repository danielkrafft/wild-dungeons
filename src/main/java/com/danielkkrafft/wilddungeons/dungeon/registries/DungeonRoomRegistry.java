package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.room.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate.DestructionRule;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper.EMPTY_BLOCK_POS;
import static com.mojang.datafixers.util.Pair.of;


public class DungeonRoomRegistry { //TODO this should probably be a json/nbt based data oriented approach, with a built-in editor item. This is already getting extremely difficult to maintain
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonRoomTemplate> dungeonRooms = new ArrayList<>();

    /**
     *  INITIAL TEST ROOMS
     */

    public static final DungeonRoomTemplate PERK_TEST = createSimple("loot/perk_tester").setClazz(LootRoom.class);
    public static final DungeonRoomTemplate SMALL_1 = createSimple("stone/small_1");
    public static final DungeonRoomTemplate SMALL_2 = createSimple("stone/small_2");
    public static final DungeonRoomTemplate SMALL_3 = createSimple("stone/small_3");
    public static final DungeonRoomTemplate SMALL_4 = createSimple("stone/small_4");
    public static final DungeonRoomTemplate SMALL_5 = create(
            "stone/small_5",
            List.of(
                    of("stone/composite_1", EMPTY_BLOCK_POS),
                    of("stone/composite_2", new BlockPos(2, 0, 4))
            ));
    public static final DungeonRoomTemplate MEDIUM_1 = createSimple("stone/medium_1");
    public static final DungeonRoomTemplate MEDIUM_2 = createSimple("stone/medium_2");
    public static final DungeonRoomTemplate MEDIUM_3 = createSimple("stone/medium_3");
    public static final DungeonRoomTemplate MEDIUM_4 = create(
            "stone/medium_4",
            List.of(
                    of("stone/medium_4_comp_1", EMPTY_BLOCK_POS),
                    of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))
            ));
    public static final DungeonRoomTemplate LARGE_1 = createSimple("stone/large_1").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate START = createSimple("stone/start");
    public static final DungeonRoomTemplate BOSS = create(
            "stone/boss",
            List.of(
                    of("stone/boss_comp_1", EMPTY_BLOCK_POS),
                    of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                    of("stone/boss_comp_3", new BlockPos(20, 20, -16))
            ));
    public static final DungeonRoomTemplate SECRET_1 = createSimple("secret/1").setClazz(SecretRoom.class)
            .set(SHOP_TABLE, OfferingTemplateTableRegistry.FREE_CUSTOM_WEAPON_TABLE);
    public static final DungeonRoomTemplate SHOP_1 = createSimple("shop/1");
    public static final DungeonRoomTemplate LOOT_1 = createSimple("single_perk")
            .setClazz(LootRoom.class);
    public static final DungeonRoomTemplate REST = create(
            "rest",
            List.of(
                    of("rest/1", EMPTY_BLOCK_POS),
                    of("rest/2", new BlockPos(-7, 0, 3)),
                    of("rest/3", new BlockPos(3, 0, -7)),
                    of("rest/4", new BlockPos(12, 0, 3)),
                    of("rest/5", new BlockPos(3, 0, 12))
            ));
    public static final DungeonRoomTemplate PARKOUR = createSimple("parkour/1");



    public static final DungeonRoomTemplate OVERWORLD_START = createSimple("overworld/start");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_1 = createSimple("overworld/sprawl/basic_1");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_2 = createSimple("overworld/sprawl/basic_2");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_3 = createSimple("overworld/sprawl/basic_3");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_4 = createSimple("overworld/sprawl/basic_4");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_5 = createSimple("overworld/sprawl/basic_5");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_6 = createSimple("overworld/sprawl/basic_6");
    public static final DungeonRoomTemplate OVERWORLD_BASIC_7 = createSimple("overworld/sprawl/basic_7");
    public static final DungeonRoomTemplate OVERWORLD_PARKOUR = createSimple("overworld/sprawl/parkour");
    public static final DungeonRoomTemplate OVERWORLD_SECRET = createSimple("overworld/sprawl/secret").set(SHOP_TABLE, OfferingTemplateTableRegistry.FREE_CUSTOM_WEAPON_TABLE).setClazz(SecretRoom.class);
    public static final DungeonRoomTemplate OVERWORLD_SMELTER_ROOM = createSimple("overworld/sprawl/smelter");
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = copyOf(OVERWORLD_BASIC_3, "overworld_combat_1").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_2 = copyOf(OVERWORLD_BASIC_4, "overworld_combat_2").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).set(WAVE_SIZE, 5).set(DIFFICULTY_MODIFIER, 0.5);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_3 = copyOf(OVERWORLD_BASIC_5, "overworld_combat_3").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).set(WAVE_SIZE, 7).set(DIFFICULTY_MODIFIER, 0.75);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_4 = copyOf(OVERWORLD_BASIC_7, "overworld_combat_4").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).set(WAVE_SIZE, 7);
    public static final DungeonRoomTemplate OVERWORLD_CHEST_ROOM = createSimple("overworld/sprawl/chest_room");
    public static final DungeonRoomTemplate OVERWORLD_STAIRCASE = createSimple("overworld/sprawl/stair_room");
    public static final DungeonRoomTemplate OVERWORLD_STAIRWAY_1 = createSimple("overworld/sprawl/stairway_1");
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_1 = createSimple("overworld/sprawl/hallway_1");
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_2 = createSimple("overworld/sprawl/hallway_2");
    public static final DungeonRoomTemplate OVERWORLD_CRAFTING_ROOM = createSimple("overworld/crafting");
    public static final DungeonRoomTemplate OVERWORLD_REST_ROOM = createSimple("overworld/rest_basic");
    public static final DungeonRoomTemplate OVERWORLD_SHOP_ROOM = createSimple("overworld/shop");
    public static final DungeonRoomTemplate OVERWORLD_FREE_PERK = createSimple("overworld/free_perk").setClazz(LootRoom.class);
    public static final DungeonRoomTemplate OVERWORLD_DOUBLE_LOOT = createSimple("overworld/double_loot").setClazz(LootChoiceRoom.class);
    public static final DungeonRoomTemplate OVERWORLD_EXIT_ROOM = createSimple("overworld/exit");
    public static final DungeonRoomTemplate OVERWORLD_TRANSITION_ROOM = create(
            "transition",
            List.of(
                    of("overworld/transition", EMPTY_BLOCK_POS),
                    of("overworld/transition2", new BlockPos(7, -13, 3))
            ))
            .set(INTENSITY, 0);
    public static final DungeonRoomTemplate TRIAL_ENTRY = create(
            "trial_entry",
            List.of(
                    of("overworld/trial/entry_1", EMPTY_BLOCK_POS),
                    of("overworld/trial/entry_2", new BlockPos(6, 0, 14))
            ))
            .set(INTENSITY, 0);
    public static final DungeonRoomTemplate TRIAL_TRANSITION = createSimple("overworld/trial/transition");
    public static final DungeonRoomTemplate TRIAL_HALL = createSimple("overworld/trial/hall");
    public static final DungeonRoomTemplate TRIAL_BOSS_ENTRY = createSimple("overworld/trial/boss_entry");
    public static final DungeonRoomTemplate TRIAL_BOSS = createSimple("overworld/trial/boss").setClazz(BossRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).set(BOSS_SPAWN_POS, new Vec3(15, 10, 15))
            .set(ENEMY_TABLE, EnemyTableRegistry.BREEZE_GOLEM_ARENA)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.MOONLIGHT_SONATA_3RD)
            .set(INTENSITY, 0);
    public static final DungeonRoomTemplate TRIAL_SIDE_LOOT = createSimple("overworld/trial/side_loot");
    public static final DungeonRoomTemplate TRIAL_SIDE_LOOT_PIECE = createSimple("overworld/trial/side_loot_piece");
    public static final DungeonRoomTemplate TRIAL_EXIT = create(
            "trial_exit",
            List.of(
                    of("overworld/trial/exit_1", EMPTY_BLOCK_POS),
                    of("overworld/trial/exit_2", new BlockPos(-4, -7, -27))
            ))
            .set(INTENSITY, 0)
            .set(SHOP_TABLE, OfferingTemplateTableRegistry.FREE_CUSTOM_WEAPON_TABLE);

    public static final DungeonRoomTemplate NETHER_CAVE_ENTRANCE_ROOM = createSimple("nether/cave/entrance");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_1 = createSimple("nether/cave/sprawl_1");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_2 = createSimple("nether/cave/sprawl_2");
    public static final DungeonRoomTemplate NETHER_CAVE_COMBAT_2 = copyOf(NETHER_CAVE_SPRAWL_2, "nether_cave_combat_2").set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).setClazz(CombatRoom.class).set(DIFFICULTY_MODIFIER, 0.5).set(WAVE_SIZE, 5);
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_3 = createSimple("nether/cave/sprawl_3");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_4 = createSimple("nether/cave/sprawl_4");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_5 = createSimple("nether/cave/sprawl_5");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_6 = createSimple("nether/cave/sprawl_6");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_7 = createSimple("nether/cave/sprawl_7");
    public static final DungeonRoomTemplate NETHER_CAVE_COMBAT_1 = copyOf(NETHER_CAVE_SPRAWL_7, "nether_cave_combat_1").set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).setClazz(CombatRoom.class);

    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_8 = createSimple("nether/cave/sprawl_8");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_9 = createSimple("nether/cave/sprawl_9");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_10 = createSimple("nether/cave/sprawl_10");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_11 = createSimple("nether/cave/sprawl_11");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_12 = createSimple("nether/cave/sprawl_12");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_13 = createSimple("nether/cave/sprawl_13");
    public static final DungeonRoomTemplate NETHER_CAVE_END_ROOM = createSimple("nether/cave/end_composite")
            .set(INTENSITY, 3);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_BREAKOUT_ROOM = createSimple("nether/pipeworks/breakout").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_0 = createSimple("nether/pipeworks/breakout").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_1 = createSimple("nether/pipeworks/1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_2 = createSimple("nether/pipeworks/2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_3 = createSimple("nether/pipeworks/3").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_4 = createSimple("nether/pipeworks/4").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_5 = createSimple("nether/pipeworks/5").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_6 = createSimple("nether/pipeworks/6").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_FREE_CHEST = createSimple("nether/pipeworks/free_chest").set(DIFFICULTY_MODIFIER, 4.0).set(CHEST_SPAWN_CHANCE, 1.0);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_FREE_PERK = createSimple("nether/pipeworks/free_perk").set(SHOP_TABLE, OfferingTemplateTableRegistry.FREE_PERK_OFFERING_TABLE);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_LAVAFALLS = createSimple("nether/pipeworks/lava_falls").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_TO_FACTORY = createSimple("nether/pipeworks/pipe_to_factory").set(BLOCKING_MATERIAL_INDEX, 1).set(INTENSITY, 1).set(SOUNDSCAPE, SoundscapeTemplateRegistry.PIGLIN_FACTORY);
    public static final DungeonRoomTemplate NETHER_FACTORY_PARKOUR_1 = createSimple("nether/factory/parkour1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_PARKOUR_2 = createSimple("nether/factory/parkour2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_REST_1 = createSimple("nether/factory/rest1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SHOP_1 = createSimple("nether/factory/shop1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SHOP_2 = createSimple("nether/factory/shop2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_1 = createSimple("nether/factory/sprawl1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_2 = createSimple("nether/factory/sprawl2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_3 = createSimple("nether/factory/sprawl3").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_4 = createSimple("nether/factory/sprawl4").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_5 = createSimple("nether/factory/sprawl5").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_6 = createSimple("nether/factory/sprawl6").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_FREE_PERK_1 = createSimple("nether/factory/free_perk_1").set(BLOCKING_MATERIAL_INDEX, 1).set(SHOP_TABLE, OfferingTemplateTableRegistry.FREE_PERK_OFFERING_TABLE);
    public static final DungeonRoomTemplate NETHER_FACTORY_RAIL_1 = createSimple("nether/factory/rail_1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_RAIL_TRANSITION = createSimple("nether/factory/rail_transition").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_TRAP_1 = createSimple("nether/factory/trap1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_1 = copyOf(NETHER_FACTORY_SPRAWL_3, "nether_factory_combat_1").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_2 = copyOf(NETHER_FACTORY_SPRAWL_2, "nether_factory_combat_2").setClazz(CombatRoom.class).set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate NETHER_FACTORY_KEY_COMBAT = copyOf(NETHER_FACTORY_COMBAT_1, "nether_factory_key_combat").set(ROOM_CLEAR_REWARD_POOL, WeightedPool.of(OfferingTemplateRegistry.DUNGEON_KEY));
    public static final DungeonRoomTemplate NETHER_FACTORY_TRI_BRANCH = create("nether_factory_tri_branch",
            List.of(
                    of("nether/factory/tribranch_1", EMPTY_BLOCK_POS),
                    of("nether/factory/tribranch_2", new BlockPos(-8, 0, 4)),
                    of("nether/factory/tribranch_3", new BlockPos(-12, 0, 12)),
                    of("nether/factory/tribranch_4", new BlockPos(15, 0, 12)),
                    of("nether/factory/tribranch_5", new BlockPos(0, 0, 27))
            ))
            .setClazz(KeyRequiredRoom.class)
            .set(BLOCKING_MATERIAL_INDEX, 1)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.NETHER_DRAGON_LEADUP)
            .set(INTENSITY, 1)
            .set(DESTRUCTION_RULE,DestructionRule.PROTECT_ALL_CLEAR);
    public static final DungeonRoomTemplate NETHER_FACTORY_BOSS_ROOM = create("nether_factory_boss_room",
            List.of(
                    of("nether/factory/boss_1", EMPTY_BLOCK_POS),
                    of("nether/factory/boss_2", new BlockPos(0,18,0)),
                    of("nether/factory/boss_3", new BlockPos(-48,18,0)),
                    of("nether/factory/boss_4", new BlockPos(7,18,0)),
                    of("nether/factory/boss_5", new BlockPos(0,18,7)),
                    of("nether/factory/boss_6", new BlockPos(0,18,-48)),
                    of("nether/factory/boss_7", new BlockPos(-48,18,-48)),
                    of("nether/factory/boss_8", new BlockPos(7,18,-48)),
                    of("nether/factory/boss_9", new BlockPos(7,18,7)),
                    of("nether/factory/boss_10", new BlockPos(-48,18,7))
            ))
            .setClazz(BossRoom.class)
            .set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR)
            .set(ROOM_CLEAR_REWARD_POOL, WeightedPool.of(OfferingTemplateRegistry.EXIT_RIFT))
            .set(ENEMY_TABLE, EnemyTableRegistry.NETHER_DRAGON_ARENA)
            .set(BOSS_SPAWN_POS, new Vec3(3.0, 40.0, 3.0));

    public static final DungeonRoomTemplate VILLAGE_SEWER_1 = createSimple("village/sewer/1");
    public static final DungeonRoomTemplate VILLAGE_SEWER_2 = createSimple("village/sewer/2");
    public static final DungeonRoomTemplate VILLAGE_SEWER_3 = createSimple("village/sewer/3");
    public static final DungeonRoomTemplate VILLAGE_SEWER_4 = createSimple("village/sewer/4");
    public static final DungeonRoomTemplate VILLAGE_SEWER_5 = createSimple("village/sewer/5");
    public static final DungeonRoomTemplate VILLAGE_SEWER_6 = createSimple("village/sewer/6");
    public static final DungeonRoomTemplate VILLAGE_SEWER_7 = createSimple("village/sewer/7");
    public static final DungeonRoomTemplate VILLAGE_SEWER_8 = createSimple("village/sewer/8");
    public static final DungeonRoomTemplate VILLAGE_SEWER_9 = createSimple("village/sewer/9");
    public static final DungeonRoomTemplate VILLAGE_SEWER_10 = createSimple("village/sewer/10");
    public static final DungeonRoomTemplate VILLAGE_SEWER_11 = createSimple("village/sewer/11");
    public static final DungeonRoomTemplate VILLAGE_SEWER_12 = createSimple("village/sewer/12");
    public static final DungeonRoomTemplate VILLAGE_SEWER_13 = createSimple("village/sewer/13");
    public static final DungeonRoomTemplate VILLAGE_SEWER_CHESTCAP = createSimple("village/sewer/chestcap").setClazz(SecretRoom.class);
    public static final DungeonRoomTemplate VILLAGE_SEWER_ELEVATOR = createSimple("village/sewer/elevator");
    public static final DungeonRoomTemplate VILLAGE_SEWER_DEELEVATOR = createSimple("village/sewer/deelevator");
    public static final DungeonRoomTemplate VILLAGE_SEWER_START = createSimple("village/sewer/start");
    public static final DungeonRoomTemplate VILLAGE_PIPE_TO_METRO = createSimple("village/sewer/pipe_to_metro");

    public static final DungeonRoomTemplate VILLAGE_METRO_CENTER = createSimple("village/metro/center");

    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_PATH = createSimple("village/metro/path_wide")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_CROSSROADS = createSimple("village/metro/path_wide_crossroad")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_STAIRS = createSimple("village/metro/path_wide_stairs")//todo handle verticality in the metro
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_MED_CROSSROAD = createSimple("village/metro/med/path_crossroad")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_PATH_STAIRS = createSimple("village/metro/path_stairs")//todo handle verticality in the metro
            .set(BLOCKING_MATERIAL_INDEX, 2);

    public static final DungeonRoomTemplate VILLAGE_METRO_CHURCH = createSimple("village/metro/daniel/med/church");
    public static final DungeonRoomTemplate VILLAGE_METRO_DORM_TOWER = createSimple("village/metro/daniel/med/dorm_tower");
    public static final DungeonRoomTemplate VILLAGE_METRO_ILLAGER_TOWER = createSimple("village/metro/daniel/med/illager_tower");
    public static final DungeonRoomTemplate VILLAGE_METRO_LIBRARY = createSimple("village/metro/daniel/med/library");
    public static final DungeonRoomTemplate VILLAGE_METRO_LUMBERYARD = createSimple("village/metro/daniel/med/lumberyard");
    public static final DungeonRoomTemplate VILLAGE_METRO_SMITHERY = createSimple("village/metro/daniel/med/smithery");
    public static final DungeonRoomTemplate VILLAGE_METRO_TANNERY = createSimple("village/metro/daniel/med/tannery");
    public static final DungeonRoomTemplate VILLAGE_METRO_VERTICAL_FARM = createSimple("village/metro/daniel/med/vertical_farm");
    public static final DungeonRoomTemplate VILLAGE_METRO_COTTAGE = createSimple("village/metro/daniel/med/cottage");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_1 = createSimple("village/metro/awseme/med/1");//todo these could all be named, but they are all just... houses
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_2 = createSimple("village/metro/awseme/med/2");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_3 = createSimple("village/metro/awseme/med/3");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_4 = createSimple("village/metro/awseme/med/4");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_5 = createSimple("village/metro/awseme/med/5");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_5B = createSimple("village/metro/awseme/med/5b");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_6 = createSimple("village/metro/awseme/med/6");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_7 = createSimple("village/metro/awseme/med/7");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_8 = createSimple("village/metro/awseme/med/8");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_9 = createSimple("village/metro/awseme/med/9");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_10 = createSimple("village/metro/awseme/med/10");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_11 = createSimple("village/metro/awseme/med/11");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_12 = createSimple("village/metro/awseme/med/12");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_13 = createSimple("village/metro/awseme/med/13");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_14 = createSimple("village/metro/awseme/med/14");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_15 = createSimple("village/metro/awseme/med/15");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_16 = createSimple("village/metro/awseme/med/16");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_17 = createSimple("village/metro/awseme/med/17");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_18 = createSimple("village/metro/awseme/med/18");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_19 = createSimple("village/metro/awseme/med/19");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_20 = createSimple("village/metro/awseme/med/20");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_CAVE = createSimple("village/metro/awseme/med/cave");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_FOUNTAIN = createSimple("village/metro/awseme/med/fountain");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_HOLLOW_HILL = createSimple("village/metro/awseme/med/hollow_hill");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_QUARRY = createSimple("village/metro/awseme/med/quarry");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_STONECUTTER = createSimple("village/metro/awseme/med/stonecutter");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_BONFIRE = createSimple("village/metro/aiyqe/med/bonfire");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_DEEPSLATE_MINE = createSimple("village/metro/aiyqe/med/deepslate_mine");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_SALMON_BRIDGE = createSimple("village/metro/aiyqe/med/salmon_bridge");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_TRADING_POST = createSimple("village/metro/aiyqe/med/trading_post");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_DEAD_TREE = createSimple("village/metro/arcanist/med/dead_tree");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_FISHING_POND = createSimple("village/metro/arcanist/med/fishing_pond");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_SAFE_HOUSE = createSimple("village/metro/arcanist/med/safe_house");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_WITCH_TOWER = createSimple("village/metro/arcanist/med/witch_tower");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_BEEKEEPERS = createSimple("village/metro/chlter121/med/beekeepers");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_FOOD_COURT = createSimple("village/metro/chlter121/med/food_court");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_FOUR_PARKOUR = createSimple("village/metro/chlter121/med/four_parkour");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_GRAVEYARD = createSimple("village/metro/chlter121/med/graveyard");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_GRAVEYARD_BROKEN = createSimple("village/metro/chlter121/med/graveyard_broken");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HEDGE_MAZE = createSimple("village/metro/chlter121/med/hedge_maze");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE = createSimple("village/metro/chlter121/med/house");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_ARCHERY = createSimple("village/metro/chlter121/med/house_archery");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_CHERRY = createSimple("village/metro/chlter121/med/house_cherry");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_CONSTRUCTION = createSimple("village/metro/chlter121/med/house_construction");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_REEDS = createSimple("village/metro/chlter121/med/house_reeds");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_MARKET_STALLS = createSimple("village/metro/chlter121/med/market_stalls");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_PLAZA = createSimple("village/metro/chlter121/med/plaza");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_RUINED_HOUSE_1 = createSimple("village/metro/chlter121/med/ruined_house_1");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_RUINED_HOUSE_2 = createSimple("village/metro/chlter121/med/ruined_house_2");
//    public static final DungeonRoomTemplate VILLAGE_METRO_ = createSimple("village/metro/");

    public static final DungeonRoomTemplate VILLAGE_METRO_CROSS = createSimple("village/metro/daniel/sml/cross");
    public static final DungeonRoomTemplate VILLAGE_METRO_FOUNTAIN = createSimple("village/metro/daniel/sml/fountain");
    public static final DungeonRoomTemplate VILLAGE_METRO_HOME = createSimple("village/metro/daniel/sml/home");
    public static final DungeonRoomTemplate VILLAGE_METRO_TREE = createSimple("village/metro/daniel/sml/tree");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_ARCH = createSimple("village/metro/awseme/sml/arch");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_CART = createSimple("village/metro/awseme/sml/cart");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_CORNER_ARCH = createSimple("village/metro/awseme/sml/corner_arch");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_FOUR_ARCH = createSimple("village/metro/awseme/sml/four_arch");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_TRI_ARCH = createSimple("village/metro/awseme/sml/tri_arch");
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_TRI_FOUNTAIN = createSimple("village/metro/awseme/sml/tri_fountain");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_HALLOW_FARM = createSimple("village/metro/aiyqe/sml/hallow_farm");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_HALLOW_TREE = createSimple("village/metro/aiyqe/sml/hallow_tree");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_REST_HUT = createSimple("village/metro/aiyqe/sml/rest_hut");
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_TINYHOME = createSimple("village/metro/aiyqe/sml/tinyhome");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_CARROT = createSimple("village/metro/aiyqe/sml/carrot");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_CRYING_STATUE = createSimple("village/metro/aiyqe/sml/crying_statue");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_TEMPLE = createSimple("village/metro/aiyqe/sml/temple");
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_WET_CROSS = createSimple("village/metro/aiyqe/sml/wet_cross");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_CHERRY_TREE = createSimple("village/metro/chlter121/sml/cherry_tree");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_BEETROOT = createSimple("village/metro/chlter121/sml/farm_beetroot");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_BERRIES = createSimple("village/metro/chlter121/sml/farm_berries");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_CARROT = createSimple("village/metro/chlter121/sml/farm_carrot");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_MELON = createSimple("village/metro/chlter121/sml/farm_melon");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_POTATO = createSimple("village/metro/chlter121/sml/farm_potato");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_PUMPKIN = createSimple("village/metro/chlter121/sml/farm_pumpkin");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_SUGARCANE = createSimple("village/metro/chlter121/sml/farm_sugarcane");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_WHEAT = createSimple("village/metro/chlter121/sml/farm_wheat");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_INCINERATOR = createSimple("village/metro/chlter121/sml/incinerator");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_JAIL = createSimple("village/metro/chlter121/sml/jail");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_PLANTER_WARPED = createSimple("village/metro/chlter121/sml/planter_warped");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_STATUE_KNEELING = createSimple("village/metro/chlter121/sml/statue_kneeling");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_TOWER_OPEN = createSimple("village/metro/chlter121/sml/tower_open");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_TOWER_OPEN_B = createSimple("village/metro/chlter121/sml/tower_open_b");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_WELL = createSimple("village/metro/chlter121/sml/well");


    public static final DungeonRoomTemplate VILLAGE_METRO_TOWER_START = createSimple("village/tower/tower_start");
    public static final DungeonRoomTemplate VILLAGE_METRO_TOWER_END = createSimple("village/tower/tower_temp");
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_CEO_OFFICE = createSimple("village/metro/chlter121/twr/ceo_office");//todo process these and set up the connection points
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_CUBICLES = createSimple("village/metro/chlter121/twr/cubicles");//todo process these and set up the connection points
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_LIBRARY = createSimple("village/metro/chlter121/twr/library");//todo process these and set up the connection points
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_SEVERANCE = createSimple("village/metro/chlter121/twr/severance");//todo process these and set up the connection points




    public static DungeonRoomTemplate copyOf(DungeonRoomTemplate template, String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.copyOf(template, name);
        dungeonRooms.add(room);
        return room;
    }

    public static DungeonRoomTemplate create(String name, List<Pair<String, BlockPos>> roomTemplates) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, roomTemplates);
        dungeonRooms.add(room);
        return room;
    }

    public static DungeonRoomTemplate createSimple(String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, List.of(Pair.of(name, EMPTY_BLOCK_POS)));
        dungeonRooms.add(room);
        return room;
    }

    public static void setupDungeonRooms() {dungeonRooms.forEach(DUNGEON_ROOM_REGISTRY::add);}
}