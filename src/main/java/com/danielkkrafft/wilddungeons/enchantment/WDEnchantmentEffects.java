package com.danielkkrafft.wilddungeons.enchantment;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.enchantment.custom.DensityEnchantmentEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WDEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, WildDungeons.MODID);


    public static final Holder<MapCodec<? extends EnchantmentEntityEffect>> DENSITY =
            ENTITY_ENCHANTMENT_EFFECTS.register("density_effect",
                    ()-> DensityEnchantmentEffect.CODEC);
}