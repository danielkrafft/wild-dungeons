package com.danielkkrafft.wilddungeons.dungeon;

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
        BlockState result = getBasic(0);
        if (input.getBlock() == Blocks.STONE_BRICKS) {result = getBasic(0);}
        if (input.getBlock() == Blocks.STONE_BRICK_STAIRS) {result = getStair(0);}
        if (input.getBlock() == Blocks.STONE_BRICK_SLAB) {result = getSlab(0);}
        if (input.getBlock() == Blocks.STONE_BRICK_WALL) {result = getWall(0);}
        if (input.getBlock() == Blocks.SEA_LANTERN) {result = getLight(0);}
        if (input.getBlock() == Blocks.CRACKED_STONE_BRICKS) {result = getHidden(0);}

        for (Property<?> property : input.getProperties()) {
            if (result.hasProperty(property)) {
                result = result.setValue((Property) property, input.getValue(property));
            }
        }

        return result;
    }

}
