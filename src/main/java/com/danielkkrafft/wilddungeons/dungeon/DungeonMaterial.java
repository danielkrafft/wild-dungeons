package com.danielkkrafft.wilddungeons.dungeon;

import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class DungeonMaterial {
    public List<List<BlockState>> basicBlockStates;
    public List<List<BlockState>> stairBlockStates;
    public List<List<BlockState>> slabBlockStates;
    public List<List<BlockState>> wallBlockStates;
    public List<List<BlockState>> lightBlockStates;

    public DungeonMaterial (List<List<BlockState>> basicBlockStates, List<List<BlockState>> stairBlockStates, List<List<BlockState>> slabBlockStates, List<List<BlockState>> wallBlockStates, List<List<BlockState>> lightBlockStates) {
        this.basicBlockStates = basicBlockStates;
        this.stairBlockStates = stairBlockStates;
        this.slabBlockStates = slabBlockStates;
        this.wallBlockStates = wallBlockStates;
        this.lightBlockStates = lightBlockStates;
    }

    public BlockState getRandomFrom(int index, List<List<BlockState>> list) {
        return list.get(index-1).get(RandomUtil.randIntBetween(0, list.get(index-1).size()-1));
    }

    public BlockState getBasic(int index) {return getRandomFrom(index, basicBlockStates);}
    public BlockState getStair(int index) {return getRandomFrom(index, stairBlockStates);}
    public BlockState getSlab(int index) {return getRandomFrom(index, slabBlockStates);}
    public BlockState getWall(int index) {return getRandomFrom(index, wallBlockStates);}
    public BlockState getLight(int index) {return getRandomFrom(index, lightBlockStates);}

    public BlockState replace(BlockState input) {
        BlockState result = getBasic(1);
        if (input.getBlock() == Blocks.STONE_BRICKS) {result = getBasic(1);}
        if (input.getBlock() == Blocks.STONE_BRICK_STAIRS) {result = getStair(1);}
        if (input.getBlock() == Blocks.STONE_BRICK_SLAB) {result = getSlab(1);}
        if (input.getBlock() == Blocks.STONE_BRICK_WALL) {result = getWall(1);}
        if (input.getBlock() == Blocks.SEA_LANTERN) {result = getLight(1);}

        for (Property<?> property : input.getProperties()) {
            if (result.hasProperty(property)) {
                result = result.setValue((Property) property, input.getValue(property));
            }
        }

        return result;
    }

}
