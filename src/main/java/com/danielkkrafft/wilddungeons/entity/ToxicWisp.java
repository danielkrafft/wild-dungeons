package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.entity.boss.CopperSentinel;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.effect.MobEffects.POISON;

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
            //particles
            serverLevel.sendParticles(ParticleTypes.ASH, this.getX(), this.getY(), this.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
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
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            for (Entity entity : level().getEntities(this, this.getBoundingBox())) {
                if (entity instanceof CopperSentinel copperSentinel) {
                    explodeWisp();
                    copperSentinel.hurt(this.damageSources().explosion(this,this),100);
                    break;
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

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.is(POISON)) {
            return false;
        }
        return super.addEffect(effectInstance, entity);
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
