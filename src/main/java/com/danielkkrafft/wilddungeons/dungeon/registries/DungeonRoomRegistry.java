package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.room.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
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
    public static final DungeonRoomTemplate LARGE_1 = createSimple("stone/large_1").setClazz(CombatRoom.class);
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
    public static final DungeonRoomTemplate LOOT_1 = create(
            "loot",
            List.of(
                    of("loot/1", EMPTY_BLOCK_POS),
                    of("loot/2", new BlockPos(0, 4, 0))
            ))
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
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = copyOf(OVERWORLD_BASIC_3, "overworld_combat_1").set(DESTRUCTION_RULE, DestructionRule.SHELL_CLEAR).setClazz(CombatRoom.class);
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
    public static final DungeonRoomTemplate VILLAGE_1 = createSimple("village/village_1");
    public static final DungeonRoomTemplate VILLAGE_FORGE = createSimple("village/village_forge");
    public static final DungeonRoomTemplate VILLAGE_PATH_1 = createSimple("village/village_path_1");
    public static final DungeonRoomTemplate VILLAGE_PATH_CROSSING = createSimple("village/village_path_crossing");
    public static final DungeonRoomTemplate VILLAGE_PATH_FARM = createSimple("village/village_path_farm");
    public static final DungeonRoomTemplate VILLAGE_CENTER = createSimple("village/village_center");
    public static final DungeonRoomTemplate VILLAGE_SINGLE_HUT = createSimple("village/village_single_hut");
    public static final DungeonRoomTemplate VILLAGE_FENCED_HORSES = createSimple("village/village_fenced_horses");
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

    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_8 = create(
            "nether_cave_sprawl_8",
            List.of(
                    of("nether/cave/sprawl_8_1", EMPTY_BLOCK_POS),
                    of("nether/cave/sprawl_8_2", new BlockPos(23, 0, -8)),
                    of("nether/cave/sprawl_8_3", new BlockPos(23, 3, 22))
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_9 = createSimple("nether/cave/sprawl_9");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_10 = create(
            "nether_cave_sprawl_10",
            List.of(
                    of("nether/cave/sprawl_10_1", EMPTY_BLOCK_POS),
                    of("nether/cave/sprawl_10_2", new BlockPos(7, 0, 0))
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_11 = createSimple("nether/cave/sprawl_11");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_12 = createSimple("nether/cave/sprawl_12");
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_13 = createSimple("nether/cave/sprawl_13");
    public static final DungeonRoomTemplate NETHER_CAVE_END_ROOM = create(
            "nether_cave_end",
            List.of(
                    of("nether/cave/end_1", EMPTY_BLOCK_POS),
                    of("nether/cave/end_2", new BlockPos(48, 0, 0))
            ))
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
    public static final DungeonRoomTemplate NETHER_PIPEWORKS_TO_FACTORY = createSimple("nether/pipeworks/pipe_to_factory").set(BLOCKING_MATERIAL_INDEX, 1).setClazz(KeyRequiredRoom.class).set(INTENSITY, 1).set(SOUNDSCAPE, SoundscapeTemplateRegistry.PIGLIN_FACTORY);
    public static final DungeonRoomTemplate NETHER_FACTORY_PARKOUR_1 = createSimple("nether/factory/parkour1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_PARKOUR_2 = create(
            "nether_factory_parkour_2",
            List.of(
                    of("nether/factory/parkour2_1", EMPTY_BLOCK_POS),
                    of("nether/factory/parkour2_2", new BlockPos(9, 0, 7))
            ))
            .set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_REST_1 = createSimple("nether/factory/rest1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SHOP_1 = createSimple("nether/factory/shop1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SHOP_2 = createSimple("nether/factory/shop2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_1 = createSimple("nether/factory/sprawl1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_2 = createSimple("nether/factory/sprawl2").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_3 = createSimple("nether/factory/sprawl3").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_4 = createSimple("nether/factory/sprawl4").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_5 = createSimple("nether/factory/sprawl5").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_SPRAWL_6 = createSimple("nether/factory/sprawl6").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_RAIL_1 = createSimple("nether/factory/rail_1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_RAIL_TRANSITION = createSimple("nether/factory/rail_transition").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_TRAP_1 = createSimple("nether/factory/trap1").set(BLOCKING_MATERIAL_INDEX, 1);
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_1 = copyOf(NETHER_FACTORY_SPRAWL_3, "nether_factory_combat_1").setClazz(CombatRoom.class);
    public static final DungeonRoomTemplate NETHER_FACTORY_COMBAT_2 = copyOf(NETHER_FACTORY_SPRAWL_2, "nether_factory_combat_2").setClazz(CombatRoom.class);
    public static final DungeonRoomTemplate NETHER_FACTORY_KEY_COMBAT = copyOf(NETHER_FACTORY_COMBAT_1, "nether_factory_key_combat").setRoomClearOffering(OfferingTemplateRegistry.DUNGEON_KEY);
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
            .set(INTENSITY, 1);
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
            .setRoomClearOffering(OfferingTemplateRegistry.EXIT_RIFT)
            .set(ENEMY_TABLE, EnemyTableRegistry.NETHER_DRAGON_ARENA)
            .set(BOSS_SPAWN_POS, new Vec3(3.0, 40.0, 3.0));


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