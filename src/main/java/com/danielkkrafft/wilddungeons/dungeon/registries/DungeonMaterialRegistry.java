package com.danielkkrafft.wilddungeons.dungeon.registries;

import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

import static net.minecraft.world.level.block.Blocks.*;

public class DungeonMaterialRegistry {
    public static final DungeonRegistration.DungeonComponentRegistry<DungeonMaterial> DUNGEON_MATERIAL_REGISTRY = new DungeonRegistration.DungeonComponentRegistry<>();
    public static ArrayList<DungeonMaterial> dungeonMaterials = new ArrayList<>();

    public static final DungeonMaterial STONE_BRICK = create(
            "STONE_BRICK",
            new WeightedPool<BlockState>()
                    .add(STONE_BRICKS.defaultBlockState(), 10)
                    .add(STONE.defaultBlockState(), 10)
                    .add(COAL_ORE.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICKS.defaultBlockState(), 10)
                    .add(CHISELED_STONE_BRICKS.defaultBlockState(), 5),
            new WeightedPool<BlockState>()
                    .add(STONE_BRICK_STAIRS.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICK_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(STONE_BRICK_SLAB.defaultBlockState(), 1)
                    .add(MOSSY_STONE_BRICK_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(STONE_BRICK_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(CRACKED_STONE_BRICKS.defaultBlockState(), 1));
    public static final DungeonMaterial PRISMARINE = create(
            "PRISMARINE",
            new WeightedPool<BlockState>()
                    .add(PRISMARINE_BRICKS.defaultBlockState(), 1)
                    .add(Blocks.PRISMARINE.defaultBlockState(), 3)
                    .add(OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                    .add(OXIDIZED_CUT_COPPER.defaultBlockState(), 3)
                    .add(DARK_PRISMARINE.defaultBlockState(), 3),
            new WeightedPool<BlockState>()
                    .add(PRISMARINE_STAIRS.defaultBlockState(), 1)
                    .add(PRISMARINE_BRICK_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(PRISMARINE_SLAB.defaultBlockState(), 1)
                    .add(PRISMARINE_BRICK_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(PRISMARINE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(PRISMARINE_BRICKS.defaultBlockState(), 1));
    public static final DungeonMaterial END_STONE = create(
            "END_STONE",
            new WeightedPool<BlockState>()
                    .add(Blocks.END_STONE.defaultBlockState(), 5)
                    .add(END_STONE_BRICKS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(END_STONE_BRICK_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(PEARLESCENT_FROGLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(PEARLESCENT_FROGLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(END_STONE_BRICKS.defaultBlockState(), 1));
    public static final DungeonMaterial WOOD = create(
            "OAK_WOOD",
            new WeightedPool<BlockState>()
                    .add(STRIPPED_OAK_WOOD.defaultBlockState(), 2)
                    .add(Blocks.OAK_WOOD.defaultBlockState(), 2)
                    .add(BIRCH_PLANKS.defaultBlockState(), 2)
                    .add(OAK_PLANKS.defaultBlockState(), 5),
            new WeightedPool<BlockState>()
                    .add(BIRCH_STAIRS.defaultBlockState(), 1)
                    .add(OAK_STAIRS.defaultBlockState(), 3),
            new WeightedPool<BlockState>()
                    .add(BIRCH_SLAB.defaultBlockState(), 1)
                    .add(OAK_SLAB.defaultBlockState(), 3),
            new WeightedPool<BlockState>()
                    .add(BIRCH_FENCE.defaultBlockState(), 1)
                    .add(OAK_FENCE.defaultBlockState(), 3),
            new WeightedPool<BlockState>()
                    .add(OCHRE_FROGLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(OCHRE_FROGLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(BIRCH_PLANKS.defaultBlockState(), 1));

    public static final DungeonMaterial TRIAL_MATERIAL = create(
            "TRIAL_MATERIAL",
            new WeightedPool<BlockState>()
                    .add(TUFF_BRICKS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(TUFF_BRICK_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(TUFF_BRICK_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(TUFF_BRICK_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COPPER_BULB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COPPER_BULB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(CHISELED_TUFF_BRICKS.defaultBlockState(), 1)
    );

    public static final DungeonMaterial OVERWORLD_MATERIAL_0 = create(
            "OVERWORLD_MATERIAL_0",
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 1)
                    .add(STONE.defaultBlockState(), 4)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 1)
                    .add(STONE_STAIRS.defaultBlockState(), 4)
                    .add(GRANITE_STAIRS.defaultBlockState(), 1)
                    .add(ANDESITE_STAIRS.defaultBlockState(), 1)
                    .add(DIORITE_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 1)
                    .add(STONE_SLAB.defaultBlockState(), 4)
                    .add(GRANITE_SLAB.defaultBlockState(), 1)
                    .add(ANDESITE_SLAB.defaultBlockState(), 1)
                    .add(DIORITE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 2)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 1)
                    .add(GRANITE_WALL.defaultBlockState(), 1)
                    .add(ANDESITE_WALL.defaultBlockState(), 1)
                    .add(DIORITE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 50)
                    .add(SOUL_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 50)
                    .add(SOUL_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 1)
                    .add(STONE.defaultBlockState(), 1)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(COBBLESTONE.defaultBlockState(), 1)
                            .add(STONE.defaultBlockState(), 4)
                            .add(STONE_BRICKS.defaultBlockState(), 3))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(COBBLESTONE_STAIRS.defaultBlockState(), 1)
                            .add(STONE_STAIRS.defaultBlockState(), 4)
                            .add(STONE_BRICK_STAIRS.defaultBlockState(), 3))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(COBBLESTONE_SLAB.defaultBlockState(), 1)
                            .add(STONE_SLAB.defaultBlockState(), 4)
                            .add(STONE_BRICK_SLAB.defaultBlockState(), 3))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(COBBLESTONE_WALL.defaultBlockState(), 1)
                            .add(STONE_BRICK_WALL.defaultBlockState(), 3));
    public static final DungeonMaterial OVERWORLD_MATERIAL_1 = create(
            "OVERWORLD_MATERIAL_1",
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 5)
                    .add(STONE.defaultBlockState(), 4)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                    .add(STONE_STAIRS.defaultBlockState(), 4)
                    .add(GRANITE_STAIRS.defaultBlockState(), 1)
                    .add(ANDESITE_STAIRS.defaultBlockState(), 1)
                    .add(DIORITE_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                    .add(STONE_SLAB.defaultBlockState(), 4)
                    .add(GRANITE_SLAB.defaultBlockState(), 1)
                    .add(ANDESITE_SLAB.defaultBlockState(), 1)
                    .add(DIORITE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                    .add(GRANITE_WALL.defaultBlockState(), 1)
                    .add(ANDESITE_WALL.defaultBlockState(), 1)
                    .add(DIORITE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 45)
                    .add(SOUL_LANTERN.defaultBlockState(), 5),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 45)
                    .add(SOUL_LANTERN.defaultBlockState(), 5),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 10)
                    .add(STONE.defaultBlockState(), 1)
                    .add(GRANITE.defaultBlockState(), 1)
                    .add(ANDESITE.defaultBlockState(), 1)
                    .add(DIORITE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(STONE.defaultBlockState(), 4)
                            .add(STONE_BRICKS.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(STONE_STAIRS.defaultBlockState(), 4)
                            .add(STONE_BRICK_STAIRS.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(STONE_SLAB.defaultBlockState(), 4)
                            .add(STONE_BRICK_SLAB.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(STONE_BRICK_WALL.defaultBlockState(), 5));
    public static final DungeonMaterial OVERWORLD_MATERIAL_2 = create(
            "OVERWORLD_MATERIAL_2",
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 5)
                    .add(TUFF.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_STAIRS.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(), 5)
                    .add(TUFF_STAIRS.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_STAIRS.defaultBlockState(), 3),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_SLAB.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 5)
                    .add(TUFF_SLAB.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE_WALL.defaultBlockState(), 7)
                    .add(MOSSY_COBBLESTONE_WALL.defaultBlockState(), 5)
                    .add(TUFF_WALL.defaultBlockState(), 3)
                    .add(COBBLED_DEEPSLATE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 40)
                    .add(SOUL_LANTERN.defaultBlockState(), 10),
            new WeightedPool<BlockState>()
                    .add(LANTERN.defaultBlockState(), 40)
                    .add(SOUL_LANTERN.defaultBlockState(), 10),
            new WeightedPool<BlockState>()
                    .add(COBBLESTONE.defaultBlockState(), 1)
                    .add(MOSSY_COBBLESTONE.defaultBlockState(), 10)
                    .add(TUFF.defaultBlockState(), 1)
                    .add(COBBLED_DEEPSLATE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(TUFF_BRICKS.defaultBlockState(), 4)
                            .add(DEEPSLATE_BRICKS.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(TUFF_BRICK_STAIRS.defaultBlockState(), 4)
                            .add(DEEPSLATE_BRICK_STAIRS.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(TUFF_BRICK_SLAB.defaultBlockState(), 4)
                            .add(DEEPSLATE_BRICK_SLAB.defaultBlockState(), 5))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(TUFF_BRICK_WALL.defaultBlockState(), 4)
                            .add(DEEPSLATE_BRICK_WALL.defaultBlockState(), 5));

    public static final DungeonMaterial PIGLIN_FACTORY_MATERIAL = create(
            "PIGLIN_FACTORY_MATERIAL",
            new WeightedPool<BlockState>()
                    .add(NETHERRACK.defaultBlockState(), 10)
                    .add(NETHER_QUARTZ_ORE.defaultBlockState(), 1)
                    .add(NETHER_GOLD_ORE.defaultBlockState(), 1)
                    .add(SOUL_SAND.defaultBlockState(), 1)
                    .add(NETHER_WART_BLOCK.defaultBlockState(), 1)
                    .add(MAGMA_BLOCK.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(CRIMSON_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(CRIMSON_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(CRIMSON_FENCE.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(GLOWSTONE.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(SOUL_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>()
                    .add(NETHER_WART_BLOCK.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE.defaultBlockState(), 5)
                            .add(GILDED_BLACKSTONE.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 1)
                            .add(CHISELED_POLISHED_BLACKSTONE.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(GLASS.defaultBlockState(), 10)
                            .add(GRAY_STAINED_GLASS.defaultBlockState(), 2)
                            .add(TINTED_GLASS.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>()
                            .add(NETHER_BRICKS.defaultBlockState(), 5)
                            .add(CRACKED_NETHER_BRICKS.defaultBlockState(), 2)
                            .add(CHISELED_NETHER_BRICKS.defaultBlockState(), 2)
                            .add(RED_NETHER_BRICKS.defaultBlockState(), 2)
            )

            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_STAIRS.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_STAIRS.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_STAIRS.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR,
                    new WeightedPool<BlockState>()
                            .add(NETHER_BRICK_STAIRS.defaultBlockState(), 5)
                            .add(RED_NETHER_BRICK_STAIRS.defaultBlockState(), 1)
            )

            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_SLAB.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_SLAB.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_BRICK_SLAB.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_SLAB.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB,
                    new WeightedPool<BlockState>()
                            .add(NETHER_BRICK_SLAB.defaultBlockState(), 5)
                            .add(RED_NETHER_BRICK_SLAB.defaultBlockState(), 1)
            )

            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_WALL.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_WALL.defaultBlockState(), 1)
                            .add(POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(BLACKSTONE_WALL.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.WALL,
                    new WeightedPool<BlockState>()
                            .add(NETHER_BRICK_WALL.defaultBlockState(), 5)
                            .add(RED_NETHER_BRICK_WALL.defaultBlockState(), 1)
            )
            .add(DungeonMaterial.BlockSetting.BlockType.LIGHT,
                    new WeightedPool<BlockState>().add(SHROOMLIGHT.defaultBlockState(), 1)
            );

    public static final DungeonMaterial VILLAGE_SEWER_MATERIAL = create("village_sewer",
            new WeightedPool<BlockState>().add(COBBLESTONE.defaultBlockState(),3).add(MOSSY_COBBLESTONE.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(COBBLESTONE_STAIRS.defaultBlockState(),3).add(MOSSY_COBBLESTONE_STAIRS.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(COBBLESTONE_SLAB.defaultBlockState(),3).add(MOSSY_COBBLESTONE_SLAB.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(COBBLESTONE_WALL.defaultBlockState(),3).add(MOSSY_COBBLESTONE_WALL.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(LANTERN.defaultBlockState(),5).add(SOUL_LANTERN.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(LANTERN.defaultBlockState(),5).add(SOUL_LANTERN.defaultBlockState(),1),
            new WeightedPool<BlockState>().add(MOSSY_COBBLESTONE.defaultBlockState(),1)
    )
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC,
                    new WeightedPool<BlockState>().add(POLISHED_ANDESITE.defaultBlockState(),1)
            );

    public static final DungeonMaterial VILLAGE_MATERIAL = create("village_metro",
        new WeightedPool<BlockState>().add(POLISHED_ANDESITE.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_ANDESITE_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_ANDESITE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(ANDESITE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_ANDESITE.defaultBlockState(), 1)
    )
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC, new WeightedPool<BlockState>().add(SMOOTH_STONE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR, new WeightedPool<BlockState>().add(OAK_STAIRS.defaultBlockState(),1))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB, new WeightedPool<BlockState>().add(OAK_SLAB.defaultBlockState(),1))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL, new WeightedPool<BlockState>().add(OAK_FENCE.defaultBlockState(),1))
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC, new WeightedPool<BlockState>().add(AIR.defaultBlockState(), 1));


    public static final DungeonMaterial TRANSLATION_MATERIAL = create("translation_debug",//todo delete when all rooms have been converted
            new WeightedPool<BlockState>().add(COBBLESTONE.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(COBBLESTONE_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(COBBLESTONE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(COBBLESTONE_WALL.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SHROOMLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(STONE.defaultBlockState(), 1)
    )
            .add(DungeonMaterial.BlockSetting.BlockType.BASIC, new WeightedPool<BlockState>().add(SANDSTONE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR, new WeightedPool<BlockState>().add(SANDSTONE_STAIRS.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB, new WeightedPool<BlockState>().add(SANDSTONE_SLAB.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL, new WeightedPool<BlockState>().add(SANDSTONE_WALL.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.LIGHT, new WeightedPool<BlockState>().add(VERDANT_FROGLIGHT.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HANGING_LIGHT, new WeightedPool<BlockState>().add(REDSTONE_LAMP.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HIDDEN, new WeightedPool<BlockState>().add(SANDSTONE.defaultBlockState(), 1))

            .add(DungeonMaterial.BlockSetting.BlockType.BASIC, new WeightedPool<BlockState>().add(OAK_PLANKS.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR, new WeightedPool<BlockState>().add(OAK_STAIRS.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB, new WeightedPool<BlockState>().add(OAK_SLAB.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL, new WeightedPool<BlockState>().add(OAK_FENCE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.LIGHT, new WeightedPool<BlockState>().add(PEARLESCENT_FROGLIGHT.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HANGING_LIGHT, new WeightedPool<BlockState>().add(OCHRE_FROGLIGHT.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HIDDEN, new WeightedPool<BlockState>().add(OAK_PLANKS.defaultBlockState(), 1))

            .add(DungeonMaterial.BlockSetting.BlockType.BASIC, new WeightedPool<BlockState>().add(NETHERRACK.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.STAIR, new WeightedPool<BlockState>().add(NETHER_BRICK_STAIRS.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.SLAB, new WeightedPool<BlockState>().add(NETHER_BRICK_SLAB.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.WALL, new WeightedPool<BlockState>().add(NETHER_BRICK_WALL.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.LIGHT, new WeightedPool<BlockState>().add(JACK_O_LANTERN.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HANGING_LIGHT, new WeightedPool<BlockState>().add(GLOWSTONE.defaultBlockState(), 1))
            .add(DungeonMaterial.BlockSetting.BlockType.HIDDEN, new WeightedPool<BlockState>().add(NETHERRACK.defaultBlockState(), 1))
            ;

    public static final DungeonMaterial SCIFI_GAUNTLET_MATERIAL = create("scifi_gauntlet_materials",//todo delete when all rooms have been converted
            new WeightedPool<BlockState>().add(QUARTZ_BLOCK.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(QUARTZ_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(QUARTZ_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(QUARTZ_BLOCK.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SHROOMLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(QUARTZ_BLOCK.defaultBlockState(), 1));

    public static final DungeonMaterial TRIAL_GAUNTLET_MATERIAL = create("trial_gauntlet_materials",//todo delete when all rooms have been converted
            new WeightedPool<BlockState>().add(TUFF_BRICKS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(TUFF_BRICK_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(TUFF_BRICK_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(TUFF_BRICKS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SHROOMLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(TUFF_BRICKS.defaultBlockState(), 1));

    public static final DungeonMaterial GENERAL_GAUNTLET_MATERIAL = create("general_gauntlet_materials",//todo delete when all rooms have been converted
            new WeightedPool<BlockState>().add(POLISHED_DEEPSLATE.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_DEEPSLATE_STAIRS.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_DEEPSLATE_SLAB.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_DEEPSLATE.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SHROOMLIGHT.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(SEA_LANTERN.defaultBlockState(), 1),
            new WeightedPool<BlockState>().add(POLISHED_DEEPSLATE.defaultBlockState(), 1));

    public static DungeonMaterial create(String name, WeightedPool<BlockState> defaultBasicBlocks, WeightedPool<BlockState> defaultStairBlocks, WeightedPool<BlockState> defaultSlabBlocks, WeightedPool<BlockState> defaultWallBlocks, WeightedPool<BlockState> defaultLightBlocks, WeightedPool<BlockState> defaultHangingLights, WeightedPool<BlockState> defaultHiddenBlocks) {
        DungeonMaterial material = new DungeonMaterial(name, defaultBasicBlocks, defaultStairBlocks, defaultSlabBlocks, defaultWallBlocks, defaultLightBlocks, defaultHangingLights, defaultHiddenBlocks);
        dungeonMaterials.add(material);
        DUNGEON_MATERIAL_REGISTRY.add(material);
        return material;
    }
}