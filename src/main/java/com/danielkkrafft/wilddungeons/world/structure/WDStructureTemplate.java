package com.danielkkrafft.wilddungeons.world.structure;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class WDStructureTemplate extends StructureTemplate {
    public ListTag dungeonMaterials = new ListTag();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        super.save(tag);
        tag.put("dungeon_materials", dungeonMaterials);
        return tag;
    }

    public void setDungeonMaterials(ListTag dungeonMaterials) {
        this.dungeonMaterials = dungeonMaterials;
    }

    @Override
    public void load(@NotNull HolderGetter<Block> blockGetter, @NotNull CompoundTag tag) {
        if (tag.contains("dungeon_materials")) {
            dungeonMaterials = tag.getList("dungeon_materials", 10);
        }
        super.load(blockGetter, tag);
    }
}
