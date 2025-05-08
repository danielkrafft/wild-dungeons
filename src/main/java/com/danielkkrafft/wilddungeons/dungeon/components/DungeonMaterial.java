package com.danielkkrafft.wilddungeons.dungeon.components;


import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DungeonMaterial implements DungeonRegistration.DungeonComponent {
    public String name;
    public Map<BlockSetting.BlockType, ArrayList<WeightedPool<BlockState>>> blockStates = new HashMap<>();

    public DungeonMaterial(String name, WeightedPool<BlockState> defaultBasicBlocks, WeightedPool<BlockState> defaultStairBlocks, WeightedPool<BlockState> defaultSlabBlocks, WeightedPool<BlockState> defaultWallBlocks, WeightedPool<BlockState> defaultLightBlocks, WeightedPool<BlockState> defaultHangingLights, WeightedPool<BlockState> defaultHiddenBlocks) {
        this.name = name;
        this.blockStates.put(BlockSetting.BlockType.BASIC, new ArrayList<>(List.of(defaultBasicBlocks)));
        this.blockStates.put(BlockSetting.BlockType.STAIR, new ArrayList<>(List.of(defaultStairBlocks)));
        this.blockStates.put(BlockSetting.BlockType.SLAB, new ArrayList<>(List.of(defaultSlabBlocks)));
        this.blockStates.put(BlockSetting.BlockType.WALL, new ArrayList<>(List.of(defaultWallBlocks)));
        this.blockStates.put(BlockSetting.BlockType.LIGHT, new ArrayList<>(List.of(defaultLightBlocks)));
        this.blockStates.put(BlockSetting.BlockType.HANGING_LIGHT, new ArrayList<>(List.of(defaultHangingLights)));
        this.blockStates.put(BlockSetting.BlockType.HIDDEN, new ArrayList<>(List.of(defaultHiddenBlocks)));
    }

    public DungeonMaterial add(BlockSetting.BlockType category, WeightedPool<BlockState> blockStates) { this.blockStates.get(category).add(blockStates); return this; }
    public BlockState get(BlockSetting.BlockType category, int index, double noiseScale, BlockPos pos) { return index > blockStates.get(category).size()-1 ? blockStates.get(category).getFirst().getNoisyRandom(pos, noiseScale) : blockStates.get(category).get(index).getNoisyRandom(pos, noiseScale); }

    public BlockState replace(BlockState input, double noiseScale, BlockPos pos, WDStructureTemplate wdTemplate) {
        if (input.is(Blocks.AIR)) {return input;}

        BlockState result = input;
        if (wdTemplate != null) {
            List<BlockSetting> dungeonIndexMapping = wdTemplate.getDungeonMaterialsAsList();
            Optional<BlockSetting> blockSettingOptional = dungeonIndexMapping.stream().filter(blockSetting -> blockSetting.blockState.equals(input.getBlock().defaultBlockState())).findFirst();
            if (blockSettingOptional.isPresent()) {
                BlockSetting blockSetting = blockSettingOptional.get();
                if (blockSetting.blockType == BlockSetting.BlockType.NONE) return result;
                int materialIndex = blockSetting.materialIndex;
                result = get(blockSetting.blockType, materialIndex, noiseScale, pos);
            }
        }

        if (input.is(WDBlocks.WD_BASIC.get())) {result = get(BlockSetting.BlockType.BASIC, 0, noiseScale, pos);}//todo remove all these when we finish updating the old templates to the new system, and remove the WD Template Blocks
        else if (input.is(WDBlocks.WD_BASIC_2.get())) {result = get(BlockSetting.BlockType.BASIC, 1, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_BASIC_3.get())) {result = get(BlockSetting.BlockType.BASIC, 2, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_BASIC_4.get())) {result = get(BlockSetting.BlockType.BASIC, 3, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_STAIRS.get())) {result = get(BlockSetting.BlockType.STAIR, 0, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_STAIRS_2.get())) {result = get(BlockSetting.BlockType.STAIR, 1, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_STAIRS_3.get())) {result = get(BlockSetting.BlockType.STAIR, 2, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_STAIRS_4.get())) {result = get(BlockSetting.BlockType.STAIR, 3, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_SLAB.get())) {result = get(BlockSetting.BlockType.SLAB, 0, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_SLAB_2.get())) {result = get(BlockSetting.BlockType.SLAB, 1, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_SLAB_3.get())) {result = get(BlockSetting.BlockType.SLAB, 2, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_SLAB_4.get())) {result = get(BlockSetting.BlockType.SLAB, 3, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_WALL.get())) {result = get(BlockSetting.BlockType.WALL, 0, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_WALL_2.get())) {result = get(BlockSetting.BlockType.WALL, 1, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_WALL_3.get())) {result = get(BlockSetting.BlockType.WALL, 2, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_WALL_4.get())) {result = get(BlockSetting.BlockType.WALL, 3, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_LIGHT.get())) {result = get(BlockSetting.BlockType.LIGHT, 0, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_LIGHT_2.get())) {result = get(BlockSetting.BlockType.LIGHT, 1, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_LIGHT_3.get())) {result = get(BlockSetting.BlockType.LIGHT, 2, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_LIGHT_4.get())) {result = get(BlockSetting.BlockType.LIGHT, 3, noiseScale, pos);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT.get())) {result = get(BlockSetting.BlockType.HANGING_LIGHT, 0, noiseScale, pos).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_2.get())) {result = get(BlockSetting.BlockType.HANGING_LIGHT, 1, noiseScale, pos).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_3.get())) {result = get(BlockSetting.BlockType.HANGING_LIGHT, 2, noiseScale, pos).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_4.get())) {result = get(BlockSetting.BlockType.HANGING_LIGHT, 3, noiseScale, pos).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_SECRET.get())) {result = get(BlockSetting.BlockType.HIDDEN, 0, noiseScale, pos);}

        for (Property<?> property : input.getProperties()) {
            if (result.hasProperty(property)) {
                if (property == BlockStateProperties.CHEST_TYPE) {continue;}
                result = result.trySetValue((Property) property, input.getValue(property));
            }
        }
        //I tried to breakpoint here, but it never triggered. That means the stairs are being fucked up... somewhere else.
        /*if (input.hasProperty(BlockStateProperties.STAIRS_SHAPE) && result.hasProperty(BlockStateProperties.STAIRS_SHAPE)   && !input.getValue(BlockStateProperties.STAIRS_SHAPE).equals(result.getValue(BlockStateProperties.STAIRS_SHAPE))){
            return result;
        }*/

        return result;
    }

    @Override
    public String name() {return this.name;}

    public static class BlockSetting {
        public BlockState blockState;
        public int materialIndex;
        public BlockType blockType = BlockType.NONE;

        public BlockSetting(BlockState blockState, int materialIndex) {
            this.blockState = blockState;
            this.materialIndex = materialIndex;
        }

        public BlockSetting setMaterialIndex(int materialIndex) {this.materialIndex = materialIndex; return this;}
        public BlockSetting setBlockType(BlockType blockType) {this.blockType = blockType; return this;}

        public enum BlockType implements StringRepresentable {
            NONE, BASIC, STAIR, SLAB, WALL, LIGHT, HANGING_LIGHT, HIDDEN;

            @Override
            public @NotNull String getSerializedName() {
                return this.name().toLowerCase();
            }
        }
    }
}
