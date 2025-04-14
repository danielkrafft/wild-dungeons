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
    public static final DeferredBlock<Block> WD_BEDROCK = registerWithItem("wd_bedrock", () -> new WDBedrockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F)));
    public static final DeferredBlock<Block> WD_LOCKABLE = registerWithItem("wd_lockable", () -> new LockableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noOcclusion().noLootTable()));

    public static final DeferredBlock<Block> EMERALD_PILE = BLOCKS.register("emerald_pile", () -> new EmeraldPileBlock(BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).strength(1.5F, 6.0F).noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }

}
