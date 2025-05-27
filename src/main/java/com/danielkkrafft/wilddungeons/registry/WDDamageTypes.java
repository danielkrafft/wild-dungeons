package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDDamageTypes {

    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, WildDungeons.MODID);


    public static final DeferredHolder<DamageType, DamageType> BLACKHOLE =
            DAMAGE_TYPES.register("blackhole", () -> new DamageType("blackhole", 0.1F));
}
