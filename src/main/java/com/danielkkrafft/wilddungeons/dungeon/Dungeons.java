package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Dungeons {

    public static final HashMap<String, Dungeon> DUNGEONS = new HashMap<>();

    public static void setupDungeons(MinecraftServer server) {
        DungeonFloors.setupDungeonFloors(server);
        DUNGEONS.put("dungeon_1", new Dungeon("dungeon_1", DungeonOpenBehavior.NONE, DungeonFloors.DUNGEON_FLOORS.get("empty_floor")));
        DUNGEONS.put("dungeon_2", new Dungeon("dungeon_2", DungeonOpenBehavior.NONE, DungeonFloors.DUNGEON_FLOORS.get("empty_floor")));
    }

    public static Dungeon getRandomDungeon() {
        List<Dungeon> dungeons = new ArrayList<>(DUNGEONS.values());
        return dungeons.get(new Random().nextInt(dungeons.size()));
    }

    public static class DungeonOpenBehavior {
        public static final String NONE = "none";
        public static final String ESSENCE_REQUIRED = "essence_required";
        public static final String ITEMS_REQUIRED = "items_required";
        public static final String ENTITY_REQUIRED = "entity_required";
        public static final String GUARD_REQUIRED = "guard_required";
        public static final String KILLS_REQUIRED = "kills_required";
    }

}
