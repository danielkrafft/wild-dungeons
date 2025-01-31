package com.danielkkrafft.wilddungeons.registry;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSessionManager;
import com.danielkkrafft.wilddungeons.util.FileUtil;
import com.danielkkrafft.wilddungeons.world.dimension.EmptyGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import java.nio.file.Path;

public class WDDimensions {


    public static Path getRegionFolder(String dimensionName) {
        return FileUtil.getWorldPath().resolve("dimensions").resolve(WildDungeons.MODID).resolve(dimensionName);
    }

    public static final ResourceKey<DimensionType> WILDDUNGEON = register("wilddungeons");
    private static ResourceKey<DimensionType> register(String name) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(WildDungeons.MODID, name));
    }

    public static LevelStem createLevel(ResourceKey<DimensionType> dimensionType) {
        MinecraftServer server = DungeonSessionManager.getInstance().server;
        Holder<DimensionType> typeHolder = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(dimensionType);
        return new LevelStem(typeHolder, new EmptyGenerator(new FixedBiomeSource(server.overworld().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.THE_VOID))));
    }
}
