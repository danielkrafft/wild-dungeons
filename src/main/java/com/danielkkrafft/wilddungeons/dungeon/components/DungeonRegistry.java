package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DungeonRegistry {

    public static final DungeonComponentRegistry<DungeonComponents.DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonBranchTemplate> DUNGEON_BRANCH_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonTemplate> DUNGEON_REGISTRY = new DungeonComponentRegistry<>();

    public static final DungeonComponentPool<DungeonComponents.DungeonRoomTemplate> SMALL_ROOM_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonComponents.DungeonRoomTemplate> MEDIUM_ROOM_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonComponents.DungeonBranchTemplate> BRANCH_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonComponents.DungeonFloorTemplate> FLOOR_POOL = new DungeonComponentPool<>();
    public static final DungeonComponentPool<DungeonComponents.DungeonTemplate> DUNGEON_POOL = new DungeonComponentPool<>();

    public static void setupDungeons() {

        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_1").pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_2").pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_3").pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_4").pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_1").pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_2").pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_3").pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/large_1").pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/start"));

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("starter_room_branch", SMALL_ROOM_POOL, DUNGEON_ROOM_REGISTRY.get("stone/start"), 1));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("small_room_branch", SMALL_ROOM_POOL, DUNGEON_ROOM_REGISTRY.get("stone/large_1"), 30).pool(BRANCH_POOL));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("medium_room_branch", MEDIUM_ROOM_POOL, DUNGEON_ROOM_REGISTRY.get("stone/large_1"), 15).pool(BRANCH_POOL));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("ending_room_branch", SMALL_ROOM_POOL, DUNGEON_ROOM_REGISTRY.get("stone/start"), 10));

        DUNGEON_FLOOR_REGISTRY.add(DungeonComponents.DungeonFloorTemplate.build("test_floor", BRANCH_POOL, DUNGEON_BRANCH_REGISTRY.get("starter_room_branch"), DUNGEON_BRANCH_REGISTRY.get("ending_room_branch"), 2).pool(FLOOR_POOL));

        DUNGEON_REGISTRY.add(DungeonComponents.DungeonTemplate.build("dungeon_1", DungeonOpenBehavior.NONE, Arrays.asList(DUNGEON_FLOOR_REGISTRY.get("test_floor"), DUNGEON_FLOOR_REGISTRY.get("test_floor"), DUNGEON_FLOOR_REGISTRY.get("test_floor"))).pool(DUNGEON_POOL));
    }

    public static class DungeonComponentPool<T extends DungeonComponents.DungeonComponent> {

        private final List<T> pool;
        public DungeonComponentPool() { pool = new ArrayList<>(); }

        public void add(T item) { pool.add(item); }
        public T getRandom() { return pool.get(DungeonSessionManager.getInstance().server.overworld().getRandom().nextInt(pool.size())); }
    }

    public static class DungeonComponentRegistry<T extends DungeonComponents.DungeonComponent> {

        private final HashMap<String, T> registry;
        public DungeonComponentRegistry() { registry = new HashMap<>(); }

        public void add(T component) { registry.put(component.name(), component); }
        public T get(String key) { return registry.get(key); }
    }
}
