package com.danielkkrafft.wilddungeons.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class HeavyRuneBlock extends Block
{
    /**
     * {@link net.minecraft.world.level.block.Blocks#WITHER_SKELETON_SKULL}
     */
    public HeavyRuneBlock()
    {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.DEEPSLATE)
                .strength(55, 1200));
    }
}
