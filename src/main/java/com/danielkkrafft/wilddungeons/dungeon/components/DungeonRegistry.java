package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterials;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

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

        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_1", List.of(Pair.of("stone/small_1", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_2", List.of(Pair.of("stone/small_2", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_3", List.of(Pair.of("stone/small_3", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_4", List.of(Pair.of("stone/small_4", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/small_5", List.of(Pair.of("stone/composite_1", TemplateHelper.EMPTY_BLOCK_POS), Pair.of("stone/composite_2", new BlockPos(2,0,4))), null).pool(SMALL_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_1", List.of(Pair.of("stone/medium_1", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_2", List.of(Pair.of("stone/medium_2", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_3", List.of(Pair.of("stone/medium_3", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/medium_4", List.of(
                Pair.of("stone/medium_4_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))), null).pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/large_1", List.of(Pair.of("stone/large_1", TemplateHelper.EMPTY_BLOCK_POS)), null).pool(MEDIUM_ROOM_POOL));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/start", List.of(Pair.of("stone/start", TemplateHelper.EMPTY_BLOCK_POS)), null));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build("stone/boss", List.of(
                Pair.of("stone/boss_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                Pair.of("stone/boss_comp_3", new BlockPos(20,20,-16))), null));


        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("starter_room_branch", List.of(SMALL_ROOM_POOL), DUNGEON_ROOM_REGISTRY.get("stone/start"), 1, null));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("small_room_branch", List.of(SMALL_ROOM_POOL), DUNGEON_ROOM_REGISTRY.get("stone/large_1"), 30, null).pool(BRANCH_POOL));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("medium_room_branch", List.of(MEDIUM_ROOM_POOL), DUNGEON_ROOM_REGISTRY.get("stone/large_1"), 15, null).pool(BRANCH_POOL));
        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("ending_room_branch", List.of(MEDIUM_ROOM_POOL), DUNGEON_ROOM_REGISTRY.get("stone/boss"), 10, List.of(DungeonMaterials.NETHER)));

        DUNGEON_FLOOR_REGISTRY.add(DungeonComponents.DungeonFloorTemplate.build("test_floor", BRANCH_POOL, DUNGEON_BRANCH_REGISTRY.get("starter_room_branch"), DUNGEON_BRANCH_REGISTRY.get("ending_room_branch"), 10, null).pool(FLOOR_POOL));

        DUNGEON_REGISTRY.add(DungeonComponents.DungeonTemplate.build("dungeon_1", DungeonOpenBehavior.NONE, Arrays.asList(
                        DUNGEON_FLOOR_REGISTRY.get("test_floor"),
                        DUNGEON_FLOOR_REGISTRY.get("test_floor"),
                        DUNGEON_FLOOR_REGISTRY.get("test_floor")),
                List.of(DungeonMaterials.DEEP_DARK, DungeonMaterials.PRISMARINE, DungeonMaterials.NETHER, DungeonMaterials.OAK_WOOD, DungeonMaterials.END_STONE, DungeonMaterials.SANDSTONE, DungeonMaterials.STONE_BRICK)).pool(DUNGEON_POOL));
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
