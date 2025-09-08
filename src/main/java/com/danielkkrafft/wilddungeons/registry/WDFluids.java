package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.LifeLiquid;
import com.danielkkrafft.wilddungeons.block.ToxicSludge;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class WDFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, WildDungeons.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, WildDungeons.MODID);

    public static Holder<FluidType> LIFE_LIQUID_TYPE = registerFluidType(() -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.wilddungeons.life_liquid")
            .fallDistanceModifier(0f)
            .canExtinguish(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .density(100)
            .pathType(PathType.WATER)
            .viscosity(100)), "life_liquid");
    public static DeferredHolder<Fluid, LifeLiquid.Flowing> FLOWING_LIFE_LIQUID = FLUIDS.register("flowing_life_liquid", LifeLiquid.Flowing::new);
    public static DeferredHolder<Fluid, LifeLiquid.Source> LIFE_LIQUID = FLUIDS.register("life_liquid", LifeLiquid.Source::new);

    public static Holder<FluidType> TOXIC_SLUDGE_TYPE = registerFluidType(() -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.wilddungeons.toxic_sludge")
            .fallDistanceModifier(0f)
            .canExtinguish(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .density(100)
            .pathType(PathType.WATER)
            .viscosity(3000)), "toxic_sludge");
    public static DeferredHolder<Fluid, ToxicSludge.Flowing> FLOWING_TOXIC_SLUDGE = FLUIDS.register("flowing_toxic_sludge", ToxicSludge.Flowing::new);
    public static DeferredHolder<Fluid, ToxicSludge.Source> TOXIC_SLUDGE = FLUIDS.register("toxic_sludge", ToxicSludge.Source::new);

    public static DeferredHolder<FluidType, FluidType> registerFluidType(Supplier<FluidType> fluidSupplier, String name)
    {
        return FLUID_TYPES.register(name, fluidSupplier);
    }


}
