package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class DungeonRooms {

    public static final HashMap<String, DungeonRoom> DUNGEON_ROOMS = new HashMap<>();

    public static void setupDungeonRooms(MinecraftServer server) {
        DUNGEON_ROOMS.put("empty_room", new DungeonRoom(server.getStructureManager(), "misc", WildDungeons.rl("empty_room")));
        DUNGEON_ROOMS.put("three_room", new DungeonRoom(server.getStructureManager(), "misc", WildDungeons.rl("three_room")));
    }
}
