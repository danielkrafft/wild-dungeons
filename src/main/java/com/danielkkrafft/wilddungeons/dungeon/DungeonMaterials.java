package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DungeonMaterials {

    public static final DungeonMaterial SANDSTONE = new DungeonMaterial(
            List.of(new WeightedPool<BlockState>()
                    .add(Blocks.SANDSTONE.defaultBlockState(), 1)
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
    );

    public static final DungeonMaterial STONE_BRICK = new DungeonMaterial(
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
    );

    public static final DungeonMaterial PRISMARINE = new DungeonMaterial(
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
    );

    public static final DungeonMaterial NETHER = new DungeonMaterial(
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
    );

    public static final DungeonMaterial END_STONE = new DungeonMaterial(
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
    );

    public static final DungeonMaterial DEEP_DARK = new DungeonMaterial(
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
    );

    public static final DungeonMaterial OAK_WOOD = new DungeonMaterial(
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
    );

}
