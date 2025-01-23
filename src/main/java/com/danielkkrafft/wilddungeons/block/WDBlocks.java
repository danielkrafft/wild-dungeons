package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

public class WDBlocks {



    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildDungeons.MODID);

    public static final DeferredBlock<Block> CONNECTION_BLOCK = registerWithItem("connection_block", () -> new ConnectionBlock(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> SPAWN_BLOCK = registerWithItem("spawn_block", () -> new Block(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> LIFE_LIQUID = BLOCKS.register("life_liquid", () -> new LifeLiquidBlock(WDFluids.LIFE_LIQUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredBlock<Block> ROTTEN_MOSS = registerWithItem("rotten_moss", RottenMossBlock::new);
    public static final DeferredBlock<Block> HEAVY_RUNE = registerWithItem("heavy_rune", HeavyRuneBlock::new);

    public static final DeferredBlock<Block> WD_BASIC = registerWithItem("wd_basic", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final DeferredBlock<Block> WD_STAIRS = registerWithItem("wd_stairs", () -> new StairBlock(WD_BASIC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_SLAB = registerWithItem("wd_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(WD_BASIC.get())));
    public static final DeferredBlock<Block> WD_WALL = registerWithItem("wd_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).forceSolidOn()));
    public static final DeferredBlock<Block> WD_LIGHT = registerWithItem("wd_light", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).lightLevel(state -> 15).requiresCorrectToolForDrops().strength(0.3F).noOcclusion()));
    public static final DeferredBlock<Block> WD_SECRET = registerWithItem("wd_secret", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).noOcclusion()));


    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        WDItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, WildDungeons.MODID);
    public static final Supplier<CreativeModeTab> WD_TAB = CREATIVE_MODE_TABS.register("wilddungeons_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + WildDungeons.MODID))
            .icon(() -> new ItemStack(WDBlocks.CONNECTION_BLOCK.get()))
            .displayItems((params, output) -> {
                output.accept(WDBlocks.CONNECTION_BLOCK.get());
                output.accept(WDBlocks.SPAWN_BLOCK.get());
                output.accept(WDBlocks.ROTTEN_MOSS.get());
                output.accept(WDBlocks.HEAVY_RUNE.get());
                output.accept(WDBlocks.WD_BASIC.get());
                output.accept(WDBlocks.WD_STAIRS.get());
                output.accept(WDBlocks.WD_SLAB.get());
                output.accept(WDBlocks.WD_WALL.get());
                output.accept(WDBlocks.WD_LIGHT.get());
                output.accept(WDBlocks.WD_SECRET.get());
                output.accept(WDItems.OFFERING_ITEM.get());
                output.accept(WDItems.RIFT_ITEM.get());
            })
            .build()
    );

}
