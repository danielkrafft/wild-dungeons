package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

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
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = create(
            "overworld_combat_1",
            List.of(
                    of("overworld/sprawl/basic_3", EMPTY_BLOCK_POS)
            ))
            .setType(COMBAT);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_2 = create(
            "overworld_combat_2",
            List.of(
                    of("overworld/sprawl/basic_4", EMPTY_BLOCK_POS)
            ))
            .setType(COMBAT)
            .setDifficulty(0.5);
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_3 = create(
            "overworld_combat_3",
            List.of(
                    of("overworld/sprawl/basic_5", EMPTY_BLOCK_POS)
            ))
            .setType(COMBAT)
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

    public static void setupDungeonRooms() {
        DUNGEON_ROOM_REGISTRY.add(SMALL_1);
        DUNGEON_ROOM_REGISTRY.add(SMALL_2);
        DUNGEON_ROOM_REGISTRY.add(SMALL_3);
        DUNGEON_ROOM_REGISTRY.add(SMALL_4);
        DUNGEON_ROOM_REGISTRY.add(SMALL_5);
        DUNGEON_ROOM_REGISTRY.add(MEDIUM_1);
        DUNGEON_ROOM_REGISTRY.add(MEDIUM_2);
        DUNGEON_ROOM_REGISTRY.add(MEDIUM_3);
        DUNGEON_ROOM_REGISTRY.add(MEDIUM_4);
        DUNGEON_ROOM_REGISTRY.add(LARGE_1);
        DUNGEON_ROOM_REGISTRY.add(START);
        DUNGEON_ROOM_REGISTRY.add(BOSS);
        DUNGEON_ROOM_REGISTRY.add(SECRET_1);
        DUNGEON_ROOM_REGISTRY.add(SHOP_1);
        DUNGEON_ROOM_REGISTRY.add(LOOT_1);
        DUNGEON_ROOM_REGISTRY.add(REST);

        //overworld
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_START);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_BASIC_1);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_BASIC_2);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_BASIC_3);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_BASIC_4);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_BASIC_5);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_COMBAT_1);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_COMBAT_2);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_COMBAT_3);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_CHEST_ROOM);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_STAIRCASE);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_STAIRWAY_1);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_HALLWAY_1);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_HALLWAY_2);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_CRAFTING_ROOM);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_REST_ROOM);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_SHOP_ROOM);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_FREE_PERK);
        DUNGEON_ROOM_REGISTRY.add(OVERWORLD_EXIT_ROOM);
    }
}