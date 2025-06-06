package com.danielkkrafft.wilddungeons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class LifeLiquidBlock extends LiquidBlock {
    public LifeLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.hasEffect(MobEffects.REGENERATION)) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4));
            }
            if (livingEntity.getAbsorptionAmount() < 12.0f) {
                livingEntity.getAttribute(Attributes.MAX_ABSORPTION).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.MAX_ABSORPTION) + 12.0f);
                livingEntity.setAbsorptionAmount(12.0f);
            }

        }
    }
}
