package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FriendlySpiderling extends Spiderling implements TraceableEntity {
    @javax.annotation.Nullable
    Entity owner;

    public FriendlySpiderling(EntityType<? extends Spiderling> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal(this, Armadillo.class, 6.0F, (double)1.0F, 1.2, (p_320185_) -> !((Armadillo)p_320185_).isScared()));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true, (target) -> target != this.getOwner() && !(target instanceof FriendlySpiderling)));
    }

    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
    }
}
