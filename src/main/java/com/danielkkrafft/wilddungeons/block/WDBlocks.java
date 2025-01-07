package com.danielkkrafft.wilddungeons.block;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WDBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildDungeons.MODID);

    public static final DeferredBlock<Block> RIFT_BLOCK = registerWithItem("rift_block", () -> new RiftBlock(BlockBehaviour.Properties.of().destroyTime(-1).lightLevel(state -> 15).noCollission().noOcclusion()));
    public static final DeferredBlock<Block> CONNECTION_BLOCK = registerWithItem("connection_block", () -> new ConnectionBlock(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> SPAWN_BLOCK = registerWithItem("spawn_block", () -> new Block(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));
    public static final DeferredBlock<Block> LIFE_LIQUID = BLOCKS.register("life_liquid", () -> new LifeLiquidBlock(WDFluids.LIFE_LIQUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredBlock<Block> ROTTEN_MOSS = registerWithItem("rotten_moss", RottenMossBlock::new);
    public static final DeferredBlock<Block> HEAVY_RUNE = registerWithItem("heavy_rune", HeavyRuneBlock::new);

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        WDItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }
}
