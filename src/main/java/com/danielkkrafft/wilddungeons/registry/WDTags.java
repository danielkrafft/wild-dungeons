package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class WDTags {

    public static class Fluids {
        public static final TagKey<Fluid> LIFE_LIQUID = create(WildDungeons.rl("fluid"));

        public static TagKey<Fluid> create(final ResourceLocation name) {
            return TagKey.create(Registries.FLUID, name);
        }
    }
}
