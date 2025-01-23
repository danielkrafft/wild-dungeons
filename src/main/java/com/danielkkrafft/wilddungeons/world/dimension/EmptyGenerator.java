package com.danielkkrafft.wilddungeons.world.dimension;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmptyGenerator extends ChunkGenerator {

    public static final int MIN_Y = -64;
    public static final int GEN_DEPTH = 320;

    private final BiomeSource biomeSource;
    public static final MapCodec<EmptyGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(EmptyGenerator::getBiomeSource)).apply(instance, EmptyGenerator::new)
    );

    public EmptyGenerator(BiomeSource biome_source) {

        super(biome_source);
        this.biomeSource = biome_source;

    }

    @Override
    protected MapCodec<EmptyGenerator> codec() {
        return CODEC;
    }

    public BiomeSource getBiomeSource() {

        return this.biomeSource;

    }

    @Override
    public void applyCarvers(WorldGenRegion p_223043_, long p_223044_, RandomState p_223045_, BiomeManager p_223046_, StructureManager p_223047_, ChunkAccess p_223048_, GenerationStep.Carving p_223049_) {

    }

    @Override
    public void buildSurface(WorldGenRegion p_223050_, StructureManager p_223051_, RandomState p_223052_, ChunkAccess chunkAccess) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion p_62167_) {

    }

    @Override
    public int getGenDepth() {

        return GEN_DEPTH;

    }

    //Idea there should be some other type of fill probably maybe an unbreakable shell
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {

        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockState blockstate = Blocks.AIR.defaultBlockState();

        for (int i = 0; i < chunk.getHeight(); i++) {
            int j = chunk.getMinBuildHeight() + i;
            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    chunk.setBlockState(blockpos$mutableblockpos.set(k, j, l), blockstate, false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {

        return 0;

    }

    @Override
    public int getMinY() {

        return MIN_Y;

    }

    @Override
    public int getBaseHeight(int p_223032_, int p_223033_, Heightmap.Types p_223034_, LevelHeightAccessor p_223035_, RandomState p_223036_) {

        return 0;

    }

    @Override
    public NoiseColumn getBaseColumn(int p_223028_, int p_223029_, LevelHeightAccessor level, RandomState p_223031_) {

        BlockState[] blockStates = new BlockState[level.getHeight()];
        Arrays.fill(blockStates, Blocks.AIR.defaultBlockState());

        return new NoiseColumn(-64, blockStates);

    }

    @Override
    public void addDebugScreenInfo(List<String> p_223175_, RandomState p_223176_, BlockPos p_223177_) {

    }

}