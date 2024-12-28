package com.danielkkrafft.wilddungeons.dungeon;

import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class DungeonMaterials {

    public static final DungeonMaterial SANDSTONE = new DungeonMaterial(
            List.of(List.of(Blocks.SANDSTONE.defaultBlockState(), Blocks.SMOOTH_SANDSTONE.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.SANDSTONE_STAIRS.defaultBlockState(), Blocks.SMOOTH_SANDSTONE_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.SANDSTONE_SLAB.defaultBlockState(), Blocks.SMOOTH_SANDSTONE_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.SANDSTONE_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.SHROOMLIGHT.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial STONE_BRICK = new DungeonMaterial(
            List.of(List.of(Blocks.STONE_BRICKS.defaultBlockState(), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.MOSSY_STONE_BRICKS.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.STONE_BRICK_STAIRS.defaultBlockState(), Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.STONE_BRICK_SLAB.defaultBlockState(), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.STONE_BRICK_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.SEA_LANTERN.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial PRISMARINE = new DungeonMaterial(
            List.of(List.of(Blocks.PRISMARINE.defaultBlockState(), Blocks.PRISMARINE_BRICKS.defaultBlockState(), Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(), Blocks.LIGHT_BLUE_WOOL.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.PRISMARINE_STAIRS.defaultBlockState(), Blocks.PRISMARINE_BRICK_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.PRISMARINE_SLAB.defaultBlockState(), Blocks.PRISMARINE_BRICK_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.PRISMARINE_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.SEA_LANTERN.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial NETHER = new DungeonMaterial(
            List.of(List.of(Blocks.NETHERRACK.defaultBlockState(), Blocks.NETHERRACK.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.NETHER_BRICK_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.NETHER_BRICK_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.NETHER_BRICK_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.GLOWSTONE.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial END_STONE = new DungeonMaterial(
            List.of(List.of(Blocks.END_STONE.defaultBlockState(), Blocks.END_STONE_BRICKS.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.END_STONE_BRICK_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.END_STONE_BRICK_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.END_STONE_BRICK_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial DEEP_DARK = new DungeonMaterial(
            List.of(List.of(Blocks.DEEPSLATE.defaultBlockState(), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), Blocks.DEEPSLATE_TILES.defaultBlockState(), Blocks.COBBLED_DEEPSLATE.defaultBlockState(), Blocks.CHISELED_DEEPSLATE.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState(), Blocks.DEEPSLATE_TILE_STAIRS.defaultBlockState(), Blocks.POLISHED_DEEPSLATE_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.DEEPSLATE_BRICK_SLAB.defaultBlockState(), Blocks.DEEPSLATE_TILE_SLAB.defaultBlockState(), Blocks.POLISHED_DEEPSLATE_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), Blocks.DEEPSLATE_TILE_WALL.defaultBlockState(), Blocks.POLISHED_DEEPSLATE_WALL.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.SHROOMLIGHT.defaultBlockState())) //LIGHT SET 1
    );

    public static final DungeonMaterial OAK_WOOD = new DungeonMaterial(
            List.of(List.of(Blocks.STRIPPED_OAK_WOOD.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState())), //BASIC SET 1
            List.of(List.of(Blocks.OAK_STAIRS.defaultBlockState())), //STAIR SET 1
            List.of(List.of(Blocks.OAK_SLAB.defaultBlockState())), //SLAB SET 1
            List.of(List.of(Blocks.OAK_FENCE.defaultBlockState())), //WALL SET 1
            List.of(List.of(Blocks.OCHRE_FROGLIGHT.defaultBlockState())) //LIGHT SET 1
    );

}
