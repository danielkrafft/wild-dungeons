package com.danielkkrafft.wilddungeons.dungeon.components;

import com.danielkkrafft.wilddungeons.dungeon.*;
import com.danielkkrafft.wilddungeons.dungeon.components.room.EnemyTables;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonRegistry {

    public static final DungeonComponentRegistry<DungeonRoomTemplate> DUNGEON_ROOM_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonBranchTemplate> DUNGEON_BRANCH_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonFloorTemplate> DUNGEON_FLOOR_REGISTRY = new DungeonComponentRegistry<>();
    public static final DungeonComponentRegistry<DungeonTemplate> DUNGEON_REGISTRY = new DungeonComponentRegistry<>();

    public static final DungeonComponentRegistry<DungeonMaterial> DUNGEON_MATERIAL_REGISTRY = new DungeonComponentRegistry<>();
    public static final WeightedPool<DungeonMaterial> ALL_MATERIAL_POOL = new WeightedPool<>();

    public static final DungeonComponentRegistry<DungeonPerkTemplate> DUNGEON_PERK_REGISTRY = new DungeonComponentRegistry<>();
    public static final WeightedPool<DungeonPerkTemplate> ALL_PERKS_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonRoomTemplate> SMALL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> SECRET_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> MEDIUM_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> SHOP_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> LOOT_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> REST_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> PARKOUR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> COMBAT_ROOM_POOL = new WeightedPool<>();

    public static final WeightedPool<DungeonBranchTemplate> BRANCH_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonFloorTemplate> FLOOR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonTemplate> DUNGEON_POOL = new WeightedPool<>();


    public static void setupDungeons() {

        /*
        DUNGEON MATERIALS
         */

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "SANDSTONE",
                List.of(new WeightedPool<BlockState>().add(Blocks.SANDSTONE.defaultBlockState(), 1)
                        .add(Blocks.SMOOTH_SANDSTONE.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SANDSTONE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.SMOOTH_SANDSTONE_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SANDSTONE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.SMOOTH_SANDSTONE_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SANDSTONE_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SHROOMLIGHT.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SAND.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "STONE_BRICK",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.STONE_BRICKS.defaultBlockState(), 10)
                        .add(Blocks.STONE.defaultBlockState(), 10)
                        .add(Blocks.COAL_ORE.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 10)
                        .add(Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 5)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.STONE_BRICK_SLAB.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.STONE_BRICK_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SEA_LANTERN.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "PRISMARINE",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PRISMARINE_BRICKS.defaultBlockState(), 1)
                        .add(Blocks.PRISMARINE.defaultBlockState(), 3)
                        .add(Blocks.OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                        .add(Blocks.OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                        .add(Blocks.DARK_PRISMARINE.defaultBlockState(), 3)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PRISMARINE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.PRISMARINE_BRICK_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PRISMARINE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.PRISMARINE_BRICK_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PRISMARINE_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SEA_LANTERN.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PRISMARINE_BRICKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "NETHER",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.NETHERRACK.defaultBlockState(), 3)
                        .add(Blocks.SOUL_SAND.defaultBlockState(), 1)
                        .add(Blocks.MAGMA_BLOCK.defaultBlockState(), 1)
                        .add(Blocks.NETHER_WART_BLOCK.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.NETHER_BRICK_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.NETHER_BRICK_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.NETHER_BRICK_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.GLOWSTONE.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.CRACKED_NETHER_BRICKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "END_STONE",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.END_STONE.defaultBlockState(), 5)
                        .add(Blocks.END_STONE_BRICKS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.END_STONE_BRICK_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.END_STONE_BRICK_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.END_STONE_BRICK_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.END_STONE_BRICKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "DEEP_DARK",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.DEEPSLATE.defaultBlockState(), 1)
                        .add(Blocks.DEEPSLATE_TILES.defaultBlockState(), 1)
                        .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 1)
                        .add(Blocks.CHISELED_DEEPSLATE.defaultBlockState(), 1)
                        .add(Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.DEEPSLATE_TILE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.POLISHED_DEEPSLATE_STAIRS.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.DEEPSLATE_BRICK_SLAB.defaultBlockState(), 1)
                        .add(Blocks.DEEPSLATE_TILE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.POLISHED_DEEPSLATE_SLAB.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), 1)
                        .add(Blocks.DEEPSLATE_TILE_WALL.defaultBlockState(), 1)
                        .add(Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.SHROOMLIGHT.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial( "OAK_WOOD",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.STRIPPED_OAK_WOOD.defaultBlockState(), 2)
                        .add(Blocks.OAK_WOOD.defaultBlockState(), 2)
                        .add(Blocks.BIRCH_PLANKS.defaultBlockState(), 2)
                        .add(Blocks.OAK_PLANKS.defaultBlockState(), 5)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.BIRCH_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.OAK_STAIRS.defaultBlockState(), 3)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.BIRCH_SLAB.defaultBlockState(), 1)
                        .add(Blocks.OAK_SLAB.defaultBlockState(), 3)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.BIRCH_FENCE.defaultBlockState(), 1)
                        .add(Blocks.OAK_FENCE.defaultBlockState(), 3)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.OCHRE_FROGLIGHT.defaultBlockState(), 1)),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.BIRCH_PLANKS.defaultBlockState(), 1))
        ).pool(ALL_MATERIAL_POOL, 1));

        /*
        ROOMS
         */

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE, "stone/small_1", List.of(Pair.of("stone/small_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/small_2", List.of(Pair.of("stone/small_2", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/small_3", List.of(Pair.of("stone/small_3", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/small_4", List.of(Pair.of("stone/small_4", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/small_5", List.of(Pair.of("stone/composite_1", TemplateHelper.EMPTY_BLOCK_POS), Pair.of("stone/composite_2", new BlockPos(2,0,4))), null, null, 1.0).pool(SMALL_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/medium_1", List.of(Pair.of("stone/medium_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/medium_2", List.of(Pair.of("stone/medium_2", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/medium_3", List.of(Pair.of("stone/medium_3", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/medium_4", List.of(
                Pair.of("stone/medium_4_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/medium_4_comp_2", new BlockPos(4, 0, -17))), null, null, 1.0).pool(MEDIUM_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/large_1", List.of(Pair.of("stone/large_1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(COMBAT_ROOM_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/start", List.of(Pair.of("stone/start", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"stone/boss", List.of(
                Pair.of("stone/boss_comp_1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("stone/boss_comp_2", new BlockPos(0, 0, 48)),
                Pair.of("stone/boss_comp_3", new BlockPos(20,20,-16))), null, null, 1.0));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.SECRET,"secret/1", List.of(Pair.of("secret/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SECRET_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.SHOP,"shop/1", List.of(Pair.of("shop/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(SHOP_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.SHOP,"shop/branch", List.of(Pair.of("shop/1", TemplateHelper.EMPTY_BLOCK_POS)), new WeightedPool<DungeonMaterial>().add(DUNGEON_MATERIAL_REGISTRY.get("NETHER"), 1), null, 1.0));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.LOOT,"loot", List.of(
                Pair.of("loot/1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("loot/2", new BlockPos(0, 4, 0))), null, null, 1.0).pool(LOOT_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE,"rest", List.of(
                Pair.of("rest/1", TemplateHelper.EMPTY_BLOCK_POS),
                Pair.of("rest/2", new BlockPos(-7, 0, 3)),
                Pair.of("rest/3", new BlockPos(3, 0, -7)),
                Pair.of("rest/4", new BlockPos(12, 0, 3)),
                Pair.of("rest/5", new BlockPos(3, 0, 12))), null, null, 1.0).pool(REST_POOL, 1));
        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(DungeonRoomTemplate.Type.NONE, "parkour", List.of(Pair.of("parkour/1", TemplateHelper.EMPTY_BLOCK_POS)), null, null, 1.0).pool(PARKOUR_POOL, 1));


        /*
        BRANCHES
         */

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("starter_room_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/start")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("small_room_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(SMALL_ROOM_POOL, 95), Pair.of(SECRET_POOL, 5)), 30)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/large_1")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("tons_of_shop_rooms_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(SHOP_POOL, 100)), 100), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("medium_room_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .add(MEDIUM_ROOM_POOL, 15)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/large_1")), null, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("all_branch",
                        new DungeonLayout<DungeonRoomTemplate>()
                                .add(WeightedPool.combine(Pair.of(SMALL_ROOM_POOL, 150), Pair.of(MEDIUM_ROOM_POOL, 150), Pair.of(SECRET_POOL, 20), Pair.of(COMBAT_ROOM_POOL, 50), Pair.of(PARKOUR_POOL, 50), Pair.of(LOOT_POOL, 50), Pair.of(REST_POOL, 50)), 30)
                                .addSimple(DUNGEON_ROOM_REGISTRY.get("shop/branch")), null, null, 1.0)
                .pool(BRANCH_POOL, 1));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("ending_room_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .add(MEDIUM_ROOM_POOL, 10)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("stone/boss")),
                new WeightedPool<DungeonMaterial>()
                        .add(DUNGEON_MATERIAL_REGISTRY.get("NETHER"), 1), null, 1.0));

        /*
        FLOORS
         */

        DUNGEON_FLOOR_REGISTRY.add(DungeonFloorTemplate.build("test_floor",
                new DungeonLayout<DungeonBranchTemplate>()
                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("starter_room_branch"))
                        .add(new WeightedPool<DungeonBranchTemplate>().add(DUNGEON_BRANCH_REGISTRY.get("all_branch"), 1), 1)
                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("ending_room_branch")), null, null, 1.0)
                .pool(FLOOR_POOL, 1));

        DUNGEON_FLOOR_REGISTRY.add(DungeonFloorTemplate.build("shop_floor",
                        new DungeonLayout<DungeonBranchTemplate>()
                                .addSimple(DUNGEON_BRANCH_REGISTRY.get("starter_room_branch"))
                                .addSimple(DUNGEON_BRANCH_REGISTRY.get("tons_of_shop_rooms_branch"))
                                .addSimple(DUNGEON_BRANCH_REGISTRY.get("ending_room_branch")), null, null, 1.0));

        /*
        DUNGEONS
         */

        DUNGEON_REGISTRY.add(DungeonTemplate.build("dungeon_1", DungeonOpenBehavior.NONE, new DungeonLayout<DungeonFloorTemplate>()
                .add(FLOOR_POOL, 1),
                ALL_MATERIAL_POOL,
                EnemyTables.BASIC_TABLE,
                1.0,
                DungeonSession.DungeonExitBehavior.NOTHING,
                DUNGEON_POOL).pool(DUNGEON_POOL, 1));

        DUNGEON_REGISTRY.add(DungeonTemplate.build("shop_dungeon", DungeonOpenBehavior.NONE, new DungeonLayout<DungeonFloorTemplate>()
                .addSimple(DUNGEON_FLOOR_REGISTRY.get("shop_floor")),
                ALL_MATERIAL_POOL,
                EnemyTables.BASIC_TABLE,
                1.0,
                DungeonSession.DungeonExitBehavior.NEW_DUNGEON,
                DUNGEON_POOL));//.pool(DUNGEON_POOL, 1));

        /*
        DUNGEON PERKS
         */

        DUNGEON_PERK_REGISTRY.add(new DungeonPerkTemplate("SWORD_DAMAGE_INCREASE", new Vector2i(0, 0)).pool(ALL_PERKS_POOL, 1));
        DUNGEON_PERK_REGISTRY.add(new DungeonPerkTemplate("AXE_DAMAGE_INCREASE", new Vector2i(1, 0)).pool(ALL_PERKS_POOL, 1));
        DUNGEON_PERK_REGISTRY.add(new DungeonPerkTemplate("BOW_DAMAGE_INCREASE", new Vector2i(2, 0)).pool(ALL_PERKS_POOL, 1));
        DUNGEON_PERK_REGISTRY.add(new DungeonPerkTemplate("EXTRA_LIFE", new Vector2i(3, 0)).pool(ALL_PERKS_POOL, 1));


    }

    public static class DungeonComponentRegistry<T extends DungeonComponent> {
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
