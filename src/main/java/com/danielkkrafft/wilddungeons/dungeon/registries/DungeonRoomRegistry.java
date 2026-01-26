package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.process.AddEmeraldPiles;
import com.danielkkrafft.wilddungeons.dungeon.components.process.AddRandomVillagers;
import com.danielkkrafft.wilddungeons.dungeon.components.process.AddVillagersOfProfession;
import com.danielkkrafft.wilddungeons.dungeon.components.room.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.registry.WDProtectedRegion.RegionRule;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.util.WeightedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty.*;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper.EMPTY_BLOCK_POS;
import static com.mojang.datafixers.util.Pair.of;
import static net.minecraft.world.entity.npc.VillagerProfession.*;


public class DungeonRoomRegistry { //TODO this should probably be a json/nbt based data oriented approach, with a built-in editor item. This is already getting extremely difficult to maintain
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    /**
     * INITIAL TEST ROOMS
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
    public static final DungeonRoomTemplate LARGE_1 = createCombat("stone/large_1");
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
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = copyCombatOf(OVERWORLD_BASIC_3, "overworld_combat_1");
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_2 = copyCombatOf(OVERWORLD_BASIC_4, "overworld_combat_2").set(WAVE_SIZE, 5).set(DIFFICULTY_MODIFIER, 0.5);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_3 = copyCombatOf(OVERWORLD_BASIC_5, "overworld_combat_3").set(WAVE_SIZE, 7).set(DIFFICULTY_MODIFIER, 0.75);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_4 = copyCombatOf(OVERWORLD_BASIC_7, "overworld_combat_4").set(WAVE_SIZE, 7);
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
    public static final DungeonRoomTemplate TRIAL_BOSS = createBoss("overworld/trial/boss", new Vec3(15, 10, 15))
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
    public static final DungeonRoomTemplate NETHER_CAVE_COMBAT_2 = copyCombatOf(NETHER_CAVE_SPRAWL_2, "nether_cave_combat_2").set(DIFFICULTY_MODIFIER, 0.5).set(WAVE_SIZE, 5);
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_3 = createSimple("nether/cave/sprawl_3");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_4 = createSimple("nether/cave/sprawl_4");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_5 = createSimple("nether/cave/sprawl_5");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_6 = createSimple("nether/cave/sprawl_6");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_7 = createSimple("nether/cave/sprawl_7");
    public static final DungeonRoomTemplate NETHER_CAVE_COMBAT_1 = copyCombatOf(NETHER_CAVE_SPRAWL_7, "nether_cave_combat_1");

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
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_1 = copyCombatOf(NETHER_FACTORY_SPRAWL_3, "nether_factory_combat_1");
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_2 = copyCombatOf(NETHER_FACTORY_SPRAWL_2, "nether_factory_combat_2");
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
            .set(DESTRUCTION_RULE, RegionRule.PROTECT_ALL_CLEAR);
    public static final DungeonRoomTemplate NETHER_FACTORY_BOSS_ROOM = create("nether_factory_boss_room",
            List.of(
                    of("nether/factory/boss_1", EMPTY_BLOCK_POS),
                    of("nether/factory/boss_2", new BlockPos(0, 18, 0)),
                    of("nether/factory/boss_3", new BlockPos(-48, 18, 0)),
                    of("nether/factory/boss_4", new BlockPos(7, 18, 0)),
                    of("nether/factory/boss_5", new BlockPos(0, 18, 7)),
                    of("nether/factory/boss_6", new BlockPos(0, 18, -48)),
                    of("nether/factory/boss_7", new BlockPos(-48, 18, -48)),
                    of("nether/factory/boss_8", new BlockPos(7, 18, -48)),
                    of("nether/factory/boss_9", new BlockPos(7, 18, 7)),
                    of("nether/factory/boss_10", new BlockPos(-48, 18, 7))
            ))
            .setClazz(BossRoom.class)
            .set(DESTRUCTION_RULE, RegionRule.SHELL_CLEAR)
            .set(ROOM_CLEAR_REWARD_POOL, WeightedPool.of(OfferingTemplateRegistry.EXIT_RIFT))
            .set(ENEMY_TABLE, EnemyTableRegistry.NETHER_DRAGON_ARENA)
            .set(BOSS_SPAWN_POS, new Vec3(3.0, 40.0, 3.0));

    public static final DungeonRoomTemplate VILLAGE_SEWER_1 = createSimple("village/sewer/1");//four way room with spawners
    public static final DungeonRoomTemplate VILLAGE_SEWER_2 = createSimple("village/sewer/2");//vertical three floor room with open center
    public static final DungeonRoomTemplate VILLAGE_SEWER_3 = createSimple("village/sewer/3");//crossbar with spawners outside of it
    public static final DungeonRoomTemplate VILLAGE_SEWER_4 = createSimple("village/sewer/4");//broken pipe with 6 inlet pipes
    public static final DungeonRoomTemplate VILLAGE_SEWER_5 = createSimple("village/sewer/5");//barrel room
    public static final DungeonRoomTemplate VILLAGE_SEWER_6 = createSimple("village/sewer/6");//copper pipe
    public static final DungeonRoomTemplate VILLAGE_SEWER_7 = createSimple("village/sewer/7");//horizontal pipe with many connections
    public static final DungeonRoomTemplate VILLAGE_SEWER_8 = createSimple("village/sewer/8");//vertical pipe with many connections
    public static final DungeonRoomTemplate VILLAGE_SEWER_9 = createSimple("village/sewer/9");//double spawners with chests
    public static final DungeonRoomTemplate VILLAGE_SEWER_10 = createSimple("village/sewer/10");//salmon gate
    public static final DungeonRoomTemplate VILLAGE_SEWER_11 = createSimple("village/sewer/11");//pillars with canal
    public static final DungeonRoomTemplate VILLAGE_SEWER_12 = createSimple("village/sewer/12");//big open cube
    public static final DungeonRoomTemplate VILLAGE_SEWER_13 = createSimple("village/sewer/13");//small open cube
    public static final DungeonRoomTemplate VILLAGE_SEWER_12_COMBAT = copyCombatOf(VILLAGE_SEWER_12, "village_sewer_12_combat").set(ENEMY_TABLE, EnemyTableRegistry.VILLAGE_SEWER_ENEMY_TABLE).set(DIFFICULTY_MODIFIER, 0.5).set(WAVE_SIZE, 2);
    public static final DungeonRoomTemplate VILLAGE_SEWER_CHESTCAP = createSimple("village/sewer/chestcap").setClazz(SecretRoom.class).set(CHEST_SPAWN_CHANCE,1.0).set(DIFFICULTY_MODIFIER,5.0);
    public static final DungeonRoomTemplate VILLAGE_SEWER_ELEVATOR = createSimple("village/sewer/elevator");
    public static final DungeonRoomTemplate VILLAGE_SEWER_DEELEVATOR = createSimple("village/sewer/deelevator");
    public static final DungeonRoomTemplate VILLAGE_SEWER_START = createSimple("village/sewer/start");
    public static final DungeonRoomTemplate VILLAGE_SEWER_PERK = createSimple("village/sewer/perk").set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL).set(INTENSITY, 1).setClazz(LootRoom.class);
    public static final DungeonRoomTemplate VILLAGE_PIPE_TO_METRO = createSimple("village/sewer/pipe_to_metro");

    public static final DungeonRoomTemplate VILLAGE_METRO_CENTER = createSimple("village/metro/center");

    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_PATH = createSimple("village/metro/path_wide")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_CROSSROADS = createSimple("village/metro/path_wide_crossroad")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_WIDE_STAIRS = createSimple("village/metro/path_wide_stairs")//todo handle verticality in the metro
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_MED_CROSSROAD = createSimple("village/metro/daniel/med/path_crossroad")
            .set(BLOCKING_MATERIAL_INDEX, 2);
    public static final DungeonRoomTemplate VILLAGE_METRO_PATH_STAIRS = createSimple("village/metro/daniel/med/path_stairs")//todo handle verticality in the metro
            .set(BLOCKING_MATERIAL_INDEX, 2);

    public static final DungeonRoomTemplate VILLAGE_METRO_CHURCH = createSimple("village/metro/daniel/med/church")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(CLERIC,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_DORM_TOWER = createSimple("village/metro/daniel/med/dorm_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ILLAGER_TOWER = createSimple("village/metro/daniel/med/illager_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_LIBRARY = createSimple("village/metro/daniel/med/library")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(LIBRARIAN,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_LUMBERYARD = createSimple("village/metro/daniel/med/lumber_yard")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_SMITHERY = createSimple("village/metro/daniel/med/smithery")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(TOOLSMITH,1,2), new AddVillagersOfProfession(WEAPONSMITH,1,2), new AddVillagersOfProfession(ARMORER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_TANNERY = createSimple("village/metro/daniel/med/tannery")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(LEATHERWORKER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_VERTICAL_FARM = createSimple("village/metro/daniel/med/vertical_farm")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FARMER,1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_COTTAGE = createSimple("village/metro/daniel/med/cottage")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_1 = createSimple("village/metro/awseme/med/1")//todo these could all be named, but they are all just... houses
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_2 = createSimple("village/metro/awseme/med/2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_3 = createSimple("village/metro/awseme/med/3")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_4 = createSimple("village/metro/awseme/med/4")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_5 = createSimple("village/metro/awseme/med/5")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_5B = createSimple("village/metro/awseme/med/5b")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_6 = createSimple("village/metro/awseme/med/6")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_7 = createSimple("village/metro/awseme/med/7")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_8 = createSimple("village/metro/awseme/med/8")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_9 = createSimple("village/metro/awseme/med/9")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_10 = createSimple("village/metro/awseme/med/10")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_11 = createSimple("village/metro/awseme/med/11")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_12 = createSimple("village/metro/awseme/med/12")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_13 = createSimple("village/metro/awseme/med/13")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_14 = createSimple("village/metro/awseme/med/14")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_15 = createSimple("village/metro/awseme/med/15")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_16 = createSimple("village/metro/awseme/med/16")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_17 = createSimple("village/metro/awseme/med/17")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_18 = createSimple("village/metro/awseme/med/18")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_19 = createSimple("village/metro/awseme/med/19")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_20 = createSimple("village/metro/awseme/med/20")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_CAVE = createSimple("village/metro/awseme/med/cave")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_FOUNTAIN = createSimple("village/metro/awseme/med/fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_HOLLOW_HILL = createSimple("village/metro/awseme/med/hollow_hill")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_QUARRY = createSimple("village/metro/awseme/med/quarry")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_MED_STONECUTTER = createSimple("village/metro/awseme/med/stonecutter")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(MASON,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_BONFIRE = createSimple("village/metro/aiyqe/med/bonfire")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_DEEPSLATE_MINE = createSimple("village/metro/aiyqe/med/deepslate_mine")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_SALMON_BRIDGE = createSimple("village/metro/aiyqe/med/salmon_bridge")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_MED_TRADING_POST = createSimple("village/metro/aiyqe/med/trading_post")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(2,4)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_DEAD_TREE = createSimple("village/metro/arcanist/med/dead_tree")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_FISHING_POND = createSimple("village/metro/arcanist/med/fishing_pond")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FISHERMAN,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_SAFE_HOUSE = createSimple("village/metro/arcanist/med/safe_house")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_MED_WITCH_TOWER = createSimple("village/metro/arcanist/med/witch_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_BEEKEEPERS = createSimple("village/metro/chlter121/med/beekeepers")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FARMER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_FOOD_COURT = createSimple("village/metro/chlter121/med/food_court")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_FOUR_PARKOUR = createSimple("village/metro/chlter121/med/four_parkour")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_GRAVEYARD = createSimple("village/metro/chlter121/med/graveyard")
            .set(ENEMY_TABLE, EnemyTableRegistry.VILLAGE_GRAVEYARD);
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_GRAVEYARD_BROKEN = createSimple("village/metro/chlter121/med/graveyard_broken")
            .set(ENEMY_TABLE, EnemyTableRegistry.VILLAGE_GRAVEYARD);
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HEDGE_MAZE = createSimple("village/metro/chlter121/med/hedge_maze")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE = createSimple("village/metro/chlter121/med/house")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_ARCHERY = createSimple("village/metro/chlter121/med/house_archery")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FLETCHER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_CHERRY = createSimple("village/metro/chlter121/med/house_cherry")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_CONSTRUCTION = createSimple("village/metro/chlter121/med/house_construction")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(MASON,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_HOUSE_REEDS = createSimple("village/metro/chlter121/med/house_reeds")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_MARKET_STALLS = createSimple("village/metro/chlter121/med/market_stalls")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_PLAZA = createSimple("village/metro/chlter121/med/plaza")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_RUINED_HOUSE_1 = createSimple("village/metro/chlter121/med/ruined_house_1")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_MED_RUINED_HOUSE_2 = createSimple("village/metro/chlter121/med/ruined_house_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_BRICK_TOWER = createSimple("village/metro/ender/med/brick_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_FARM = createSimple("village/metro/ender/med/farm")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FARMER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_FOUNTAIN = createSimple("village/metro/ender/med/fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_1 = createSimple("village/metro/ender/med/house_1")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_2 = createSimple("village/metro/ender/med/house_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_3 = createSimple("village/metro/ender/med/house_3")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_4 = createSimple("village/metro/ender/med/house_4")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_5 = createSimple("village/metro/ender/med/house_5")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_HOUSE_6 = createSimple("village/metro/ender/med/house_6")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_MARKET_STALLS = createSimple("village/metro/ender/med/market_stalls")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_OASIS = createSimple("village/metro/ender/med/oasis")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_PARKOUR = createSimple("village/metro/ender/med/parkour")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_MED_POND = createSimple("village/metro/ender/med/pond")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_CHURCH = createSimple("village/metro/flyingnokk/med/church")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(CLERIC,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_CRYSTAL_TOWER = createSimple("village/metro/flyingnokk/med/crystal_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(CLERIC,1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_DUPLEX = createSimple("village/metro/flyingnokk/med/duplex")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_EMERALD_ARCH = createSimple("village/metro/flyingnokk/med/emerald_arch")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_FISHING_POND = createSimple("village/metro/flyingnokk/med/fishing_pond")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FISHERMAN,1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_GARDEN = createSimple("village/metro/flyingnokk/med/garden")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_GRAVEYARD = createSimple("village/metro/flyingnokk/med/graveyard")
            .set(ENEMY_TABLE, EnemyTableRegistry.VILLAGE_GRAVEYARD);
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_1 = createSimple("village/metro/flyingnokk/med/house_1")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_2 = createSimple("village/metro/flyingnokk/med/house_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_3 = createSimple("village/metro/flyingnokk/med/house_3")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_4 = createSimple("village/metro/flyingnokk/med/house_4")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_FARM = createSimple("village/metro/flyingnokk/med/house_farm")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(FARMER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_HOUSE_FARM_RUINED = createSimple("village/metro/flyingnokk/med/house_farm_ruined")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_LIBRARY = createSimple("village/metro/flyingnokk/med/library")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(LIBRARIAN,1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_MARKET_STALLS = createSimple("village/metro/flyingnokk/med/market_stalls")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_PLAZA = createSimple("village/metro/flyingnokk/med/plaza")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddRandomVillagers(1,3)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_REDSTONE_SHOP = createSimple("village/metro/flyingnokk/med/redstone_shop")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_SMITHERY = createSimple("village/metro/flyingnokk/med/smithery")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(WEAPONSMITH,1,2), new AddVillagersOfProfession(TOOLSMITH,1,2), new AddVillagersOfProfession(ARMORER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_STABLES = createSimple("village/metro/flyingnokk/med/stables")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1), new AddVillagersOfProfession(BUTCHER,1,2)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_TOWER = createSimple("village/metro/flyingnokk/med/tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_MED_WELL_RUINED = createSimple("village/metro/flyingnokk/med/well_ruined")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));

    public static final DungeonRoomTemplate VILLAGE_METRO_CROSS = createSimple("village/metro/daniel/sml/cross")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FOUNTAIN = createSimple("village/metro/daniel/sml/fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_HOME = createSimple("village/metro/daniel/sml/home")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_TREE = createSimple("village/metro/daniel/sml/tree")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_ARCH = createSimple("village/metro/awseme/sml/arch")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_CART = createSimple("village/metro/awseme/sml/cart")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_CORNER_ARCH = createSimple("village/metro/awseme/sml/corner_arch")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_FOUR_ARCH = createSimple("village/metro/awseme/sml/four_arch")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_TRI_ARCH = createSimple("village/metro/awseme/sml/tri_arch")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AWSEME_SML_TRI_FOUNTAIN = createSimple("village/metro/awseme/sml/tri_fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_HALLOW_FARM = createSimple("village/metro/aiyqe/sml/hallow_farm")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_HALLOW_TREE = createSimple("village/metro/aiyqe/sml/hallow_tree")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_REST_HUT = createSimple("village/metro/aiyqe/sml/rest_hut")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_AIYQE_SML_TINYHOME = createSimple("village/metro/aiyqe/sml/tinyhome")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_CARROT = createSimple("village/metro/arcanist/sml/carrot")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_CRYING_STATUE = createSimple("village/metro/arcanist/sml/crying_statue")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_TEMPLE = createSimple("village/metro/arcanist/sml/temple")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ARCANIST_SML_WET_CROSS = createSimple("village/metro/arcanist/sml/wet_cross")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_CHERRY_TREE = createSimple("village/metro/chlter121/sml/cherry_tree")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_BEETROOT = createSimple("village/metro/chlter121/sml/farm_beetroot")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_BERRIES = createSimple("village/metro/chlter121/sml/farm_berries")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_CARROT = createSimple("village/metro/chlter121/sml/farm_carrot")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_MELON = createSimple("village/metro/chlter121/sml/farm_melon")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_POTATO = createSimple("village/metro/chlter121/sml/farm_potato")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_PUMPKIN = createSimple("village/metro/chlter121/sml/farm_pumpkin")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_SUGARCANE = createSimple("village/metro/chlter121/sml/farm_sugarcane")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_FARM_WHEAT = createSimple("village/metro/chlter121/sml/farm_wheat")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_INCINERATOR = createSimple("village/metro/chlter121/sml/incinerator")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_JAIL = createSimple("village/metro/chlter121/sml/jail")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_PLANTER_WARPED = createSimple("village/metro/chlter121/sml/planter_warped")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_STATUE_KNEELING = createSimple("village/metro/chlter121/sml/statue_kneeling")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_TOWER_OPEN = createSimple("village/metro/chlter121/sml/tower_open")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_TOWER_OPEN_B = createSimple("village/metro/chlter121/sml/tower_open_b")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_SML_WELL = createSimple("village/metro/chlter121/sml/well")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_DEBRIS = createSimple("village/metro/ender/sml/debris")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_FARM = createSimple("village/metro/ender/sml/farm")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_FOUNTAIN = createSimple("village/metro/ender/sml/fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_HOUSE_1 = createSimple("village/metro/ender/sml/house_1")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_HOUSE_2 = createSimple("village/metro/ender/sml/house_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_HOUSE_3 = createSimple("village/metro/ender/sml/house_3")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_HOUSE_RUINED = createSimple("village/metro/ender/sml/house_ruined")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_MARKET_STALL = createSimple("village/metro/ender/sml/market_stall")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_ENDER_SML_TENT = createSimple("village/metro/ender/sml/tent")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_BEES = createSimple("village/metro/flyingnokk/sml/bees")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_BELL_TOWER = createSimple("village/metro/flyingnokk/sml/bell_tower")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_CART = createSimple("village/metro/flyingnokk/sml/cart")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_FARM_BEETROOT = createSimple("village/metro/flyingnokk/sml/farm_beetroot")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_FARM_CARROT = createSimple("village/metro/flyingnokk/sml/farm_carrot")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_FARM_POTATO = createSimple("village/metro/flyingnokk/sml/farm_potato")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_FOUNTAIN = createSimple("village/metro/flyingnokk/sml/fountain")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_HEDGES = createSimple("village/metro/flyingnokk/sml/hedges")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_HOUSE_1 = createSimple("village/metro/flyingnokk/sml/house_1")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_HOUSE_2 = createSimple("village/metro/flyingnokk/sml/house_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_HOUSE_3 = createSimple("village/metro/flyingnokk/sml/house_3")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_PATH_HEDGES = createSimple("village/metro/flyingnokk/sml/path_hedges")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_RUINS = createSimple("village/metro/flyingnokk/sml/ruins")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_SIDEWALK = createSimple("village/metro/flyingnokk/sml/sidewalk")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_SIDEWALK_2 = createSimple("village/metro/flyingnokk/sml/sidewalk_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_SMELTER = createSimple("village/metro/flyingnokk/sml/smelter")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_STATUE = createSimple("village/metro/flyingnokk/sml/statue")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_TARGETS = createSimple("village/metro/flyingnokk/sml/targets")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_TREE = createSimple("village/metro/flyingnokk/sml/tree")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_TREE_2 = createSimple("village/metro/flyingnokk/sml/tree_2")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_WHEAT_PATH = createSimple("village/metro/flyingnokk/sml/wheat_path")
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_WALL_CORNER = createSimple("village/metro/flyingnokk/sml/wall_corner");//todo put these in a different pool because they are wide_path compatible
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_WALL_STAIRS = createSimple("village/metro/flyingnokk/sml/wall_stairs");
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_SML_WALL_STRAIGHT = createSimple("village/metro/flyingnokk/sml/wall_straight");

    public static final DungeonRoomTemplate VILLAGE_METRO_TOWER_START = createSimple("village/tower/tower_start").setClazz(KeyRequiredRoom.class)
            .set(DO_PLACEMENT_MIRROR, false)
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.PEACEFUL)
            .set(INTENSITY, 1)
            .set(SHOP_TABLE, new WeightedTable<DungeonRegistration.OfferingTemplate>().add(OfferingTemplatePoolRegistry.VILLAGE_STORE_POOL, 1))
            .set(DESTRUCTION_RULE, RegionRule.PROTECT_ALL_CLEAR);//so they can't tunnel through the wall and cheat the keys
    public static final DungeonRoomTemplate VILLAGE_METRO_TOWER_STAIRS = createSimple("village/tower/tower_connection")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(DESTRUCTION_RULE, RegionRule.PROTECT_ALL);
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_CUBICLES = createCombat("village/metro/chlter121/twr/cubicles")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_LIBRARY = createCombat("village/metro/chlter121/twr/library")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_CHLTER121_TWR_SEVERANCE = createCombat("village/metro/chlter121/twr/severance")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_TWR_SEVERANCE_HALLS = createCombat("village/metro/flyingnokk/twr/severance_halls")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_TWR_JAIL = createCombat("village/metro/flyingnokk/twr/jail")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_TWR_LOBBY = createCombat("village/metro/flyingnokk/twr/lobby")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_FLYINGNOKK_TWR_VAULT = createCombat("village/metro/flyingnokk/twr/vault")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_MOBFIA_TWR_OFFICE = createCombat("village/metro/mobfia/twr/office")
            .set(DO_PLACEMENT_MIRROR, false)
            .set(CHEST_SPAWN_CHANCE, 1.0)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));
    public static final DungeonRoomTemplate VILLAGE_METRO_MOBFIA_TWR_CEO_OFFICE = createBoss("village/metro/mobfia/twr/ceo_office", new Vec3(13, 3, 20))
            .set(DO_PLACEMENT_MIRROR, false)
            .set(ENEMY_TABLE, EnemyTableRegistry.VILLAGER_CEO_ARENA)
            .set(ROOM_CLEAR_REWARD_POOL, WeightedPool.of(OfferingTemplateRegistry.EXIT_RIFT))
            .set(SOUNDSCAPE, SoundscapeTemplateRegistry.VD_ANGEL_INVESTOR);
    public static final DungeonRoomTemplate VILLAGE_METRO_TOWER_PERK = createSimple("village/tower/tower_perk")
            .set(DO_PLACEMENT_MIRROR, false)
            .setClazz(LootChoiceRoom.class)
            .set(CHEST_SPAWN_CHANCE, 1.0)
            .set(POST_GEN_PROCESSING_STEPS, List.of(new AddEmeraldPiles(1)));

    // ------- GAUNTLETS -------
    public static final DungeonRoomTemplate BOSS_KEY_ROOM = createSimple("special/gauntlet/boss_key_room").setClazz(WeaponGauntletKeyRoom.class).set(SOUNDSCAPE, SoundscapeTemplateRegistry.HEARTBEAT);
    // --- SCIFI
    public static final DungeonRoomTemplate SCIFI_ARENA_LOOT_CHOICE_ROOM = createSimple("special/gauntlet/scifi_arena_loot_room").setClazz(LootChoiceRoom.class).set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate SCIFI_ARENA_COMBAT_ROOM = createCombat("special/gauntlet/scifi_arena_combat_room").set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR).set(DIFFICULTY_MODIFIER, 20.0).set(WAVE_SIZE, 40).set(COMBAT_GROUP_SIZE, 5).set(ROOM_CLEAR_REWARD_POOL, OfferingTemplatePoolRegistry.SCIFI_WEAPONS_POOL);
    public static final DungeonRoomTemplate SCIFI_ARENA_EXIT_ROOM = createSimple("special/gauntlet/scifi_arena_exit_room");
    // --- LAVA
    public static final DungeonRoomTemplate TRIAL_ARENA_LOOT_CHOICE_ROOM = createSimple("special/gauntlet/trial_arena_loot_room").setClazz(LootChoiceRoom.class).set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate TRIAL_ARENA_COMBAT_ROOM = createCombat("special/gauntlet/trial_arena_combat_room").set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR).set(DIFFICULTY_MODIFIER, 20.0).set(WAVE_SIZE, 40).set(COMBAT_GROUP_SIZE, 5).set(ROOM_CLEAR_REWARD_POOL, OfferingTemplatePoolRegistry.WIND_WEAPONS_POOL);
    public static final DungeonRoomTemplate TRIAL_ARENA_EXIT_ROOM = createSimple("special/gauntlet/trial_arena_exit_room");
    // --- GENERAL
    public static final DungeonRoomTemplate GENERAL_ARENA_LOOT_CHOICE_ROOM = createSimple("special/gauntlet/general_arena_loot_room").setClazz(LootChoiceRoom.class).set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR);
    public static final DungeonRoomTemplate GENERAL_ARENA_COMBAT_ROOM = createCombat("special/gauntlet/general_arena_combat_room").set(DESTRUCTION_RULE,RegionRule.SHELL_CLEAR).set(DIFFICULTY_MODIFIER, 20.0).set(WAVE_SIZE, 40).set(COMBAT_GROUP_SIZE, 5).set(ROOM_CLEAR_REWARD_POOL, OfferingTemplatePoolRegistry.GENERAL_WEAPONS_POOL);
    public static final DungeonRoomTemplate GENERAL_ARENA_EXIT_ROOM = createSimple("special/gauntlet/general_arena_exit_room");

    public static DungeonRoomTemplate copyOf(DungeonRoomTemplate template, String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.copyOf(template, name);
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }

    public static DungeonRoomTemplate copyCombatOf(DungeonRoomTemplate template, String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.copyOf(template, name);
        room.setClazz(CombatRoom.class);
        room.set(DESTRUCTION_RULE, RegionRule.SHELL_CLEAR);
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }

    public static DungeonRoomTemplate create(String name, List<Pair<String, BlockPos>> roomTemplates) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, roomTemplates);
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }

    public static DungeonRoomTemplate createSimple(String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, List.of(Pair.of(name, EMPTY_BLOCK_POS)));
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }

    public static DungeonRoomTemplate createCombat(String name) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, List.of(Pair.of(name, EMPTY_BLOCK_POS)));
        room.setClazz(CombatRoom.class);
        room.set(DESTRUCTION_RULE, RegionRule.SHELL_CLEAR);
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }

    public static DungeonRoomTemplate createBoss(String name, Vec3 bossSpawnPos) {
        DungeonRoomTemplate room = DungeonRoomTemplate.create(name, List.of(Pair.of(name, EMPTY_BLOCK_POS)));
        room.setClazz(BossRoom.class);
        room.set(DESTRUCTION_RULE, RegionRule.SHELL_CLEAR);
        room.set(MOBS_FACE_PLAYER_ON_SPAWN, true);
        room.set(BOSS_SPAWN_POS, bossSpawnPos);
        DUNGEON_ROOM_REGISTRY.add(room);
        return room;
    }
}
