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

    public static final DungeonRoomTemplate SMALL_1 = build(
            NONE,
            "stone/small_1",
            List.of(
                    of("stone/small_1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SMALL_2 = build(
            NONE,
            "stone/small_2",
            List.of(
                    of("stone/small_2", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SMALL_3 = build(
            NONE,
            "stone/small_3",
            List.of(
                    of("stone/small_3", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SMALL_4 = build(
            NONE,
            "stone/small_4",
            List.of(
                    of("stone/small_4", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SMALL_5 = build(
            NONE,
            "stone/small_5",
            List.of(
                    of("stone/composite_1", EMPTY_BLOCK_POS),
                    of("stone/composite_2", new BlockPos(2,0,4))
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate MEDIUM_1 = build(
            NONE,
            "stone/medium_1",
            List.of(
                    of("stone/medium_1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate MEDIUM_2 = build(
            NONE,
            "stone/medium_2",
            List.of(
                    of("stone/medium_2", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate MEDIUM_3 = build(
            NONE,
            "stone/medium_3",
            List.of(
                    of("stone/medium_3", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate MEDIUM_4 = build(
            NONE,
            "stone/medium_4",
            List.of(
                    of("stone/medium_4_comp_1", EMPTY_BLOCK_POS),
                    of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate LARGE_1 = build(
            COMBAT,
            "stone/large_1",
            List.of(
                    of("stone/large_1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate START = build(
            NONE,
            "stone/start",
            List.of(
                    of("stone/start", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate BOSS = build(
            NONE,
            "stone/boss",
            List.of(
                    of("stone/boss_comp_1", EMPTY_BLOCK_POS),
                    of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                    of("stone/boss_comp_3", new BlockPos(20,20,-16))
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SECRET_1 = build(
            SECRET,
            "secret/1",
            List.of(
                    of("secret/1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate SHOP_1 = build(
            SHOP,
            "shop/1",
            List.of(
                    of("shop/1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate LOOT_1 = build(
            LOOT,
            "loot",
            List.of(
                    of("loot/1", EMPTY_BLOCK_POS),
                    of("loot/2", new BlockPos(0, 4, 0))
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate REST = build(
            NONE,
            "rest",
            List.of(
                    of("rest/1", EMPTY_BLOCK_POS),
                    of("rest/2", new BlockPos(-7, 0, 3)),
                    of("rest/3", new BlockPos(3, 0, -7)),
                    of("rest/4", new BlockPos(12, 0, 3)),
                    of("rest/5", new BlockPos(3, 0, 12))
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate PARKOUR = build(
            NONE,
            "parkour",
            List.of(
                    of("parkour/1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate OVERWORLD_START = build(
            NONE,
            "overworld_start",
            List.of(
                    of("overworld/start", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0);
    public static final DungeonRoomTemplate OVERWORLD_BASIC_1 = build(
            NONE,
            "overworld_basic_1",
            List.of(
                    of("overworld/sprawl/basic_1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_BASIC_2 = build(
            NONE,
            "overworld_basic_2",
            List.of(
                    of("overworld/sprawl/basic_2", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_BASIC_3 = build(
            NONE,
            "overworld_basic_3",
            List.of(
                    of("overworld/sprawl/basic_3", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_BASIC_4 = build(
            NONE,
            "overworld_basic_4",
            List.of(
                    of("overworld/sprawl/basic_4", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_BASIC_5 = build(
            NONE,
            "overworld_basic_5",
            List.of(
                    of("overworld/sprawl/basic_5", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_1 = build(
            COMBAT,
            "overworld_combat_1",
            List.of(
                    of("overworld/sprawl/basic_3", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1
    );
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_2 = build(
            COMBAT,
            "overworld_combat_2",
            List.of(
                    of("overworld/sprawl/basic_4", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            0.5
    );
    public static final DungeonRoomTemplate OVERWORLD_COMBAT_3 = build(
            COMBAT,
            "overworld_combat_3",
            List.of(
                    of("overworld/sprawl/basic_5", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            0.75
    );
    public static final DungeonRoomTemplate OVERWORLD_CHEST_ROOM = build(
            NONE,
            "overworld_chest_room",
            List.of(
                    of("overworld/sprawl/chest_room", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_STAIRCASE = build(
            NONE,
            "overworld_staircase",
            List.of(
                    of("overworld/sprawl/stair_room", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_STAIRWAY_1 = build(
                    NONE,
                    "overworld_stairway_1",
                    List.of(
                            of("overworld/sprawl/stairway_1", EMPTY_BLOCK_POS)
                    ),
                    null,
                    null,
                    1.0
            );
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_1 = build(
            NONE,
            "overworld_hallway_1",
            List.of(
                    of("overworld/sprawl/hallway_1", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_HALLWAY_2 = build(
            NONE,
            "overworld_hallway_2",
            List.of(
                    of("overworld/sprawl/hallway_2", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_CRAFTING_ROOM = build(
            NONE,
            "overworld_crafting",
            List.of(
                    of("overworld/crafting", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_REST_ROOM = build(
            NONE,
            "overworld_restroom_basic",
            List.of(
                    of("overworld/rest_basic", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_SHOP_ROOM = build(
            SHOP,
            "overworld_shop",
            List.of(
                    of("overworld/shop", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_FREE_PERK = build(
            LOOT,
            "overworld_free_perk",
            List.of(
                    of("overworld/free_perk", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );
    public static final DungeonRoomTemplate OVERWORLD_EXIT_ROOM = build(
            NONE,
            "overworld_exit",
            List.of(
                    of("overworld/exit", EMPTY_BLOCK_POS)
            ),
            null,
            null,
            1.0
    );

    public static void setupDungeonRooms(){
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
