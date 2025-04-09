package com.danielkkrafft.wilddungeons.mixin;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement.legacy;

@Mixin(PlainVillagePools.class)
public class PlainVillagePoolsMixin {

    @ModifyArgs(
            method = "bootstrap",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/data/worldgen/Pools;register(Lnet/minecraft/data/worldgen/BootstrapContext;Ljava/lang/String;Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;)V"
            )
    )
    private static void modifyWellBottomsPool(Args args) {
        String poolName = args.get(1);

        // Check if this is the well_bottoms pool
        if ("village/common/well_bottoms".equals(poolName)) {
            StructureTemplatePool originalPool = args.get(2);
            WildDungeons.getLogger().debug("Modifying well_bottoms pool");
            // Create a new pool with additional entries
            StructureTemplatePool newPool = new StructureTemplatePool(
                    originalPool.getFallback(),
                    ImmutableList.of(
//                            Pair.of(StructurePoolElement.legacy("village/common/well_bottom"), 3),
                            Pair.of(legacy("wilddungeons:village/well_bottom"), 1)
                    ),
                    StructureTemplatePool.Projection.RIGID
            );
            // Replace the original pool with our modified one
            args.set(2, newPool);
        }
    }

}