package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.world.structure.RiftStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, WildDungeons.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<RiftStructure>> RIFT = STRUCTURE_TYPES.register("rift", () -> () -> RiftStructure.CODEC);
}
