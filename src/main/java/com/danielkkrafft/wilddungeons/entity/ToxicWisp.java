package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class ToxicWisp extends EmeraldWisp{
    public ToxicWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void explodeWisp() {
        if ((this.level() instanceof ServerLevel serverLevel)){
            this.dead = true;
            this.triggerOnDeathMobEffects(RemovalReason.KILLED);
            this.discard();
            //play sound
            this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F);
            //spawn toxic gas blocks
            int amount = 3 + this.random.nextInt(5);
            for (int i = 0; i < amount; i++) {
                BlockPos pos = this.blockPosition().offset(this.random.nextInt(3) - 1, 0, this.random.nextInt(3) - 1);
                if (serverLevel.isEmptyBlock(pos)) {
                    serverLevel.setBlockAndUpdate(pos, WDBlocks.TOXIC_GAS.get().defaultBlockState());
                }
            }
        }

    }
}
