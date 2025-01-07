package com.danielkkrafft.wilddungeons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RottenMossBlock extends Block
{
    protected static final VoxelShape SHAPE = Block.box(1,0,1,15,15,15);
    /**
     * {@link net.minecraft.world.level.block.Blocks#MOSS_BLOCK}
     * {@link net.minecraft.world.level.block.HoneyBlock}
     */
    public RottenMossBlock()
    {
        super(BlockBehaviour.Properties.of().
                mapColor(MapColor.COLOR_GREEN).
                strength(0.1F).
                sound(SoundType.MOSS).
                pushReaction(PushReaction.DESTROY));
    }
    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext)
    {
        return SHAPE;
    }
    @Override
    protected void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity entity)
    {
        if(entity instanceof LivingEntity li)poisonEntity(li);
    }
    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity)
    {
        if(entity instanceof LivingEntity li)poisonEntity(li);
    }
    private void poisonEntity(@NotNull LivingEntity li)
    {
        if(!li.hasEffect(MobEffects.POISON)) li.addEffect(new MobEffectInstance(MobEffects.POISON,100,2));
    }
}
