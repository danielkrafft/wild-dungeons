package com.danielkkrafft.wilddungeons.dungeon.components;


import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.DungeonRegistration;
import com.danielkkrafft.wilddungeons.dungeon.components.template.HierarchicalProperty;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class DungeonMaterial implements DungeonRegistration.DungeonComponent {
    public String name;

    public ArrayList<WeightedPool<BlockState>> basicBlockStates;
    public ArrayList<WeightedPool<BlockState>> stairBlockStates;
    public ArrayList<WeightedPool<BlockState>> slabBlockStates;
    public ArrayList<WeightedPool<BlockState>> wallBlockStates;
    public ArrayList<WeightedPool<BlockState>> lightBlockStates;
    public ArrayList<WeightedPool<BlockState>> hangingLightBlockStates;
    public ArrayList<WeightedPool<BlockState>> hiddenBlockStates;
    public float chestChance = .33f;

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
    public DungeonMaterial setChestChance(float chestChance) {this.chestChance = chestChance; return this;}
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
    public BlockState getHangingLight(int index) {return index > hangingLightBlockStates.size()-1 ? hangingLightBlockStates.getFirst().getRandom() : hangingLightBlockStates.get(index).getRandom();}
    public BlockState getHidden(int index) {return index > hiddenBlockStates.size()-1 ? hiddenBlockStates.getFirst().getRandom() : hiddenBlockStates.get(index).getRandom();}

    public BlockState replace(BlockState input, DungeonRoom room) {
        BlockState result = input;

        if (input.is(WDBlocks.WD_BASIC.get())) {result = getBasic(0);}
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
        //IMPORTANT: do NOT replace Trapped Chests as this will break some rooms
        else if (input.is(Blocks.CHEST))  {result = RandomUtil.randFloatBetween(0, 1) < room.getProperty(HierarchicalProperty.CHEST_SPAWN_CHANCE) ? Blocks.CHEST.defaultBlockState() : Blocks.AIR.defaultBlockState();}
        else if (input.is(Blocks.BARREL)) {result = RandomUtil.randFloatBetween(0, 1) < room.getProperty(HierarchicalProperty.CHEST_SPAWN_CHANCE) ? Blocks.BARREL.defaultBlockState() : Blocks.AIR.defaultBlockState();}
        else {return result;}

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
}
