package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import java.nio.file.Path;
import java.util.Set;

public class WDDimensions {

    public static final Set<ResourceKey<Level>> VANILLA_LEVELS = Set.of(Level.OVERWORLD, Level.NETHER, Level.END);

    public static Path getRegionFolder(String dimensionName) {
        return FileUtil.getWorldPath().resolve("dimensions").resolve(WildDungeons.MODID).resolve(dimensionName);
    }

    public static LevelStem createLevel(MinecraftServer server) {
        Holder<DimensionType> typeHolder = server.overworld().dimensionTypeRegistration();
        return new LevelStem(typeHolder, new EmptyGenerator(new FixedBiomeSource(server.overworld().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.THE_VOID))));
    }
}
