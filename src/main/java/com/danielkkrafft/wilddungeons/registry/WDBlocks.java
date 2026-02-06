package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.danielkkrafft.wilddungeons.registry.WDItems.ITEMS;

public class WDBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildDungeons.MODID);

    public static final ResourceKey<Block> LIFE_LIQUID_ID = createKey("life_liquid");

    public record PrismarineSet(
            DeferredBlock<Block> tile,
            DeferredBlock<Block> tileStairs,
            DeferredBlock<Block> tileSlab
    ) {}

    private static void registerPrismarineSet(String color, MapColor mapColor, boolean hasSmall) {
        String name = color + "_prismarine_tile";
        String smallName = color + "_prismarine_small_tile";
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of().mapColor(mapColor).requiresCorrectToolForDrops().strength(1.5F, 6.0F);

        DeferredBlock<Block> tile = registerWithItem(name, key -> new Block(props.setId(key)));
        DeferredBlock<Block> stairs = registerWithItem(name + "_stairs", key -> new StairBlock(tile.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(tile.get()).setId(key)));
        DeferredBlock<Block> slab = registerWithItem(name + "_slab", key -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(tile.get()).setId(key)));
        PRISMARINE_TILES.put(name, new PrismarineSet(tile, stairs, slab));

        if (hasSmall) {
            DeferredBlock<Block> smallTile = registerWithItem(smallName, key -> new Block(props.setId(key)));
            DeferredBlock<Block> smallStairs = registerWithItem(smallName + "_stairs", key -> new StairBlock(smallTile.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(smallTile.get()).setId(key)));
            DeferredBlock<Block> smallSlab = registerWithItem(smallName + "_slab", key -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(smallTile.get()).setId(key)));
            PRISMARINE_TILES.put(smallName, new PrismarineSet(smallTile, smallStairs, smallSlab));
        }
    }

    public static final Map<String, PrismarineSet> PRISMARINE_TILES = new HashMap<>();

    static {
        registerPrismarineSet("yellow", MapColor.COLOR_YELLOW, true);
        registerPrismarineSet("orange", MapColor.COLOR_ORANGE, true);
        registerPrismarineSet("brown", MapColor.COLOR_BROWN, true);
        registerPrismarineSet("red", MapColor.COLOR_RED, true);
        registerPrismarineSet("green", MapColor.COLOR_GREEN, true);
        registerPrismarineSet("lime", MapColor.COLOR_LIGHT_GREEN, true);
        registerPrismarineSet("purple", MapColor.COLOR_PURPLE, true);
        registerPrismarineSet("pink", MapColor.COLOR_PINK, true);
        registerPrismarineSet("magenta", MapColor.COLOR_MAGENTA, true);
        registerPrismarineSet("blue", MapColor.COLOR_BLUE, true);
        registerPrismarineSet("white", MapColor.COLOR_LIGHT_GRAY, true);
        registerPrismarineSet("light_gray", MapColor.COLOR_LIGHT_GRAY, true);
        registerPrismarineSet("dark_gray", MapColor.COLOR_GRAY, true);
        registerPrismarineSet("black", MapColor.COLOR_BLACK, true);
    }

    public static final DeferredBlock<Block> CONNECTION_BLOCK = registerWithItem("connection_block", key -> new ConnectionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).setId(key)));
//    public static final DeferredBlock<Block> SPAWN_BLOCK = registerWithItem("spawn_block", () -> new Block(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> LIFE_LIQUID = BLOCKS.register("life_liquid", () -> new LifeLiquidBlock(WDFluids.LIFE_LIQUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).setId(LIFE_LIQUID_ID)));
//    public static final DeferredBlock<Block> TOXIC_SLUDGE = BLOCKS.register("toxic_sludge", () -> new ToxicSludgeBlock(WDFluids.TOXIC_SLUDGE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).liquid().mapColor(MapColor.COLOR_LIGHT_GREEN)));
//    public static final DeferredBlock<Block> TOXIC_GAS = registerWithItem("toxic_gas",() -> new ToxicGasBlock(BlockBehaviour.Properties.of().strength(0F).ignitedByLava().noCollission().noLootTable().isSuffocating((a, b, c) -> true)));
    public static final DeferredBlock<Block> ROTTEN_MOSS = registerWithItem("rotten_moss",  key -> new ConnectionBlock(BlockBehaviour.Properties.of().setId(key)));
    public static final DeferredBlock<Block> HEAVY_RUNE = registerWithItem("heavy_rune", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(55, 1200).setId(key)));

    public static final DeferredBlock<Block> WD_BASIC = registerWithItem("wd_basic", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_BASIC_2 = registerWithItem("wd_basic_2", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_BASIC_3 = registerWithItem("wd_basic_3", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_BASIC_4 = registerWithItem("wd_basic_4", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));

    public static final DeferredBlock<Block> WD_STAIRS = registerWithItem("wd_stairs", key -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_STAIRS_2 = registerWithItem("wd_stairs_2", key -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_STAIRS_3 = registerWithItem("wd_stairs_3", key -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_STAIRS_4 = registerWithItem("wd_stairs_4", key -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));

    public static final DeferredBlock<Block> WD_SLAB = registerWithItem("wd_slab", key -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_SLAB_2 = registerWithItem("wd_slab_2", key -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_SLAB_3 = registerWithItem("wd_slab_3", key -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> WD_SLAB_4 = registerWithItem("wd_slab_4", key -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));

    public static final DeferredBlock<Block> WD_WALL = registerWithItem("wd_wall", key -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn().setId(key)));
    public static final DeferredBlock<Block> WD_WALL_2 = registerWithItem("wd_wall_2", key -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn().setId(key)));
    public static final DeferredBlock<Block> WD_WALL_3 = registerWithItem("wd_wall_3", key -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn().setId(key)));
    public static final DeferredBlock<Block> WD_WALL_4 = registerWithItem("wd_wall_4", key -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn().setId(key)));

    public static final DeferredBlock<Block> WD_LIGHT = registerWithItem("wd_light", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_LIGHT_2 = registerWithItem("wd_light_2", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_LIGHT_3 = registerWithItem("wd_light_3", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_LIGHT_4 = registerWithItem("wd_light_4", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));

    public static final DeferredBlock<Block> WD_HANGING_LIGHT = registerWithItem("wd_hanging_light", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_2 = registerWithItem("wd_hanging_light_2", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_3 = registerWithItem("wd_hanging_light_3", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));
    public static final DeferredBlock<Block> WD_HANGING_LIGHT_4 = registerWithItem("wd_hanging_light_4", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).setId(key)));

    public static final DeferredBlock<Block> WD_SECRET = registerWithItem("wd_secret", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).noOcclusion().setId(key)));
//    public static final DeferredBlock<Block> WD_LOCKABLE = registerWithItem("wd_lockable", () -> new LockableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noOcclusion().noLootTable()));

    public static final DeferredBlock<Block> EMERALD_PILE = registerWithItem("emerald_pile", key -> new EmeraldPileBlock(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(1.5F, 6.0F).noOcclusion().instabreak().sound(SoundType.AMETHYST).setId(key)));
//    public static final DeferredBlock<Block> SPIDER_EGG = registerWithItem("spider_egg", () -> new SpiderEggSacBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).noCollission().strength(1.5F, 6.0F).noOcclusion().instabreak().sound(SoundType.COBWEB)));

    public static final DeferredBlock<Block> IRON_GRATE = registerWithItem("iron_grate", key -> new WaterloggedTransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_GRATE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).setId(key)));

    public static final DeferredBlock<Block> PRISMARINE_TILE = registerWithItem("prismarine_tile", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> PRISMARINE_TILE_STAIRS = registerWithItem("prismarine_tile_stairs", key -> new StairBlock(PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get()).setId(key)));
    public static final DeferredBlock<Block> PRISMARINE_TILE_SLAB = registerWithItem("prismarine_tile_slab", key -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get()).setId(key)));

    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE = registerWithItem("prismarine_small_tile", key -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).setId(key)));
    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE_STAIRS = registerWithItem("prismarine_small_tile_stairs", key -> new StairBlock(PRISMARINE_TILE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get()).setId(key)));
    public static final DeferredBlock<Block> PRISMARINE_SMALL_TILE_SLAB = registerWithItem("prismarine_small_tile_slab", key -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE_TILE.get()).setId(key)));

    public static final DeferredBlock<Block> DETONITE_ORE = registerWithItem("detonite_ore", key -> new DetoniteBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> DETONITE_BLOCK = registerWithItem("detonite_block", key -> new DetoniteBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> DETONITE_CRYSTAL_BLOCK = registerWithItem("detonite_crystal_block", key -> new DetoniteBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> SMALL_DETONITE_BUD = registerWithItem("small_detonite_bud", key -> new DetoniteClusterBlock(3f,4f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> MEDIUM_DETONITE_BUD = registerWithItem("medium_detonite_bud", key -> new DetoniteClusterBlock(4f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> LARGE_DETONITE_BUD = registerWithItem("large_detonite_bud", key -> new DetoniteClusterBlock(5f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().setId(key)));
    public static final DeferredBlock<Block> DETONITE_CLUSTER = registerWithItem("detonite_cluster", key -> new DetoniteClusterBlock(7f,3f,BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(0.3F, 6.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().setId(key)));

//    public static final DeferredBlock<Block> PUTRID_EGG = registerWithItem("putrid_egg", () -> new PutridEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).requiresCorrectToolForDrops().strength(3.5F, 0.2f).sound(SoundType.MUD)));

    public static final DeferredBlock<Block> DENSE_TNT = registerWithItem("dense_tnt", key -> new DenseTNTBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).instabreak().sound(SoundType.GRASS).ignitedByLava().setId(key)));

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<ResourceKey<Block>, T> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, WildDungeons.rl(name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, WildDungeons.rl(name));

        DeferredBlock<T> block = BLOCKS.register(name, () -> factory.apply(blockKey));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(itemKey)));
        return block;
    }

    private static ResourceKey<Block> createKey(String id) {
        return ResourceKey.create(Registries.BLOCK, WildDungeons.rl(id));
    }
}
