package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DungeonRooms {

    public static final HashMap<String, DungeonRoom> DUNGEON_ROOMS = new HashMap<>();
    public static final List<DungeonRoom> ALL_POOL = new ArrayList<>();

    public static void setupDungeonRooms(MinecraftServer server) {
        DUNGEON_ROOMS.put("small_1", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/small_1")));
        DUNGEON_ROOMS.put("small_2", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/small_2")));
        DUNGEON_ROOMS.put("small_3", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/small_3")));
        DUNGEON_ROOMS.put("medium_1", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/medium_1")));
        DUNGEON_ROOMS.put("medium_2", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/medium_2")));
        DUNGEON_ROOMS.put("large_1", new DungeonRoom(server.getStructureManager(), WildDungeons.rl("stone/large_1")));

        populateDungeonRoomPools();
    }


    public static void populateDungeonRoomPools() {
        DungeonRooms.DUNGEON_ROOMS.entrySet().stream().forEach(stringDungeonRoomEntry -> {
            ALL_POOL.add(stringDungeonRoomEntry.getValue());
        });
    }

    public static DungeonRoom getRandomFromPool(List<DungeonRoom> pool) {
        return pool.get(new Random().nextInt(pool.size()));
    }
}
