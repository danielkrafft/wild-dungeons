package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static java.util.List.*;
import static net.minecraft.world.level.block.Blocks.*;

public class DungeonMaterialRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonMaterial> DUNGEON_MATERIAL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();

    public static final DungeonMaterial STONE_BRICK = new DungeonMaterial(
            "STONE_BRICK",
            of(new WeightedPool<BlockState>()
                    .add(STONE_BRICKS.defaultBlockState(), 10)
                    .add(STONE.defaultBlockState(), 10)
                    .add(COAL_ORE.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICKS.defaultBlockState(), 10)
                    .add(CHISELED_STONE_BRICKS.defaultBlockState(), 5)),
            of(new WeightedPool<BlockState>()
                    .add(STONE_BRICK_STAIRS.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(STONE_BRICK_SLAB.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICK_SLAB.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(STONE_BRICK_WALL.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(CRACKED_STONE_BRICKS.defaultBlockState(), 1)),
            0.33f
    );
    public static final DungeonMaterial PRISMARINE =new DungeonMaterial(
            "PRISMARINE",
            of(new WeightedPool<BlockState>()
                    .add(PRISMARINE_BRICKS.defaultBlockState(), 1)
                    .add(Blocks.PRISMARINE.defaultBlockState(), 3)
                    .add(OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                    .add(OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                    .add(DARK_PRISMARINE.defaultBlockState(), 3)),
            of(new WeightedPool<BlockState>()
                    .add(PRISMARINE_STAIRS.defaultBlockState(), 1)
                    .add(PRISMARINE_BRICK_STAIRS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(PRISMARINE_SLAB.defaultBlockState(), 1)
                    .add(PRISMARINE_BRICK_SLAB.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(PRISMARINE_WALL.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(PRISMARINE_BRICKS.defaultBlockState(), 1)),
            0.33f
    );
    public static final DungeonMaterial END_STONE =new DungeonMaterial(
            "END_STONE",
            of(new WeightedPool<BlockState>()
                    .add(Blocks.END_STONE.defaultBlockState(), 5)
                    .add(END_STONE_BRICKS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_STAIRS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_SLAB.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_WALL.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(PEARLESCENT_FROGLIGHT.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(END_STONE_BRICKS.defaultBlockState(), 1)),
            0.33f
    );
    public static final DungeonMaterial OAK_WOOD = new DungeonMaterial(
            "OAK_WOOD",
            of(new WeightedPool<BlockState>()
                    .add(STRIPPED_OAK_WOOD.defaultBlockState(), 2)
                    .add(Blocks.OAK_WOOD.defaultBlockState(), 2)
                    .add(BIRCH_PLANKS.defaultBlockState(), 2)
                    .add(OAK_PLANKS.defaultBlockState(), 5)),
            of(new WeightedPool<BlockState>()
                    .add(BIRCH_STAIRS.defaultBlockState(), 1)
                    .add(OAK_STAIRS.defaultBlockState(), 3)),
            of(new WeightedPool<BlockState>()
                    .add(BIRCH_SLAB.defaultBlockState(), 1)
                    .add(OAK_SLAB.defaultBlockState(), 3)),
            of(new WeightedPool<BlockState>()
                    .add(BIRCH_FENCE.defaultBlockState(), 1)
                    .add(OAK_FENCE.defaultBlockState(), 3)),
            of(new WeightedPool<BlockState>()
                    .add(OCHRE_FROGLIGHT.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(BIRCH_PLANKS.defaultBlockState(), 1)),
            0.33f
    );

    public static final DungeonMaterial OVERWORLD_MATERIAL_0 = new DungeonMaterial(
            "OVERWORLD_MATERIAL_0",
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 1)
                    .add(STONE.defaultBlockState(), 4)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 1)
                    .add(STONE_STAIRS.defaultBlockState(), 4)
                    .add(GRANITE_STAIRS.defaultBlockState(), 1)
                    .add(ANDESITE_STAIRS.defaultBlockState(), 1)
                    .add(DIORITE_STAIRS.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 1)
                    .add(STONE_SLAB.defaultBlockState(), 4)
                    .add(GRANITE_SLAB.defaultBlockState(), 1)
                    .add(ANDESITE_SLAB.defaultBlockState(), 1)
                    .add(DIORITE_SLAB.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 1)
                    .add(GRANITE_WALL.defaultBlockState(), 1)
                    .add(ANDESITE_WALL.defaultBlockState(), 1)
                    .add(DIORITE_WALL.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 50)
                    .add(SOUL_LANTERN.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 1)
                    .add(STONE.defaultBlockState(), 1)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1)
            ),
            0.33f);
    public static final DungeonMaterial OVERWORLD_MATERIAL_1 = new DungeonMaterial(
            "OVERWORLD_MATERIAL_1",
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 5)
                    .add(STONE.defaultBlockState(), 4)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                    .add(STONE_STAIRS.defaultBlockState(), 4)
                    .add(GRANITE_STAIRS.defaultBlockState(), 1)
                    .add(ANDESITE_STAIRS.defaultBlockState(), 1)
                    .add(DIORITE_STAIRS.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                    .add(STONE_SLAB.defaultBlockState(), 4)
                    .add(GRANITE_SLAB.defaultBlockState(), 1)
                    .add(ANDESITE_SLAB.defaultBlockState(), 1)
                    .add(DIORITE_SLAB.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                    .add(GRANITE_WALL.defaultBlockState(), 1)
                    .add(ANDESITE_WALL.defaultBlockState(), 1)
                    .add(DIORITE_WALL.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 45)
                    .add(SOUL_LANTERN.defaultBlockState(), 5)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 10)
                    .add(STONE.defaultBlockState(), 1)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1)
            ),
            0.33f);
    public static final DungeonMaterial OVERWORLD_MATERIAL_2 = new DungeonMaterial(
            "OVERWORLD_MATERIAL_2",
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 5)
                    .add(TUFF.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                    .add(TUFF_STAIRS.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_STAIRS.defaultBlockState(), 3)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                    .add(TUFF_SLAB.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_SLAB.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                    .add(TUFF_WALL.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_WALL.defaultBlockState(), 1)
            ),
            of(new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 40)
                    .add(SOUL_LANTERN.defaultBlockState(), 10)
            ),
            of(new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 10)
                    .add(TUFF.defaultBlockState(), 1)
                    .add(COBBLED_DEEPSLATE.defaultBlockState(), 1)
            ),
            0.33f);
    public static final DungeonMaterial SANDSTONEY = new DungeonMaterial(
            "SANDSTONEY",
            of(new WeightedPool<BlockState>()
                    .add(SANDSTONE.defaultBlockState(), 1)
                    .add(SMOOTH_SANDSTONE.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SANDSTONE_STAIRS.defaultBlockState(), 1)
                    .add(SMOOTH_SANDSTONE_STAIRS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SANDSTONE_SLAB.defaultBlockState(), 1)
                    .add(SMOOTH_SANDSTONE_SLAB.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SANDSTONE_WALL.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(SAND.defaultBlockState(), 1)),
            0.33f
    );
    public static final DungeonMaterial RED_SANDSTONEY = new DungeonMaterial(
            "RED_SANDSTONEY",
            of(new WeightedPool<BlockState>()
                    .add(RED_SANDSTONE.defaultBlockState(), 1)
                    .add(SMOOTH_RED_SANDSTONE.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(RED_SANDSTONE_STAIRS.defaultBlockState(), 1)
                    .add(SMOOTH_RED_SANDSTONE_STAIRS.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(RED_SANDSTONE_SLAB.defaultBlockState(), 1)
                    .add(SMOOTH_RED_SANDSTONE_SLAB.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(RED_SANDSTONE_WALL.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 1)),
            of(new WeightedPool<BlockState>()
                    .add(RED_SAND.defaultBlockState(), 1)),
            0.33f
    );




    public static void setupDungeonMaterials(){
        add(STONE_BRICK);
        add(SANDSTONEY);
        add(RED_SANDSTONEY);
        add(PRISMARINE);
        add(END_STONE);
        add(OAK_WOOD);
        add(OVERWORLD_MATERIAL_0);
        add(OVERWORLD_MATERIAL_1);
        add(OVERWORLD_MATERIAL_2);
    }

    public static void add(DungeonMaterial material){
        DUNGEON_MATERIAL_REGISTRY.add(material);
    }
}
