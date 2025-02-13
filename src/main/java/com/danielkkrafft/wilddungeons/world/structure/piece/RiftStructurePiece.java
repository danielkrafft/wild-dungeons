package com.danielkkrafft.wilddungeons.world.structure.piece;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonTemplate;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.dungeon.registries.DungeonRegistry;
import com.danielkkrafft.wilddungeons.entity.Offering;
import com.danielkkrafft.wilddungeons.registry.WDStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;

import static com.danielkkrafft.wilddungeons.dungeon.registries.RiftPoolRegistry.*;

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
                offering = OVERWORLD_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            } else if (biome.is(BiomeTags.IS_NETHER)) {
                offering = NETHER_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            } else if (biome.is(BiomeTags.IS_END)) {
                offering = END_RIFT_POOL.getRandom().asOffering(serverLevelAccessor.getLevel());
            }
            if (offering == null) return;

            DungeonTemplate template = DungeonRegistry.DUNGEON_REGISTRY.get(offering.getOfferingId().split("wd-")[1]);
            offering.setPrimaryColor(template.get(HierarchicalProperty.PRIMARY_COLOR));
            offering.setSecondaryColor(template.get(HierarchicalProperty.SECONDARY_COLOR));
            offering.setPos(Vec3.atCenterOf(blockPos.above()));
            serverLevelAccessor.addFreshEntity(offering);
        }
    }

    static StructurePlaceSettings makeSettings() {
        return new StructurePlaceSettings();
    }


}
