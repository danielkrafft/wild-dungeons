package com.danielkkrafft.wilddungeons.world.structure;

import com.danielkkrafft.wilddungeons.WildDungeons;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WDStructureTemplate extends StructureTemplate {

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        super.save(tag);
        WildDungeons.getLogger().info("SAVING STRUCTURE TEMPLATE");
        ListTag listTag = new ListTag();

        List<BlockState> testBlockstates = new ArrayList<>();
        testBlockstates.add(Blocks.ANDESITE.defaultBlockState());
        testBlockstates.add(Blocks.BEDROCK.defaultBlockState());
        testBlockstates.add(Blocks.BIRCH_LOG.defaultBlockState());
        for(BlockState blockState : testBlockstates) {
            listTag.add(NbtUtils.writeBlockState(blockState));
        }

        tag.put("dungeonMaterialMap", listTag);;
        return tag;
    }

    @Override
    public void load(@NotNull HolderGetter<Block> blockGetter, @NotNull CompoundTag tag) {
        super.load(blockGetter, tag);
        ListTag listTag = tag.getList("dungeonMaterialMap", 10);
        List<BlockState> blockStates = new ArrayList<>();
        for(int i = 0; i < listTag.size(); i++) {
            blockStates.add(NbtUtils.readBlockState(blockGetter,listTag.getCompound(i)));
        }
        blockStates.forEach(blockState ->
                WildDungeons.getLogger().info("Blockstate: {}", blockState));
    }
}
