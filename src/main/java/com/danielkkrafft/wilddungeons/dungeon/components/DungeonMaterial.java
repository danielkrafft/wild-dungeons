package com.danielkkrafft.wilddungeons.dungeon.components;


import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.block.WDBlocks;
import com.danielkkrafft.wilddungeons.dungeon.components.template.DungeonComponent;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import com.danielkkrafft.wilddungeons.util.WeightedPool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class DungeonMaterial implements DungeonComponent {
    public String name;

    public List<WeightedPool<BlockState>> basicBlockStates;
    public List<WeightedPool<BlockState>> stairBlockStates;
    public List<WeightedPool<BlockState>> slabBlockStates;
    public List<WeightedPool<BlockState>> wallBlockStates;
    public List<WeightedPool<BlockState>> lightBlockStates;
    public List<WeightedPool<BlockState>> hangingLightBlockStates;
    public List<WeightedPool<BlockState>> hiddenBlockStates;
    public float chestChance;

    public DungeonMaterial (String name, List<WeightedPool<BlockState>> basicBlockStates, List<WeightedPool<BlockState>> stairBlockStates, List<WeightedPool<BlockState>> slabBlockStates, List<WeightedPool<BlockState>> wallBlockStates, List<WeightedPool<BlockState>> lightBlockStates, List<WeightedPool<BlockState>> hiddenBlockStates, float chestChance) {
        this.name = name;
        this.basicBlockStates = basicBlockStates;
        this.stairBlockStates = stairBlockStates;
        this.slabBlockStates = slabBlockStates;
        this.wallBlockStates = wallBlockStates;
        this.lightBlockStates = lightBlockStates;
        this.hiddenBlockStates = hiddenBlockStates;
        this.chestChance = chestChance;
    }

    public DungeonMaterial setHangingLights(List<WeightedPool<BlockState>> hangingLightBlockStates) {this.hangingLightBlockStates = hangingLightBlockStates; return this;}

    public BlockState getBasic(int index) {return index > basicBlockStates.size()-1 ? basicBlockStates.getFirst().getRandom() : basicBlockStates.get(index).getRandom();}
    public BlockState getStair(int index) {return index > stairBlockStates.size()-1 ? stairBlockStates.getFirst().getRandom() : stairBlockStates.get(index).getRandom();}
    public BlockState getSlab(int index) {return index > slabBlockStates.size()-1 ? slabBlockStates.getFirst().getRandom() : slabBlockStates.get(index).getRandom();}
    public BlockState getWall(int index) {return index > wallBlockStates.size()-1 ? wallBlockStates.getFirst().getRandom() : wallBlockStates.get(index).getRandom();}
    public BlockState getLight(int index) {return index > lightBlockStates.size()-1 ? lightBlockStates.getFirst().getRandom() : lightBlockStates.get(index).getRandom();}
    public BlockState getHangingLight(int index) {return index > hangingLightBlockStates.size()-1 ? hangingLightBlockStates.getFirst().getRandom() : hangingLightBlockStates.get(index).getRandom();}
    public BlockState getHidden(int index) {return index > hiddenBlockStates.size()-1 ? hiddenBlockStates.getFirst().getRandom() : hiddenBlockStates.get(index).getRandom();}

    public BlockState replace(BlockState input) {
        BlockState result = input;

        if (input.getBlock() == WDBlocks.WD_BASIC.get()) {result = getBasic(0);}
        else if (input.getBlock() == WDBlocks.WD_BASIC_2.get()) {result = getBasic(1);}
        else if (input.getBlock() == WDBlocks.WD_BASIC_3.get()) {result = getBasic(2);}
        else if (input.getBlock() == WDBlocks.WD_BASIC_4.get()) {result = getBasic(3);}
        else if (input.getBlock() == WDBlocks.WD_STAIRS.get()) {result = getStair(0);}
        else if (input.getBlock() == WDBlocks.WD_STAIRS_2.get()) {result = getStair(1);}
        else if (input.getBlock() == WDBlocks.WD_STAIRS_3.get()) {result = getStair(2);}
        else if (input.getBlock() == WDBlocks.WD_STAIRS_4.get()) {result = getStair(3);}
        else if (input.getBlock() == WDBlocks.WD_SLAB.get()) {result = getSlab(0);}
        else if (input.getBlock() == WDBlocks.WD_SLAB_2.get()) {result = getSlab(1);}
        else if (input.getBlock() == WDBlocks.WD_SLAB_3.get()) {result = getSlab(2);}
        else if (input.getBlock() == WDBlocks.WD_SLAB_4.get()) {result = getSlab(3);}
        else if (input.getBlock() == WDBlocks.WD_WALL.get()) {result = getWall(0);}
        else if (input.getBlock() == WDBlocks.WD_WALL_2.get()) {result = getWall(1);}
        else if (input.getBlock() == WDBlocks.WD_WALL_3.get()) {result = getWall(2);}
        else if (input.getBlock() == WDBlocks.WD_WALL_4.get()) {result = getWall(3);}
        else if (input.getBlock() == WDBlocks.WD_LIGHT.get()) {result = getLight(0);}
        else if (input.getBlock() == WDBlocks.WD_LIGHT_2.get()) {result = getLight(1);}
        else if (input.getBlock() == WDBlocks.WD_LIGHT_3.get()) {result = getLight(2);}
        else if (input.getBlock() == WDBlocks.WD_LIGHT_4.get()) {result = getLight(3);}
        else if (input.getBlock() == WDBlocks.WD_HANGING_LIGHT.get()) {result = getHangingLight(0).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.getBlock() == WDBlocks.WD_HANGING_LIGHT_2.get()) {result = getHangingLight(1).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.getBlock() == WDBlocks.WD_HANGING_LIGHT_3.get()) {result = getHangingLight(2).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.getBlock() == WDBlocks.WD_HANGING_LIGHT_4.get()) {result = getHangingLight(3).trySetValue(BlockStateProperties.HANGING,true);}
        else if (input.getBlock() == WDBlocks.WD_SECRET.get()) {result = getHidden(0);}
        else if (input.getBlock() == Blocks.CHEST) {result = RandomUtil.randFloatBetween(0, 1) < chestChance ? Blocks.CHEST.defaultBlockState() : Blocks.AIR.defaultBlockState();}
        else if (input.getBlock() == Blocks.BARREL) {result = RandomUtil.randFloatBetween(0, 1) < chestChance ? Blocks.BARREL.defaultBlockState() : Blocks.AIR.defaultBlockState();}
        else {return result;}

        for (Property<?> property : input.getProperties()) {
            if (result.hasProperty(property)) {
//                if (property==BlockStateProperties.STAIRS_SHAPE) {
//                    WildDungeons.getLogger().info("Placing stairs with shape: {} && and half: {}", input.getValue(property), input.getValue(BlockStateProperties.HALF));
//                }
                if (property == BlockStateProperties.CHEST_TYPE) {continue;}
                result = result.setValue((Property) property, input.getValue(property));
            }
        }

        return result;
    }

    @Override
    public String name() {return this.name;}
}
