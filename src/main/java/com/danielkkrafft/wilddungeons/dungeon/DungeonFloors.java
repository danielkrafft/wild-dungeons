package com.danielkkrafft.wilddungeons.dungeon;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class DungeonFloors {

    public static final HashMap<String, DungeonFloor> DUNGEON_FLOORS = new HashMap<>();

    public static void setupDungeonFloors(MinecraftServer server) {
        DungeonRooms.setupDungeonRooms(server);
        DUNGEON_FLOORS.put("empty_floor", new DungeonFloor(DungeonRooms.DUNGEON_ROOMS.get("three_room"), 100));
    }
}
