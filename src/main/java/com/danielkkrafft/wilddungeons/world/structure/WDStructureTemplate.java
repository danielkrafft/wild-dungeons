package com.danielkkrafft.wilddungeons.world.structure;

import com.danielkkrafft.wilddungeons.dungeon.components.DungeonMaterial;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WDStructureTemplate {
    public ListTag dungeonMaterials = new ListTag();
    public List<Pair<StructureTemplate, BlockPos>> innerTemplates = new ArrayList<>();

    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag innerTagList = new ListTag();
        for (Pair<StructureTemplate, BlockPos> innerTemplate : innerTemplates) {
            CompoundTag innerTag = new CompoundTag();
            innerTemplate.getFirst().save(innerTag);
            innerTag.put("originOffset", newIntegerList(innerTemplate.getSecond().getX(), innerTemplate.getSecond().getY(), innerTemplate.getSecond().getZ()));
            innerTagList.add(innerTag);
        }
        tag.put("inner_templates", innerTagList);
        tag.put("dungeon_materials", dungeonMaterials);

        return tag;
    }

    public void setDungeonMaterials(ListTag dungeonMaterials) {
        this.dungeonMaterials = dungeonMaterials;
    }

    public void load(@NotNull HolderGetter<Block> blockGetter, @NotNull CompoundTag tag) {
        if (tag.contains("inner_templates")) {
            ListTag innerTagList = tag.getListOrEmpty("inner_templates");
            for (int i = 0; i < innerTagList.size(); i++) {
                CompoundTag innerTag = innerTagList.getCompoundOrEmpty(i);
                StructureTemplate innerTemplate = new StructureTemplate();
                innerTemplate.load(blockGetter, innerTag);
                ListTag originOffset = innerTag.getListOrEmpty("originOffset");
                Pair<StructureTemplate, BlockPos> innerTemplatePair = Pair.of(innerTemplate, new BlockPos(originOffset.getIntOr(0, 0), originOffset.getIntOr(1, 0), originOffset.getIntOr(2, 0)));
                innerTemplates.add(innerTemplatePair);
            }
        }
        if (tag.contains("dungeon_materials")) {
            dungeonMaterials = tag.getListOrEmpty("dungeon_materials");
        }
    }

    public List<DungeonMaterial.BlockSetting> getDungeonMaterialsAsList() {
        List<DungeonMaterial.BlockSetting> loadedMaterials = new ArrayList<>();
        dungeonMaterials.forEach(tag -> {
            CompoundTag compoundTag = (CompoundTag) tag;
            BlockState blockState = readBlockState(WDStructureTemplateManager.INSTANCE.getBlockLookup(), compoundTag);
            int dungeonMaterialId = compoundTag.getIntOr("dungeon_material_id", 0);
            DungeonMaterial.BlockSetting blockSetting = new DungeonMaterial.BlockSetting(blockState, dungeonMaterialId);
            if (compoundTag.contains("blockType")){
                blockSetting.setBlockType(DungeonMaterial.BlockSetting.BlockType.values()[compoundTag.getIntOr("blockType", 0)]);
            }
            loadedMaterials.add(blockSetting);
        });
        return loadedMaterials;
    }

    public static BlockState readBlockState(HolderGetter<Block> blockGetter, CompoundTag tag) {
        if (!tag.contains("Name")) {
            return Blocks.AIR.defaultBlockState();
        } else {
            Identifier resourceLocation = Identifier.parse(tag.getStringOr("Name", ""));
            Optional<? extends Holder<Block>> optional = blockGetter.get(ResourceKey.create(Registries.BLOCK, resourceLocation));
            if (optional.isEmpty()) {
                return Blocks.AIR.defaultBlockState();
            } else {
                Block block = optional.get().value();
                return block.defaultBlockState();
            }
        }
    }

    public ListTag newIntegerList(int... values) {
        ListTag listtag = new ListTag();

        for (int i : values) {
            listtag.add(IntTag.valueOf(i));
        }

        return listtag;
    }
}