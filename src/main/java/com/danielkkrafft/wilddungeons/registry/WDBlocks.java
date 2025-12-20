package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.danielkkrafft.wilddungeons.registry.WDItems.ITEMS;

public class WDBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildDungeons.MODID);

    public static final DeferredBlock<Block> CONNECTION_BLOCK = registerWithItem("connection_block", () -> new ConnectionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F)));
    public static final DeferredBlock<Block> SPAWN_BLOCK = registerWithItem("spawn_block", () -> new Block(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> LIFE_LIQUID = BLOCKS.register("life_liquid", () -> new LifeLiquidBlock(WDFluids.LIFE_LIQUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredBlock<Block> TOXIC_SLUDGE = BLOCKS.register("toxic_sludge", () -> new ToxicSludgeBlock(WDFluids.TOXIC_SLUDGE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).liquid().mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<Block> TOXIC_GAS = registerWithItem("toxic_gas",() -> new ToxicGasBlock(BlockBehaviour.Properties.of().strength(0F).ignitedByLava().noCollission().noLootTable().isSuffocating((a, b, c) -> true)));
    public static final DeferredBlock<Block> ROTTEN_MOSS = registerWithItem("rotten_moss", RottenMossBlock::new);
    public static final DeferredBlock<Block> HEAVY_RUNE = registerWithItem("heavy_rune", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.DEEPSLATE)
            .strength(55, 1200)));

    public static final DeferredBlock<Block> WD_BASIC = registerWithItem("wd_basic", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WD_BASIC_2 = registerWithItem("wd_basic_2", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WD_BASIC_3 = registerWithItem("wd_basic_3", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WD_BASIC_4 = registerWithItem("wd_basic_4", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

    public static final DeferredBlock<Block> WD_STAIRS = registerWithItem("wd_stairs", () -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_STAIRS_2 = registerWithItem("wd_stairs_2", () -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_STAIRS_3 = registerWithItem("wd_stairs_3", () -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_STAIRS_4 = registerWithItem("wd_stairs_4", () -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));

    public static final DeferredBlock<Block> WD_SLAB = registerWithItem("wd_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_SLAB_2 = registerWithItem("wd_slab_2", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_SLAB_3 = registerWithItem("wd_slab_3", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_SLAB_4 = registerWithItem("wd_slab_4", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));

    public static final DeferredBlock<Block> WD_WALL = registerWithItem("wd_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final DeferredBlock<Block> WD_WALL_2 = registerWithItem("wd_wall_2", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final DeferredBlock<Block> WD_WALL_3 = registerWithItem("wd_wall_3", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final DeferredBlock<Block> WD_WALL_4 = registerWithItem("wd_wall_4", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));

    public static final DeferredBlock<Block> WD_LIGHT = registerWithItem("wd_light", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_LIGHT_2 = registerWithItem("wd_light_2", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_LIGHT_3 = registerWithItem("wd_light_3", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_LIGHT_4 = registerWithItem("wd_light_4", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));

    public static final DeferredBlock<Block> WD_HANGING_LIGHT = registerWithItem("wd_hanging_light", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_2 = registerWithItem("wd_hanging_light_2", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_3 = registerWithItem("wd_hanging_light_3", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_4 = registerWithItem("wd_hanging_light_4", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F)));

    public static final DeferredBlock<Block> WD_SECRET = registerWithItem("wd_secret", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).noOcclusion()));
    public static final DeferredBlock<Block> WD_LOCKABLE = registerWithItem("wd_lockable", () -> new LockableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noOcclusion().noLootTable()));

    public static final DeferredBlock<Block> EMERALD_PILE = registerWithItem("emerald_pile", () -> new EmeraldPileBlock(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(1.5F, 6.0F).noOcclusion().instabreak().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> SPIDER_EGG = registerWithItem("spider_egg", () -> new SpiderEggSacBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).noCollission().strength(1.5F, 6.0F).noOcclusion().instabreak().sound(SoundType.COBWEB)));

    public static final DeferredBlock<Block> IRON_GRATE = registerWithItem("iron_grate", () -> new WaterloggedTransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_GRATE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)));

    public static final DeferredBlock<Block> PRISMARINE_TILE = registerWithItem("prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PRISMARINE_TILE_STAIRS = registerWithItem("prismarine_tile_stairs", () -> new StairBlock(PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> PRISMARINE_TILE_SLAB = registerWithItem("prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE = registerWithItem("prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("prismarine_small_tile_stairs", () -> new StairBlock(PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE_SLAB = registerWithItem("prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> YELLOW_PRISMARINE_TILE = registerWithItem("yellow_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> YELLOW_PRISMARINE_TILE_STAIRS = registerWithItem("yellow_prismarine_tile_stairs", () -> new StairBlock(YELLOW_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(YELLOW_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> YELLOW_PRISMARINE_TILE_SLAB = registerWithItem("yellow_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(YELLOW_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> YELLOW_PRISMARINE_SMALL_TILE = registerWithItem("yellow_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> YELLOW_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("yellow_prismarine_small_tile_stairs", () -> new StairBlock(YELLOW_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(YELLOW_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> YELLOW_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("yellow_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(YELLOW_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> ORANGE_PRISMARINE_TILE = registerWithItem("orange_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> ORANGE_PRISMARINE_TILE_STAIRS = registerWithItem("orange_prismarine_tile_stairs", () -> new StairBlock(ORANGE_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(ORANGE_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> ORANGE_PRISMARINE_TILE_SLAB = registerWithItem("orange_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ORANGE_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> ORANGE_PRISMARINE_SMALL_TILE = registerWithItem("orange_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> ORANGE_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("orange_prismarine_small_tile_stairs", () -> new StairBlock(ORANGE_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(ORANGE_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> ORANGE_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("orange_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ORANGE_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> BROWN_PRISMARINE_TILE = registerWithItem("brown_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BROWN_PRISMARINE_TILE_STAIRS = registerWithItem("brown_prismarine_tile_stairs", () -> new StairBlock(BROWN_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BROWN_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> BROWN_PRISMARINE_TILE_SLAB = registerWithItem("brown_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BROWN_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> BROWN_PRISMARINE_SMALL_TILE = registerWithItem("brown_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BROWN_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("brown_prismarine_small_tile_stairs", () -> new StairBlock(BROWN_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BROWN_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> BROWN_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("brown_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BROWN_PRISMARINE_SMALL_TILE.get())));


    public static final DeferredBlock<Block> RED_PRISMARINE_TILE = registerWithItem("red_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> RED_PRISMARINE_TILE_STAIRS = registerWithItem("red_prismarine_tile_stairs", () -> new StairBlock(RED_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(RED_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> RED_PRISMARINE_TILE_SLAB = registerWithItem("red_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(RED_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> RED_PRISMARINE_SMALL_TILE = registerWithItem("red_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> RED_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("red_prismarine_small_tile_stairs", () -> new StairBlock(RED_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(RED_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> RED_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("red_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(RED_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> GREEN_PRISMARINE_TILE = registerWithItem("green_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> GREEN_PRISMARINE_TILE_STAIRS = registerWithItem("green_prismarine_tile_stairs", () -> new StairBlock(GREEN_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GREEN_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> GREEN_PRISMARINE_TILE_SLAB = registerWithItem("green_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(GREEN_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> GREEN_PRISMARINE_SMALL_TILE = registerWithItem("green_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> GREEN_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("green_prismarine_small_tile_stairs", () -> new StairBlock(GREEN_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GREEN_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> GREEN_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("green_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(GREEN_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> LIME_PRISMARINE_TILE = registerWithItem("lime_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> LIME_PRISMARINE_TILE_STAIRS = registerWithItem("lime_prismarine_tile_stairs", () -> new StairBlock(LIME_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LIME_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> LIME_PRISMARINE_TILE_SLAB = registerWithItem("lime_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LIME_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> LIME_PRISMARINE_SMALL_TILE = registerWithItem("lime_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> LIME_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("lime_prismarine_small_tile_stairs", () -> new StairBlock(LIME_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LIME_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> LIME_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("lime_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LIME_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> PURPLE_PRISMARINE_TILE = registerWithItem("purple_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PURPLE_PRISMARINE_TILE_STAIRS = registerWithItem("purple_prismarine_tile_stairs", () -> new StairBlock(PURPLE_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PURPLE_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> PURPLE_PRISMARINE_TILE_SLAB = registerWithItem("purple_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PURPLE_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> PURPLE_PRISMARINE_SMALL_TILE = registerWithItem("purple_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PURPLE_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("purple_prismarine_small_tile_stairs", () -> new StairBlock(PURPLE_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PURPLE_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> PURPLE_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("purple_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PURPLE_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> PINK_PRISMARINE_TILE = registerWithItem("pink_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PINK_PRISMARINE_TILE_STAIRS = registerWithItem("pink_prismarine_tile_stairs", () -> new StairBlock(PINK_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PINK_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> PINK_PRISMARINE_TILE_SLAB = registerWithItem("pink_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PINK_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> PINK_PRISMARINE_SMALL_TILE = registerWithItem("pink_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> PINK_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("pink_prismarine_small_tile_stairs", () -> new StairBlock(PINK_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PINK_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> PINK_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("pink_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PINK_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_TILE = registerWithItem("magenta_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_TILE_STAIRS = registerWithItem("magenta_prismarine_tile_stairs", () -> new StairBlock(MAGENTA_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_TILE_SLAB = registerWithItem("magenta_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_SMALL_TILE = registerWithItem("magenta_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("magenta_prismarine_small_tile_stairs", () -> new StairBlock(MAGENTA_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> MAGENTA_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("magenta_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> BLUE_PRISMARINE_TILE = registerWithItem("blue_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BLUE_PRISMARINE_TILE_STAIRS = registerWithItem("blue_prismarine_tile_stairs", () -> new StairBlock(BLUE_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> BLUE_PRISMARINE_TILE_SLAB = registerWithItem("blue_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BLUE_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> BLUE_PRISMARINE_SMALL_TILE = registerWithItem("blue_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BLUE_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("blue_prismarine_small_tile_stairs", () -> new StairBlock(BLUE_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MAGENTA_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> BLUE_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("blue_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BLUE_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> WHITE_PRISMARINE_TILE = registerWithItem("white_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WHITE_PRISMARINE_TILE_STAIRS = registerWithItem("white_prismarine_tile_stairs", () -> new StairBlock(WHITE_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WHITE_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> WHITE_PRISMARINE_TILE_SLAB = registerWithItem("white_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WHITE_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> WHITE_PRISMARINE_SMALL_TILE = registerWithItem("white_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WHITE_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("white_prismarine_small_tile_stairs", () -> new StairBlock(WHITE_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WHITE_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> WHITE_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("white_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WHITE_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_TILE = registerWithItem("light_gray_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_TILE_STAIRS = registerWithItem("light_gray_prismarine_tile_stairs", () -> new StairBlock(LIGHT_GRAY_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_TILE_SLAB = registerWithItem("light_gray_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_SMALL_TILE = registerWithItem("light_gray_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("light_gray_prismarine_small_tile_stairs", () -> new StairBlock(LIGHT_GRAY_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> LIGHT_GRAY_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("light_gray_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LIGHT_GRAY_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> GRAY_PRISMARINE_TILE = registerWithItem("gray_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> GRAY_PRISMARINE_TILE_STAIRS = registerWithItem("gray_prismarine_tile_stairs", () -> new StairBlock(GRAY_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GRAY_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> GRAY_PRISMARINE_TILE_SLAB = registerWithItem("gray_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(GRAY_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> GRAY_PRISMARINE_SMALL_TILE = registerWithItem("gray_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> GRAY_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("gray_prismarine_small_tile_stairs", () -> new StairBlock(GRAY_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(GRAY_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> GRAY_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("gray_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(GRAY_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_TILE = registerWithItem("dark_gray_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_TILE_STAIRS = registerWithItem("dark_gray_prismarine_tile_stairs", () -> new StairBlock(DARK_GRAY_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DARK_GRAY_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_TILE_SLAB = registerWithItem("dark_gray_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DARK_GRAY_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_SMALL_TILE = registerWithItem("dark_gray_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("dark_gray_prismarine_small_tile_stairs", () -> new StairBlock(DARK_GRAY_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DARK_GRAY_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> DARK_GRAY_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("dark_gray_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DARK_GRAY_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> BLACK_PRISMARINE_TILE = registerWithItem("black_prismarine_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BLACK_PRISMARINE_TILE_STAIRS = registerWithItem("black_prismarine_tile_stairs", () -> new StairBlock(BLACK_PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BLACK_PRISMARINE_TILE.get())));
    public static final DeferredBlock<Block> BLACK_PRISMARINE_TILE_SLAB = registerWithItem("black_prismarine_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BLACK_PRISMARINE_TILE.get())));

    public static final DeferredBlock<Block> BLACK_PRISMARINE_SMALL_TILE = registerWithItem("black_prismarine_small_tile", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> BLACK_PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("black_prismarine_small_tile_stairs", () -> new StairBlock(BLACK_PRISMARINE_SMALL_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BLACK_PRISMARINE_SMALL_TILE.get())));
    public static final DeferredBlock<Block> BLACK_PRISMARINE_SMALL_TILE_SLAB = registerWithItem("black_prismarine_small_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BLACK_PRISMARINE_SMALL_TILE.get())));

    public static final DeferredBlock<Block> DETONITE_ORE = registerWithItem("detonite_ore", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.9F, 6.0F).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DETONITE_BLOCK = registerWithItem("detonite_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.9F, 6.0F).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DETONITE_CRYSTAL_BLOCK = registerWithItem("detonite_crystal_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> SMALL_DETONITE_BUD = registerWithItem("small_detonite_bud", () -> new DetoniteClusterBlock(3f,4f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> MEDIUM_DETONITE_BUD = registerWithItem("medium_detonite_bud", () -> new DetoniteClusterBlock(4f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> LARGE_DETONITE_BUD = registerWithItem("large_detonite_bud", () -> new DetoniteClusterBlock(5f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> DETONITE_CLUSTER = registerWithItem("detonite_cluster", () -> new DetoniteClusterBlock(7f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> PUTRID_EGG = registerWithItem("putrid_egg", () -> new PutridEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).requiresCorrectToolForDrops().strength(3.5F, 0.2f).sound(SoundType.MUD)));

    public static final DeferredBlock<Block> DENSE_TNT = registerWithItem("dense_tnt", () -> new DenseTNTBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).instabreak().sound(SoundType.GRASS).ignitedByLava()));


    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }

}
