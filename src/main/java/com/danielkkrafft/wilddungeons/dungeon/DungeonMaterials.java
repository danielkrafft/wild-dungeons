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

}
