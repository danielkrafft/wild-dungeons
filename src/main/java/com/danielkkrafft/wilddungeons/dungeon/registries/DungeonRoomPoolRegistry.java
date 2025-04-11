package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonRoomTemplate;
import com.danielkkrafft.wilddungeons.util.WeightedPool;

import static com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRoomRegistry.*;

public class DungeonRoomPoolRegistry {
    public static final WeightedPool<DungeonRoomTemplate> SMALL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> MEDIUM_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> SECRET_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> SHOP_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> LOOT_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> REST_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> PARKOUR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> COMBAT_ROOM_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SPRAWL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SPACER_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_REST_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_LOOT_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SHOP_ROOM_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonRoomTemplate> VILLAGE_SEWER_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonRoomTemplate> PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> PIGLIN_FACTORY_PIPEWORKS_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> PIGLIN_FACTORY_SPRAWL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> PIGLIN_FACTORY_SHOP_ROOM_POOL = new WeightedPool<>();

    public static void setupRoomPools() {
        SMALL_ROOM_POOL
                .add(SMALL_1,1)
                .add(SMALL_2,1)
                .add(SMALL_3,1)
                .add(SMALL_4,1)
                .add(SMALL_5,1);
        MEDIUM_ROOM_POOL
                .add(MEDIUM_1,1)
                .add(MEDIUM_2,1)
                .add(MEDIUM_3,1)
                .add(MEDIUM_4,1);
        SECRET_POOL
                .add(SECRET_1,1);
        SHOP_POOL
                .add(SHOP_1,1);
        LOOT_POOL
                .add(LOOT_1,1);
        REST_POOL
                .add(REST,1);
        PARKOUR_POOL
                .add(PARKOUR,1);
        COMBAT_ROOM_POOL
                .add(LARGE_1,1);

        OVERWORLD_SPRAWL_ROOM_POOL
                .add(OVERWORLD_BASIC_1,4)
                .add(OVERWORLD_BASIC_2,4)
                .add(OVERWORLD_BASIC_3,1)
                .add(OVERWORLD_BASIC_4,3)
                .add(OVERWORLD_BASIC_5,4)
                .add(OVERWORLD_BASIC_6, 3)
                .add(OVERWORLD_BASIC_7, 3)
                .add(OVERWORLD_COMBAT_1,2)
                .add(OVERWORLD_COMBAT_2,2)
                .add(OVERWORLD_COMBAT_3,2)
                .add(OVERWORLD_COMBAT_4,2)
                .add(OVERWORLD_CHEST_ROOM,2)
                .add(OVERWORLD_STAIRCASE,1)
                .add(OVERWORLD_STAIRWAY_1,1)
                .add(OVERWORLD_HALLWAY_1,2)
                .add(OVERWORLD_HALLWAY_2,1)
                .add(OVERWORLD_CRAFTING_ROOM,1)
                .add(OVERWORLD_PARKOUR,1)
                .add(OVERWORLD_SECRET,1)
                .add(OVERWORLD_SMELTER_ROOM, 1);
        OVERWORLD_SPACER_ROOM_POOL
                .add(OVERWORLD_STAIRWAY_1,1)
                .add(OVERWORLD_HALLWAY_1,3)
                .add(OVERWORLD_HALLWAY_2,2);
        OVERWORLD_REST_ROOM_POOL
                .add(OVERWORLD_REST_ROOM,1);
        OVERWORLD_SHOP_ROOM_POOL
                .add(OVERWORLD_SHOP_ROOM,1);
        OVERWORLD_LOOT_ROOM_POOL
                .add(OVERWORLD_FREE_PERK,1)
                .add(OVERWORLD_DOUBLE_LOOT, 1);

        PIGLIN_FACTORY_CAVE_SPRAWL_ROOM_POOL
                .add(NETHER_CAVE_SPRAWL_1, 1)
                .add(NETHER_CAVE_SPRAWL_2, 1)
                .add(NETHER_CAVE_COMBAT_2, 1)
                .add(NETHER_CAVE_SPRAWL_3, 1)
                .add(NETHER_CAVE_SPRAWL_4, 1)
                .add(NETHER_CAVE_SPRAWL_5, 1)
                .add(NETHER_CAVE_SPRAWL_6, 1)
                .add(NETHER_CAVE_SPRAWL_7, 1)
                .add(NETHER_CAVE_COMBAT_1, 1)
                .add(NETHER_CAVE_SPRAWL_8, 1)
                .add(NETHER_CAVE_SPRAWL_9, 1)
                .add(NETHER_CAVE_SPRAWL_10, 1)
                .add(NETHER_CAVE_SPRAWL_11, 1)
                .add(NETHER_CAVE_SPRAWL_12, 1)
                .add(NETHER_CAVE_SPRAWL_13, 1);

        PIGLIN_FACTORY_PIPEWORKS_ROOM_POOL
                .add(NETHER_PIPEWORKS_0, 1)
                .add(NETHER_PIPEWORKS_1, 1)
                .add(NETHER_PIPEWORKS_2, 1)
                .add(NETHER_PIPEWORKS_3, 1)
                .add(NETHER_PIPEWORKS_4, 1)
                .add(NETHER_PIPEWORKS_5, 1)
                .add(NETHER_PIPEWORKS_6, 1)
                .add(NETHER_PIPEWORKS_LAVAFALLS, 1)
                .add(NETHER_PIPEWORKS_BREAKOUT_ROOM, 1);

        PIGLIN_FACTORY_SPRAWL_ROOM_POOL
                .add(NETHER_FACTORY_SPRAWL_1, 1)
                .add(NETHER_FACTORY_SPRAWL_2, 1)
                .add(NETHER_FACTORY_SPRAWL_3, 1)
                .add(NETHER_FACTORY_SPRAWL_4, 1)
                .add(NETHER_FACTORY_SPRAWL_5, 1)
                .add(NETHER_FACTORY_SPRAWL_6, 1)
                .add(NETHER_FACTORY_RAIL_1, 1)
                .add(NETHER_FACTORY_RAIL_TRANSITION, 1)
                .add(NETHER_FACTORY_COMBAT_1, 1)
                .add(NETHER_FACTORY_COMBAT_2, 1)
                .add(NETHER_FACTORY_PARKOUR_1, 1)
                .add(NETHER_FACTORY_PARKOUR_2, 1)
                .add(NETHER_FACTORY_REST_1, 1)
                .add(NETHER_FACTORY_TRAP_1, 1);

        PIGLIN_FACTORY_SHOP_ROOM_POOL
                .add(NETHER_FACTORY_SHOP_1, 1)
                .add(NETHER_FACTORY_SHOP_2, 1);

        VILLAGE_SEWER_POOL
                .add(VILLAGE_SEWER_1, 1)
                .add(VILLAGE_SEWER_2, 1)
                .add(VILLAGE_SEWER_3, 1)
                .add(VILLAGE_SEWER_4, 1)
                .add(VILLAGE_SEWER_5, 1)
                .add(VILLAGE_SEWER_6, 1)
                .add(VILLAGE_SEWER_7, 1)
                .add(VILLAGE_SEWER_8, 1)
                .add(VILLAGE_SEWER_9, 1)
                .add(VILLAGE_SEWER_10, 1)
                .add(VILLAGE_SEWER_11, 1)
                .add(VILLAGE_SEWER_12, 1)
                .add(VILLAGE_SEWER_13, 1)
                .add(VILLAGE_SEWER_CHESTCAP, 1)
                .add(VILLAGE_SEWER_ELEVATOR, 1)
                .add(VILLAGE_SEWER_DEELEVATOR, 1);
    }
}
