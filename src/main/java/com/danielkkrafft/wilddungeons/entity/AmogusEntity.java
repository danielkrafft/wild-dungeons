package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class AmogusEntity extends PathfinderMob
{

    public AmogusEntity(EntityType<? extends AmogusEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20).
                add(Attributes.MOVEMENT_SPEED, 0.25).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 2).
                build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1, 300));

        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.5F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, false));
    }

    @Override protected SoundEvent getHurtSound(DamageSource damageSourceIn) {return WDSoundEvents.AMOGUS_KILL.value();}
    @Override protected SoundEvent getDeathSound() {
        return WDSoundEvents.AMOGUS_KILL.value();
    }
    @Override protected SoundEvent getAmbientSound() {
        return WDSoundEvents.AMOGUS_AMBIENT.value();
    }
    @Override public int getAmbientSoundInterval() {return 240;}

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(WDSoundEvents.AMOGUS_STEP.value(), 0.5f, RandomUtil.randFloatBetween(0.5f, 1.0f));
        super.playStepSound(pos, state);
    }
}