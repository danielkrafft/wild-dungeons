package com.danielkkrafft.wilddungeons.world.dimension;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmptyGenerator extends ChunkGenerator {

    public static final int MIN_Y = 0;
    public static final int GEN_DEPTH = 640;

    private final BiomeSource biomeSource;
    private final BlockState[] blockStates;
    public static final Codec<BlockState[]> BLOCKSTATE_ARRAY_CODEC = Codec.list(BlockState.CODEC)
            .xmap(list -> list.toArray(new BlockState[0]), Arrays::asList);
    public static final MapCodec<EmptyGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(EmptyGenerator::getBiomeSource),
                    BLOCKSTATE_ARRAY_CODEC.fieldOf("blockstates").forGetter(EmptyGenerator::getBlockStates)
                    ).apply(instance, EmptyGenerator::new)
    );

    public EmptyGenerator(BiomeSource biomeSource, BlockState[] blockStates) { // could we pass a session id to get the dungeonFloor?
        super(biomeSource);
        this.biomeSource = biomeSource;
        if (blockStates != null) {
            this.blockStates = blockStates;
        } else {
            BlockState[] defaultBlockStates = new BlockState[EmptyGenerator.GEN_DEPTH];
            Arrays.fill(defaultBlockStates, Blocks.AIR.defaultBlockState());
            this.blockStates = defaultBlockStates;
        }

    }

    @Override
    protected MapCodec<EmptyGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long l, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess) {

    }

    public BiomeSource getBiomeSource() {return this.biomeSource;}
    public BlockState[] getBlockStates() {return this.blockStates;}

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

        for (int i = 0; i < chunk.getHeight(); i++) {
            int j = chunk.getMinY() + i;
            for (int k = 0; k < 16; k++) {
                for (int l = 0; l < 16; l++) {
                    chunk.setBlockState(blockpos$mutableblockpos.set(k, j, l), this.blockStates[i], i);
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
        return new NoiseColumn(MIN_Y, this.blockStates);
    }

    @Override
    public void addDebugScreenInfo(List<String> p_223175_, RandomState p_223176_, BlockPos p_223177_) {

    }

}