package com.danielkkrafft.wilddungeons.world.structure;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDStructureTypes;
import com.danielkkrafft.wilddungeons.world.structure.piece.RiftStructurePiece;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class RiftStructure extends Structure {
    private static final String STRUCTURE_LOCATION = "rift";
    public static final MapCodec<RiftStructure> CODEC = simpleCodec(RiftStructure::new);

    protected RiftStructure(StructureSettings p_226558_) {
        super(p_226558_);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        WorldgenRandom worldgenrandom = context.random();
        int x = context.chunkPos().getMinBlockX() + worldgenrandom.nextInt(16);
        int z = context.chunkPos().getMinBlockZ() + worldgenrandom.nextInt(16);
        int y = findSuitableY(worldgenrandom, context.chunkGenerator(), x, z, context.heightAccessor(), context.randomState());
        if (y == -999) return Optional.empty();
        BlockPos templatePosition = new BlockPos(x, y, z);

        return Optional.of(new GenerationStub(templatePosition, (p_229297_) -> {
            p_229297_.addPiece(new RiftStructurePiece(context.structureTemplateManager(), WildDungeons.rl(STRUCTURE_LOCATION), templatePosition));
        }));
    }

    private static int findSuitableY(WorldgenRandom random, ChunkGenerator chunkGenerator, int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int randomHeight = random.nextIntBetweenInclusive(36, heightAccessor.getMaxBuildHeight());
        int baseHeight = heightAccessor.getMinBuildHeight() + 16;

        NoiseColumn noiseColumn = chunkGenerator.getBaseColumn(x, z, heightAccessor, randomState);

        int y;
        boolean foundAir = false;
        for (y = randomHeight; y > baseHeight; y--) {
            BlockState blockState = noiseColumn.getBlock(y);
            if (blockState.isAir()) {
                foundAir = true;
            } else if (foundAir && !blockState.isAir()) {
                return y+1;
            }
        }

        return -999;
    }

    @Override
    public StructureType<?> type() {
        return WDStructureTypes.RIFT.get();
    }
}
