package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

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
                BlockPos pos = this.blockPosition().offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1,this.random.nextInt(3) - 1);
                if (serverLevel.isEmptyBlock(pos)) {
                    serverLevel.setBlockAndUpdate(pos, WDBlocks.TOXIC_GAS.get().defaultBlockState());
                }
            }
        }

    }

    @Override
    public void onDamageTaken(DamageContainer damageContainer) {
        if (damageContainer.getSource().is(DamageTypes.PLAYER_ATTACK)){
            //fly off in the direction the player hit it from
            this.setDeltaMovement(this.getDeltaMovement().add(damageContainer.getSource().getDirectEntity().getLookAngle().scale(3)));
            this.ignite();
            this.level().playSound(this,this.blockPosition(), SoundEvents.BEE_STING,this.getSoundSource(), 1.0F, 1.0F);
        }
        super.onDamageTaken(damageContainer);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16f)
                .add(Attributes.FLYING_SPEED, 0.15)
                .add(Attributes.MOVEMENT_SPEED, 0.15F)
                .add(Attributes.ATTACK_DAMAGE, 2.0F)
                .add(Attributes.FOLLOW_RANGE, 48.0F);
    }

}
