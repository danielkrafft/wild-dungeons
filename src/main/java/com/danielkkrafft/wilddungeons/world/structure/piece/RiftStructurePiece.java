package com.danielkkrafft.wilddungeons.world.structure.piece;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.DungeonRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RiftStructurePiece extends TemplateStructurePiece {
    public RiftStructurePiece(StructureTemplateManager structureTemplateManager, ResourceLocation location, BlockPos templatePosition) {
        super(WDStructurePieceTypes.RIFT.value(), 0, structureTemplateManager, location, location.toString(), new StructurePlaceSettings(), templatePosition);
        WildDungeons.getLogger().info("PLACED RIFT STRUCTURE AT {}", templatePosition);
    }

    public RiftStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(WDStructurePieceTypes.RIFT.value(), tag, context.structureTemplateManager(), resourceLocation -> makeSettings());
    }

    @Override
    protected void handleDataMarker(String s, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource randomSource, BoundingBox boundingBox) {
        if (s.equals("rift")) {
            serverLevelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
            Holder<Biome> biome = serverLevelAccessor.getBiome(blockPos);
            Offering offering = null;
            if (biome.is(BiomeTags.IS_OVERWORLD)) {
                offering = DungeonRegistry.OVERWORLD_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            } else if (biome.is(BiomeTags.IS_NETHER)) {
                offering = DungeonRegistry.NETHER_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            } else if (biome.is(BiomeTags.IS_END)) {
                offering = DungeonRegistry.END_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            }
            if (offering == null) return;

            offering.setPos(Vec3.atCenterOf(blockPos.above()));
            serverLevelAccessor.addFreshEntity(offering);
        }
    }

    static StructurePlaceSettings makeSettings() {
        return new StructurePlaceSettings();
    }


}
