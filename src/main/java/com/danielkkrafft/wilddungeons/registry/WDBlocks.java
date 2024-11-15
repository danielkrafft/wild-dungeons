package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.ConnectionBlock;
import com.danielkkrafft.wilddungeons.block.RiftBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.function.Supplier;

public class WDBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildDungeons.MODID);

    public static final DeferredBlock<Block> RIFT_BLOCK = registerWithItem("rift_block", () -> new RiftBlock(BlockBehaviour.Properties.of().destroyTime(-1).lightLevel(state -> 15).noCollission().noOcclusion()));
    public static final DeferredBlock<Block> CONNECTION_BLOCK = registerWithItem("connection_block", () -> new ConnectionBlock(BlockBehaviour.Properties.of().destroyTime(-1).noCollission()));

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.@NotNull Properties properties) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        WDItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }
}
