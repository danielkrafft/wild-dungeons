package com.danielkkrafft.wilddungeons.block;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class LifeLiquidBlock extends LiquidBlock {
    public LifeLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

//    @Override TODO - Fix for 1.21.11
//    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
//        super.entityInside(state, level, pos, entity);
//
//        if (entity instanceof LivingEntity livingEntity) {
//            if (!livingEntity.hasEffect(MobEffects.REGENERATION)) {
//                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 4));
//            }
//            if (livingEntity.getAbsorptionAmount() < 12.0f) {
//                livingEntity.getAttribute(Attributes.MAX_ABSORPTION).setBaseValue(livingEntity.getAttributeBaseValue(Attributes.MAX_ABSORPTION) + 12.0f);
//                livingEntity.setAbsorptionAmount(12.0f);
//            }
//
//        }
//    }
}
