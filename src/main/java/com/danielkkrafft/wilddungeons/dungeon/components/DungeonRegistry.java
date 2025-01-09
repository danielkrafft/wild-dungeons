package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonMaterials;
import com.danielkkrafft.wilddungeons.dungeon.components.room.EnemyTables;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonRegistry {

    public static final DungeonComponentRegistry<DungeonComponents.DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonBranchTemplate> DUNGEON_BRANCH_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonComponents.DungeonTemplate> DUNGEON_REGISTRY = new DungeonComponentRegistry<>();

    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> SMALL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> SECRET_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> MEDIUM_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> SHOP_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> LOOT_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> REST_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> PARKOUR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonRoomTemplate> COMBAT_ROOM_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonComponents.DungeonBranchTemplate> BRANCH_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonFloorTemplate> FLOOR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonComponents.DungeonTemplate> DUNGEON_POOL = new WeightedPool<>();


    public static void setupDungeons() {

        /**
         *  ROOMS
         */

        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE, "stone/small_1", List.of(Pair.of("stone/small_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/small_2", List.of(Pair.of("stone/small_2", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/small_3", List.of(Pair.of("stone/small_3", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/small_4", List.of(Pair.of("stone/small_4", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/small_5", List.of(Pair.of("stone/composite_1", TemplateHelper.EMPTY_BLOCK_POS), Pair.of("stone/composite_2", new BlockPos(2,0,4))), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/medium_1", List.of(Pair.of("stone/medium_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/medium_2", List.of(Pair.of("stone/medium_2", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/medium_3", List.of(Pair.of("stone/medium_3", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/medium_4", List.of(
                Pair.of("stone/medium_4_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.COMBAT,"stone/large_1", List.of(Pair.of("stone/large_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(COMBAT_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/start", List.of(Pair.of("stone/start", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"stone/boss", List.of(
                Pair.of("stone/boss_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                Pair.of("stone/boss_comp_3", new BlockPos(20,20,-16))), null, null, 1.0));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.SECRET,"secret/1", List.of(Pair.of("secret/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SECRET_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.SHOP,"shop/1", List.of(Pair.of("shop/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SHOP_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.LOOT,"loot", List.of(
                Pair.of("loot/1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("loot/2", new BlockPos(0, 4, 0))), null, null, 1.0).pool(LOOT_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE,"rest", List.of(
                Pair.of("rest/1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("rest/2", new BlockPos(-7, 0, 3)),
                Pair.of("rest/3", new BlockPos(3, 0, -7)),
                Pair.of("rest/4", new BlockPos(12, 0, 3)),
                Pair.of("rest/5", new BlockPos(3, 0, 12))), null, null, 1.0).pool(REST_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonComponents.DungeonRoomTemplate.build(DungeonComponents.DungeonRoomTemplate.Type.NONE, "parkour", List.of(Pair.of("parkour/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(PARKOUR_POOL, 1));


        /**
         *  BRANCHES
         */

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("starter_room_branch",
                new DungeonLayout<DungeonComponents.DungeonRoomTemplate>()
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/start")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("small_room_branch",
                new DungeonLayout<DungeonComponents.DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(SMALL_ROOM_POOL, 100), Pair.of(SECRET_POOL, 10)), 30)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/large_1")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("medium_room_branch",
                new DungeonLayout<DungeonComponents.DungeonRoomTemplate>()
                        .add(MEDIUM_ROOM_POOL, 15)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/large_1")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("all_branch",
                        new DungeonLayout<DungeonComponents.DungeonRoomTemplate>()
                                .add(WeightedPool.combine(Pair.of(SMALL_ROOM_POOL, 150), Pair.of(MEDIUM_ROOM_POOL, 150), Pair.of(SECRET_POOL, 20), Pair.of(COMBAT_ROOM_POOL, 50), Pair.of(PARKOUR_POOL, 50), Pair.of(LOOT_POOL, 50), Pair.of(REST_POOL, 50)), 30)
                                .addSimple(DUNGEON_ROOM_REGISTRY.get("shop/1")), null, null, 1.0)
                .pool(BRANCH_POOL, 1));

        DUNGEON_BRANCH_REGISTRY.add(DungeonComponents.DungeonBranchTemplate.build("ending_room_branch",
                new DungeonLayout<DungeonComponents.DungeonRoomTemplate>()
                        .add(MEDIUM_ROOM_POOL, 10)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/boss")),
                new WeightedPool<DungeonMaterial>()
                        .add(DungeonMaterials.NETHER, 1), null, 1.0));

        /**
         *  FLOORS
         */

        DUNGEON_FLOOR_REGISTRY.add(DungeonComponents.DungeonFloorTemplate.build("test_floor",
                new DungeonLayout<DungeonComponents.DungeonBranchTemplate>()
                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("starter_room_branch"))
                        .add(BRANCH_POOL, 10)
                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("ending_room_branch")), null, null, 1.0)
                .pool(FLOOR_POOL, 1));

        /**
         *  DUNGEONS
         */

        DUNGEON_REGISTRY.add(DungeonComponents.DungeonTemplate.build("dungeon_1", DungeonOpenBehavior.NONE,
                new DungeonLayout<DungeonComponents.DungeonFloorTemplate>()
                        .add(FLOOR_POOL, 1),
                new WeightedPool<DungeonMaterial>()
                        .add(DungeonMaterials.DEEP_DARK, 1)
                        .add(DungeonMaterials.PRISMARINE, 1)
                        .add(DungeonMaterials.OAK_WOOD, 1)
                        .add(DungeonMaterials.SANDSTONE, 1)
                        .add(DungeonMaterials.STONE_BRICK, 1), EnemyTables.BASIC_TABLE, 1.0, DungeonSession.DungeonExitBehavior.NEW_DUNGEON, DUNGEON_POOL)
                .pool(DUNGEON_POOL, 1));
    }

    public static class DungeonComponentRegistry<T extends DungeonComponents.DungeonComponent> {
        private final HashMap<String, T> registry;
        public DungeonComponentRegistry() { registry = new HashMap<>(); }

        public void add(T component) { registry.put(component.name(), component); }
        public T get(String key) { return registry.get(key); }
    }

    public static class DungeonLayout<T> {
        private final List<WeightedPool<T>> order;
        public DungeonLayout() {order = new ArrayList<>();}

        public DungeonLayout<T> add(WeightedPool<T> pool, int count) {
            for (int i = 0; i < count; i++) {
                this.order.add(pool);
            }
            return this;
        }

        public DungeonLayout<T> addSimple(T entry) {
            this.order.add(new WeightedPool<T>().add(entry, 1));
            return this;
        }

        public WeightedPool<T> get(int index) {return this.order.get(index);}
        public int size() {return this.order.size();}
        public WeightedPool<T> getFirst() {return this.order.getFirst();}
        public WeightedPool<T> getLast() {return this.order.getLast();}
    }
}
