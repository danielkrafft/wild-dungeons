package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

import java.util.Arrays;
import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate.*;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate.Type.*;
import static com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper.*;
import static com.mojang.datafixers.util.Pair.*;

public class DungeonRoomRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonRoomTemplate SMALL_1 = create(
            "stone/small_1",
            List.of(
                    of("stone/small_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate SMALL_2 = create(
            "stone/small_2",
            List.of(
                    of("stone/small_2", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate SMALL_3 = create(
            "stone/small_3",
            List.of(
                    of("stone/small_3", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate SMALL_4 = create(
            "stone/small_4",
            List.of(
                    of("stone/small_4", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate SMALL_5 = create(
            "stone/small_5",
            List.of(
                    of("stone/composite_1", EMPTY_BLOCK_POS),
                    of("stone/composite_2", new BlockPos(2, 0, 4))
            ));
    public static final DungeonRoomTemplate MEDIUM_1 = create(
            "stone/medium_1",
            List.of(
                    of("stone/medium_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate MEDIUM_2 = create(
            "stone/medium_2",
            List.of(
                    of("stone/medium_2", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate MEDIUM_3 = create(
            "stone/medium_3",
            List.of(
                    of("stone/medium_3", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate MEDIUM_4 = create(
            "stone/medium_4",
            List.of(
                    of("stone/medium_4_comp_1", EMPTY_BLOCK_POS),
                    of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))
            ));
    public static final DungeonRoomTemplate LARGE_1 = create(
            "stone/large_1",
            List.of(
                    of("stone/large_1", EMPTY_BLOCK_POS)
            ))
            .setType(COMBAT);
    public static final DungeonRoomTemplate START = create(
            "stone/start",
            List.of(
                    of("stone/start", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate BOSS = create(
            "stone/boss",
            List.of(
                    of("stone/boss_comp_1", EMPTY_BLOCK_POS),
                    of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                    of("stone/boss_comp_3", new BlockPos(20, 20, -16))
            ));
    public static final DungeonRoomTemplate SECRET_1 = create(
            "secret/1",
            List.of(
                    of("secret/1", EMPTY_BLOCK_POS)
            ))
            .setType(SECRET);
    public static final DungeonRoomTemplate SHOP_1 = create(
            "shop/1",
            List.of(
                    of("shop/1", EMPTY_BLOCK_POS)
            ))
            .setType(SHOP);
    public static final DungeonRoomTemplate LOOT_1 = create(
            "loot",
            List.of(
                    of("loot/1", EMPTY_BLOCK_POS),
                    of("loot/2", new BlockPos(0, 4, 0))
            ))
            .setType(LOOT);
    public static final DungeonRoomTemplate REST = create(
            "rest",
            List.of(
                    of("rest/1", EMPTY_BLOCK_POS),
                    of("rest/2", new BlockPos(-7, 0, 3)),
                    of("rest/3", new BlockPos(3, 0, -7)),
                    of("rest/4", new BlockPos(12, 0, 3)),
                    of("rest/5", new BlockPos(3, 0, 12))
            ));
    public static final DungeonRoomTemplate PARKOUR = create(
            "parkour",
            List.of(
                    of("parkour/1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_START = create(
            "overworld_start",
            List.of(
                    of("overworld/start", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_BASIC_1 = create(
            "overworld_basic_1",
            List.of(
                    of("overworld/sprawl/basic_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_BASIC_2 = create(
            "overworld_basic_2",
            List.of(
                    of("overworld/sprawl/basic_2", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_BASIC_3 = create(
            "overworld_basic_3",
            List.of(
                    of("overworld/sprawl/basic_3", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_BASIC_4 = create(
            "overworld_basic_4",
            List.of(
                    of("overworld/sprawl/basic_4", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_BASIC_5 = create(
            "overworld_basic_5",
            List.of(
                    of("overworld/sprawl/basic_5", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = copyOf(
            OVERWORLD_BASIC_3,
            "overworld_combat_1")
            .setDestructionRule(DestructionRule.SHELL_CLEAR)
            .setType(COMBAT);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_2 = copyOf(
            OVERWORLD_BASIC_4,
            "overworld_combat_2")
            .setType(COMBAT)
            .setDestructionRule(DestructionRule.SHELL_CLEAR)
            .setDifficulty(0.5);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_3 = copyOf(
            OVERWORLD_BASIC_5,
            "overworld_combat_3")
            .setType(COMBAT)
            .setDestructionRule(DestructionRule.SHELL_CLEAR)
            .setDifficulty(0.75);
    public static final DungeonRoomTemplate OVERWORLD_CHEST_ROOM = create(
            "overworld_chest_room",
            List.of(
                    of("overworld/sprawl/chest_room", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_STAIRCASE = create(
            "overworld_staircase",
            List.of(
                    of("overworld/sprawl/stair_room", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_STAIRWAY_1 = create(
            "overworld_stairway_1",
            List.of(
                    of("overworld/sprawl/stairway_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_1 = create(
            "overworld_hallway_1",
            List.of(
                    of("overworld/sprawl/hallway_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_2 = create(
            "overworld_hallway_2",
            List.of(
                    of("overworld/sprawl/hallway_2", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_CRAFTING_ROOM = create(
            "overworld_crafting",
            List.of(
                    of("overworld/crafting", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_REST_ROOM = create(
            "overworld_restroom_basic",
            List.of(
                    of("overworld/rest_basic", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_SHOP_ROOM = create(
            "overworld_shop",
            List.of(
                    of("overworld/shop", EMPTY_BLOCK_POS)
            ))
            .setType(SHOP);
    public static final DungeonRoomTemplate OVERWORLD_FREE_PERK = create(
            "overworld_free_perk",
            List.of(
                    of("overworld/free_perk", EMPTY_BLOCK_POS)
            ))
            .setType(LOOT);
    public static final DungeonRoomTemplate OVERWORLD_EXIT_ROOM = create(
            "overworld_exit",
            List.of(
                    of("overworld/exit", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate OVERWORLD_TRANSITION_ROOM = create(
            "transition",
            List.of(
                    of("overworld/transition", EMPTY_BLOCK_POS),
                    of("overworld/transition2", new BlockPos(7, -13, 3))
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_ENTRANCE_ROOM = create(
            "nether_cave_entrance",
            List.of(
                    of("nether/cave/entrance", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_1 = create(
            "nether_cave_sprawl_1",
            List.of(
                    of("nether/cave/sprawl_1", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_2 = create(
            "nether_cave_sprawl_2",
            List.of(
                    of("nether/cave/sprawl_2", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_3 = create(
            "nether_cave_sprawl_3",
            List.of(
                    of("nether/cave/sprawl_3", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_4 = create(
            "nether_cave_sprawl_4",
            List.of(
                    of("nether/cave/sprawl_4", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_5 = create(
            "nether_cave_sprawl_5",
            List.of(
                    of("nether/cave/sprawl_5", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_6 = create(
            "nether_cave_sprawl_6",
            List.of(
                    of("nether/cave/sprawl_6", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_7 = create(
            "nether_cave_sprawl_7",
            List.of(
                    of("nether/cave/sprawl_7", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_8 = create(
            "nether_cave_sprawl_8",
            List.of(
                    of("nether/cave/sprawl_8_1", EMPTY_BLOCK_POS),
                    of("nether/cave/sprawl_8_2", new BlockPos(23, 0, -8)),
                    of("nether/cave/sprawl_8_3", new BlockPos(23, 3, 22))
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_9 = create(
            "nether_cave_sprawl_9",
            List.of(
                    of("nether/cave/sprawl_9", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_10 = create(
            "nether_cave_sprawl_10",
            List.of(
                    of("nether/cave/sprawl_10_1", EMPTY_BLOCK_POS),
                    of("nether/cave/sprawl_10_2", new BlockPos(7, 0, 0))
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_11 = create(
            "nether_cave_sprawl_11",
            List.of(
                    of("nether/cave/sprawl_11", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_12 = create(
            "nether_cave_sprawl_12",
            List.of(
                    of("nether/cave/sprawl_12", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_SPRAWL_13 = create(
            "nether_cave_sprawl_13",
            List.of(
                    of("nether/cave/sprawl_13", EMPTY_BLOCK_POS)
            ));
    public static final DungeonRoomTemplate NETHER_CAVE_END_ROOM = create(
            "nether_cave_end",
            List.of(
                    of("nether/cave/end_1", EMPTY_BLOCK_POS),
                    of("nether/cave/end_2", new BlockPos(48, 0, 0))
            ));


    public static void setupDungeonRooms() {
        Arrays.asList(NETHER_CAVE_ENTRANCE_ROOM, NETHER_CAVE_SPRAWL_1, NETHER_CAVE_SPRAWL_2, NETHER_CAVE_SPRAWL_3, NETHER_CAVE_SPRAWL_4, NETHER_CAVE_SPRAWL_5, NETHER_CAVE_SPRAWL_6, NETHER_CAVE_SPRAWL_7, NETHER_CAVE_SPRAWL_8, NETHER_CAVE_SPRAWL_9, NETHER_CAVE_SPRAWL_10, NETHER_CAVE_SPRAWL_11, NETHER_CAVE_SPRAWL_12, NETHER_CAVE_SPRAWL_13, NETHER_CAVE_END_ROOM).forEach(DUNGEON_ROOM_REGISTRY::add);
        Arrays.asList(SMALL_1, SMALL_2, SMALL_3, SMALL_4, SMALL_5, MEDIUM_1, MEDIUM_2, MEDIUM_3, MEDIUM_4, LARGE_1, START, BOSS, SECRET_1, SHOP_1, LOOT_1, REST, OVERWORLD_START, OVERWORLD_BASIC_1, OVERWORLD_BASIC_2, OVERWORLD_BASIC_3, OVERWORLD_BASIC_4, OVERWORLD_BASIC_5, OVERWORLD_COMBAT_1, OVERWORLD_COMBAT_2, OVERWORLD_COMBAT_3, OVERWORLD_CHEST_ROOM, OVERWORLD_STAIRCASE, OVERWORLD_STAIRWAY_1, OVERWORLD_HALLWAY_1, OVERWORLD_HALLWAY_2, OVERWORLD_CRAFTING_ROOM, OVERWORLD_REST_ROOM, OVERWORLD_SHOP_ROOM, OVERWORLD_FREE_PERK, OVERWORLD_EXIT_ROOM, OVERWORLD_TRANSITION_ROOM).forEach(DUNGEON_ROOM_REGISTRY::add);
    }

}