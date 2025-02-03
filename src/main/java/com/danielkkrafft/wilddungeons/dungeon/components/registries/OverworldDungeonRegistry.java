package com.danielkkrafft.wilddungeons.dungeon.components.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.dungeon.components.template.*;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonOpenBehavior;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry.*;


public class OverworldDungeonRegistry {
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_0 = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_1 = new WeightedPool<>();
    public static final WeightedPool<DungeonMaterial> OVERWORLD_MATERIAL_POOL_2 = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SPRAWL_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SPACER_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_REST_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_LOOT_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonRoomTemplate> OVERWORLD_SHOP_ROOM_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonFloorTemplate> OVERWORLD_FLOOR_POOL = new WeightedPool<>();
    public static final WeightedPool<DungeonTemplate> OVERWORLD_DUNGEON_POOL = new WeightedPool<>();

    public static void register(){

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial("OVERWORLD_MATERIAL_0",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 2)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 1)
                        .add(Blocks.STONE.defaultBlockState(), 4)
                        .add(Blocks.GRANITE.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE.defaultBlockState(), 1)
                        .add(Blocks.DIORITE.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 2)
                        .add(Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.STONE_STAIRS.defaultBlockState(), 4)
                        .add(Blocks.GRANITE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_STAIRS.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2)
                        .add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.STONE_SLAB.defaultBlockState(), 4)
                        .add(Blocks.GRANITE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_SLAB.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_WALL.defaultBlockState(), 2)
                        .add(Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 1)
                        .add(Blocks.GRANITE_WALL.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_WALL.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_WALL.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.LANTERN.defaultBlockState(), 50)
                        .add(Blocks.SOUL_LANTERN.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 1)
                        .add(Blocks.STONE.defaultBlockState(), 1)
                        .add(Blocks.GRANITE.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE.defaultBlockState(), 1)
                        .add(Blocks.DIORITE.defaultBlockState(), 1)
                ),
                0.33f
        )
                .pool(OVERWORLD_MATERIAL_POOL_0, 1).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial("OVERWORLD_MATERIAL_1",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5)
                        .add(Blocks.STONE.defaultBlockState(), 4)
                        .add(Blocks.GRANITE.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE.defaultBlockState(), 1)
                        .add(Blocks.DIORITE.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                        .add(Blocks.STONE_STAIRS.defaultBlockState(), 4)
                        .add(Blocks.GRANITE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_STAIRS.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_STAIRS.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                        .add(Blocks.STONE_SLAB.defaultBlockState(), 4)
                        .add(Blocks.GRANITE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_SLAB.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_SLAB.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_WALL.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                        .add(Blocks.GRANITE_WALL.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE_WALL.defaultBlockState(), 1)
                        .add(Blocks.DIORITE_WALL.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.LANTERN.defaultBlockState(), 45)
                        .add(Blocks.SOUL_LANTERN.defaultBlockState(), 5)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10)
                        .add(Blocks.STONE.defaultBlockState(), 1)
                        .add(Blocks.GRANITE.defaultBlockState(), 1)
                        .add(Blocks.ANDESITE.defaultBlockState(), 1)
                        .add(Blocks.DIORITE.defaultBlockState(), 1)
                ),
                0.33f
        )
                .pool(OVERWORLD_MATERIAL_POOL_1, 1).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_MATERIAL_REGISTRY.add(new DungeonMaterial("OVERWORLD_MATERIAL_2",
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5)
                        .add(Blocks.TUFF.defaultBlockState(), 3)
                        .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_STAIRS.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                        .add(Blocks.TUFF_STAIRS.defaultBlockState(), 3)
                        .add(Blocks.COBBLED_DEEPSLATE_STAIRS.defaultBlockState(), 3)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                        .add(Blocks.TUFF_SLAB.defaultBlockState(), 3)
                        .add(Blocks.COBBLED_DEEPSLATE_SLAB.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE_WALL.defaultBlockState(), 7)
                        .add(Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                        .add(Blocks.TUFF_WALL.defaultBlockState(), 3)
                        .add(Blocks.COBBLED_DEEPSLATE_WALL.defaultBlockState(), 1)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.LANTERN.defaultBlockState(), 40)
                        .add(Blocks.SOUL_LANTERN.defaultBlockState(), 10)
                ),
                List.of(new WeightedPool<BlockState>()
                        .add(Blocks.COBBLESTONE.defaultBlockState(), 1)
                        .add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10)
                        .add(Blocks.TUFF.defaultBlockState(), 1)
                        .add(Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 1)
                ),
                0.33f
        )
                .pool(OVERWORLD_MATERIAL_POOL_2, 1).pool(ALL_MATERIAL_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                DungeonRoomTemplate.Type.NONE,
                "overworld_start",
                List.of(Pair.of("overworld/start", TemplateHelper.EMPTY_BLOCK_POS)),
                null,
                null,
                1.0));


        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_basic_1",
                        List.of(
                                Pair.of("overworld/sprawl/basic_1", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 5));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_basic_2",
                        List.of(
                                Pair.of("overworld/sprawl/basic_2", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 5));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_basic_3",
                        List.of(
                                Pair.of("overworld/sprawl/basic_3", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_basic_4",
                        List.of(
                                Pair.of("overworld/sprawl/basic_4", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 2));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_basic_5",
                        List.of(
                                Pair.of("overworld/sprawl/basic_5", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 2));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.COMBAT,
                        "overworld_basic_3",
                        List.of(
                                Pair.of("overworld/sprawl/basic_3", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 2));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.COMBAT,
                        "overworld_basic_4",
                        List.of(
                                Pair.of("overworld/sprawl/basic_4", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        0.5
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1));


        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.COMBAT,
                        "overworld_basic_5",
                        List.of(
                                Pair.of("overworld/sprawl/basic_5", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        0.75
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_chest_room",
                        List.of(
                                Pair.of("overworld/sprawl/chest_room", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 2));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_staircase",
                        List.of(
                                Pair.of("overworld/sprawl/stair_room", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1));


        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_stairway_1",
                        List.of(
                                Pair.of("overworld/sprawl/stairway_1", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPACER_ROOM_POOL, 2)
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 3)
        );

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_hallway_1",
                        List.of(
                                Pair.of("overworld/sprawl/hallway_1", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPACER_ROOM_POOL, 3)
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1)
        );

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_hallway_2",
                        List.of(
                                Pair.of("overworld/sprawl/hallway_2", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPACER_ROOM_POOL, 3)
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1)
        );

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_crafting",
                        List.of(
                                Pair.of("overworld/crafting", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SPRAWL_ROOM_POOL, 1)
        );

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_restroom_basic",
                        List.of(
                                Pair.of("overworld/rest_basic", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_REST_ROOM_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.SHOP,
                        "overworld_shop",
                        List.of(
                                Pair.of("overworld/shop", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_SHOP_ROOM_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.LOOT,
                        "overworld_free_perk",
                        List.of(
                                Pair.of("overworld/free_perk", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                )
                .pool(OVERWORLD_LOOT_ROOM_POOL, 1));

        DUNGEON_ROOM_REGISTRY.add(DungeonRoomTemplate.build(
                        DungeonRoomTemplate.Type.NONE,
                        "overworld_exit",
                        List.of(
                                Pair.of("overworld/boss", TemplateHelper.EMPTY_BLOCK_POS)
                        ),
                        null,
                        null,
                        1.0
                ));



        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_starter_branch",
                new DungeonRegistry.DungeonLayout<DungeonRoomTemplate>()
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("overworld_start")),
                OVERWORLD_MATERIAL_POOL_0, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_sprawl_0",
                new DungeonRegistry.DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 10),
                OVERWORLD_MATERIAL_POOL_0, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_sprawl_1",
                new DungeonRegistry.DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 10)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_REST_ROOM_POOL, 100)), 1),
                OVERWORLD_MATERIAL_POOL_1, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_sprawl_2",
                new DungeonRegistry.DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 10)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_LOOT_ROOM_POOL, 100)), 1),
                OVERWORLD_MATERIAL_POOL_1, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_sprawl_3",
                new DungeonRegistry.DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPRAWL_ROOM_POOL, 100)), 10)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SHOP_ROOM_POOL, 100)), 1),
                OVERWORLD_MATERIAL_POOL_2, null, 1.0));

        DUNGEON_BRANCH_REGISTRY.add(DungeonBranchTemplate.build("overworld_ending_branch",
                new DungeonLayout<DungeonRoomTemplate>()
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPACER_ROOM_POOL, 100)), 2)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_REST_ROOM_POOL, 100)), 1)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_LOOT_ROOM_POOL, 100)), 1)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPACER_ROOM_POOL, 100)), 1)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SHOP_ROOM_POOL, 100)), 1)
                        .add(WeightedPool.combine(Pair.of(OVERWORLD_SPACER_ROOM_POOL, 100)), 2)
                        .addSimple(DUNGEON_ROOM_REGISTRY.get("overworld_exit")),
                OVERWORLD_MATERIAL_POOL_2, null, 1.0));

        DUNGEON_FLOOR_REGISTRY.add(
                DungeonFloorTemplate.build("overworld_basic",
                                new DungeonRegistry.DungeonLayout<DungeonBranchTemplate>()
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_starter_branch"))
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_sprawl_0"))
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_sprawl_1"))
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_sprawl_2"))
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_sprawl_3"))
                                        .addSimple(DUNGEON_BRANCH_REGISTRY.get("overworld_ending_branch")), null, null, 1.0)
                        .pool(OVERWORLD_FLOOR_POOL, 1)
        );

        DUNGEON_REGISTRY.add(DungeonTemplate.build(
                        "overworld_basic",
                        DungeonOpenBehavior.NONE,
                        new DungeonRegistry.DungeonLayout<DungeonFloorTemplate>()
                                .add(OVERWORLD_FLOOR_POOL, 1),
                        OVERWORLD_MATERIAL_POOL_0,
                        ENEMY_TABLE_REGISTRY.get("BASIC_TABLE"),
                        1.0, 1.1,
                        DungeonSession.DungeonExitBehavior.RANDOMIZE,
                        OVERWORLD_DUNGEON_POOL)
                .pool(OVERWORLD_DUNGEON_POOL, 1)
        );

        OFFERING_TEMPLATE_REGISTRY.add(new DungeonRegistry.OfferingTemplate(
                "OVERWORLD_TEST_RIFT",
                Offering.Type.RIFT,
                1,
                "wd-overworld_basic",
                Offering.CostType.XP_LEVEL,
                30,
                1.5f
        )
                .pool(OVERWORLD_RIFT_POOL, 1)
        );
    }
}
