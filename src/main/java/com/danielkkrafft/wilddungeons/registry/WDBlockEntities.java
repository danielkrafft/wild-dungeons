package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.blockentity.ConnectionBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, WildDungeons.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConnectionBlockEntity>> CONNECTION_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "connection_block_entity",
            () -> BlockEntityType.Builder.of(ConnectionBlockEntity::new, WDBlocks.CONNECTION_BLOCK.get()).build(null));
}
