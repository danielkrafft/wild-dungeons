package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class DungeonMaterial {
    public List<WeightedPool<BlockState>> basicBlockStates;
    public List<WeightedPool<BlockState>> stairBlockStates;
    public List<WeightedPool<BlockState>> slabBlockStates;
    public List<WeightedPool<BlockState>> wallBlockStates;
    public List<WeightedPool<BlockState>> lightBlockStates;
    public List<WeightedPool<BlockState>> hiddenBlockStates;

    public DungeonMaterial (List<WeightedPool<BlockState>> basicBlockStates, List<WeightedPool<BlockState>> stairBlockStates, List<WeightedPool<BlockState>> slabBlockStates, List<WeightedPool<BlockState>> wallBlockStates, List<WeightedPool<BlockState>> lightBlockStates, List<WeightedPool<BlockState>> hiddenBlockStates) {
        this.basicBlockStates = basicBlockStates;
        this.stairBlockStates = stairBlockStates;
        this.slabBlockStates = slabBlockStates;
        this.wallBlockStates = wallBlockStates;
        this.lightBlockStates = lightBlockStates;
        this.hiddenBlockStates = hiddenBlockStates;
    }

    public BlockState getBasic(int index) {return basicBlockStates.get(index).getRandom();}
    public BlockState getStair(int index) {return stairBlockStates.get(index).getRandom();}
    public BlockState getSlab(int index) {return slabBlockStates.get(index).getRandom();}
    public BlockState getWall(int index) {return wallBlockStates.get(index).getRandom();}
    public BlockState getLight(int index) {return lightBlockStates.get(index).getRandom();}
    public BlockState getHidden(int index) {return hiddenBlockStates.get(index).getRandom();}

    public BlockState replace(BlockState input) {
        BlockState result = input;
        if (input.getBlock() == WDBlocks.WD_BASIC.get()) {result = getBasic(0);}
        else if (input.getBlock() == WDBlocks.WD_STAIRS.get()) {result = getStair(0);}
        else if (input.getBlock() == WDBlocks.WD_SLAB.get()) {result = getSlab(0);}
        else if (input.getBlock() == WDBlocks.WD_WALL.get()) {result = getWall(0);}
        else if (input.getBlock() == WDBlocks.WD_LIGHT.get()) {result = getLight(0);}
        else if (input.getBlock() == WDBlocks.WD_SECRET.get()) {result = getHidden(0);}
        else {return result;}

        for (Property<?> property : input.getProperties()) {
            if (result.hasProperty(property)) {
                result = result.setValue((Property) property, input.getValue(property));
            }
        }

        return result;
    }

}
