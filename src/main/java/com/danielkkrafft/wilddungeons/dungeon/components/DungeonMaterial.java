package com.danielkkrafft.wilddungeons.dungeon.components;


import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import com.danielkkrafft.wilddungeons.world.structure.WDStructureTemplate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DungeonMaterial implements DungeonRegistration.DungeonComponent {
    public String name;

    public ArrayList<WeightedPool<BlockState>> basicBlockStates;
    public ArrayList<WeightedPool<BlockState>> stairBlockStates;
    public ArrayList<WeightedPool<BlockState>> slabBlockStates;
    public ArrayList<WeightedPool<BlockState>> wallBlockStates;
    public ArrayList<WeightedPool<BlockState>> lightBlockStates;
    public ArrayList<WeightedPool<BlockState>> hangingLightBlockStates;
    public ArrayList<WeightedPool<BlockState>> hiddenBlockStates;

    public DungeonMaterial(String name, WeightedPool<BlockState> defaultBasicBlocks, WeightedPool<BlockState> defaultStairBlocks, WeightedPool<BlockState> defaultSlabBlocks, WeightedPool<BlockState> defaultWallBlocks, WeightedPool<BlockState> defaultLightBlocks, WeightedPool<BlockState> defaultHiddenBlocks) {
        this.name = name;
        this.basicBlockStates = new ArrayList<>(List.of(defaultBasicBlocks));
        this.stairBlockStates = new ArrayList<>(List.of(defaultStairBlocks));
        this.slabBlockStates = new ArrayList<>(List.of(defaultSlabBlocks));
        this.wallBlockStates = new ArrayList<>(List.of(defaultWallBlocks));
        this.lightBlockStates = new ArrayList<>(List.of(defaultLightBlocks));
        this.hiddenBlockStates = new ArrayList<>(List.of(defaultHiddenBlocks));
    }

    public DungeonMaterial setHangingLights(WeightedPool<BlockState> hangingLightBlockStates) {this.hangingLightBlockStates = new ArrayList<>(List.of(hangingLightBlockStates)); return this;}
    public DungeonMaterial addBasicBlockSet(WeightedPool<BlockState> blockStates) {this.basicBlockStates.add(blockStates); return this;}
    public DungeonMaterial addStairBlockSet(WeightedPool<BlockState> blockStates) {this.stairBlockStates.add(blockStates); return this;}
    public DungeonMaterial addSlabBlockSet(WeightedPool<BlockState> blockStates) {this.slabBlockStates.add(blockStates); return this;}
    public DungeonMaterial addWallBlockSet(WeightedPool<BlockState> blockStates) {this.wallBlockStates.add(blockStates); return this;}
    public DungeonMaterial addLightBlockSet(WeightedPool<BlockState> blockStates) {this.lightBlockStates.add(blockStates); return this;}
    public DungeonMaterial addHiddenBlockSet(WeightedPool<BlockState> blockStates) {this.hiddenBlockStates.add(blockStates); return this;}
    public DungeonMaterial addHangingLightBlockSet(WeightedPool<BlockState> blockStates) {this.hangingLightBlockStates.add(blockStates); return this;}

    public BlockState getBasic(int index) {return index > basicBlockStates.size()-1 ? basicBlockStates.getFirst().getRandom() : basicBlockStates.get(index).getRandom();}
    public BlockState getStair(int index) {return index > stairBlockStates.size()-1 ? stairBlockStates.getFirst().getRandom() : stairBlockStates.get(index).getRandom();}
    public BlockState getSlab(int index) {return index > slabBlockStates.size()-1 ? slabBlockStates.getFirst().getRandom() : slabBlockStates.get(index).getRandom();}
    public BlockState getWall(int index) {return index > wallBlockStates.size()-1 ? wallBlockStates.getFirst().getRandom() : wallBlockStates.get(index).getRandom();}
    public BlockState getLight(int index) {return index > lightBlockStates.size()-1 ? lightBlockStates.getFirst().getRandom() : lightBlockStates.get(index).getRandom();}
    public BlockState getHangingLight(int index) {
        if (hangingLightBlockStates == null || hangingLightBlockStates.isEmpty()) {return Blocks.AIR.defaultBlockState();}
        return index > hangingLightBlockStates.size()-1 ? hangingLightBlockStates.getFirst().getRandom() : hangingLightBlockStates.get(index).getRandom();
    }
    public BlockState getHidden(int index) {return index > hiddenBlockStates.size()-1 ? hiddenBlockStates.getFirst().getRandom() : hiddenBlockStates.get(index).getRandom();}

    public BlockState replace(BlockState input, WDStructureTemplate wdTemplate) {
        if (input.is(Blocks.AIR)) {return input;}
        BlockState result = input;
        if (wdTemplate != null){
            List<BlockSetting> dungeonIndexMapping = wdTemplate.getDungeonMaterialsAsList();
            Optional<BlockSetting> blockSettingOptional = dungeonIndexMapping.stream().filter(blockSetting -> blockSetting.blockState.equals(input.getBlock().defaultBlockState())).findFirst();
            if (blockSettingOptional.isPresent()) {
                BlockSetting blockSetting = blockSettingOptional.get();
                int materialIndex = blockSetting.materialIndex;
                switch (blockSetting.blockType) {
                    case BASIC -> result = getBasic(materialIndex);
                    case STAIR -> result = getStair(materialIndex);
                    case SLAB -> result = getSlab(materialIndex);
                    case WALL -> result = getWall(materialIndex);
                    case LIGHT -> result = getLight(materialIndex);
                    case HANGING_LIGHT -> result = getHangingLight(materialIndex).trySetValue(BlockStateProperties.HANGING,true);
                    case HIDDEN -> result = getHidden(materialIndex);
                }
            }
        }



        if (input.is(WDBlocks.WD_BASIC.get())) {result = getBasic(0);}//todo remove all these when we finish updating the old templates to the new system, and remove the WD Template Blocks
        else if (input.is(WDBlocks.WD_BASIC_2.get())) {result = getBasic(1);}
        else if (input.is(WDBlocks.WD_BASIC_3.get())) {result = getBasic(2);}
        else if (input.is(WDBlocks.WD_BASIC_4.get())) {result = getBasic(3);}
        else if (input.is(WDBlocks.WD_STAIRS.get())) {result = getStair(0);}
        else if (input.is(WDBlocks.WD_STAIRS_2.get())) {result = getStair(1);}
        else if (input.is(WDBlocks.WD_STAIRS_3.get())) {result = getStair(2);}
        else if (input.is(WDBlocks.WD_STAIRS_4.get())) {result = getStair(3);}
        else if (input.is(WDBlocks.WD_SLAB.get())) {result = getSlab(0);}
        else if (input.is(WDBlocks.WD_SLAB_2.get())) {result = getSlab(1);}
        else if (input.is(WDBlocks.WD_SLAB_3.get())) {result = getSlab(2);}
        else if (input.is(WDBlocks.WD_SLAB_4.get())) {result = getSlab(3);}
        else if (input.is(WDBlocks.WD_WALL.get())) {result = getWall(0);}
        else if (input.is(WDBlocks.WD_WALL_2.get())) {result = getWall(1);}
        else if (input.is(WDBlocks.WD_WALL_3.get())) {result = getWall(2);}
        else if (input.is(WDBlocks.WD_WALL_4.get())) {result = getWall(3);}
        else if (input.is(WDBlocks.WD_LIGHT.get())) {result = getLight(0);}
        else if (input.is(WDBlocks.WD_LIGHT_2.get())) {result = getLight(1);}
        else if (input.is(WDBlocks.WD_LIGHT_3.get())) {result = getLight(2);}
        else if (input.is(WDBlocks.WD_LIGHT_4.get())) {result = getLight(3);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT.get())) {result = getHangingLight(0).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_2.get())) {result = getHangingLight(1).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_3.get())) {result = getHangingLight(2).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_HANGING_LIGHT_4.get())) {result = getHangingLight(3).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.is(WDBlocks.WD_SECRET.get())) {result = getHidden(0);}

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
