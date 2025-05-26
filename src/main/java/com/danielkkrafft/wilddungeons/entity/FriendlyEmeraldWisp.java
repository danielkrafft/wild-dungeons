package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FriendlyEmeraldWisp extends EmeraldWisp{
    public FriendlyEmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerSpecificGoals() {
        this.goalSelector.addGoal(1, new SuicideBombGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0F, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Monster.class, true, (target) -> target != this.getOwner()));
    }
    
    public static class SuicideBombGoal extends Goal {
        private final FriendlyEmeraldWisp wisp;
        private int chargeTicks = 0;
        private static final int MAX_CHARGE_TICKS = 60; // 3 seconds

        public SuicideBombGoal(FriendlyEmeraldWisp wisp) {
            this.wisp = wisp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            return this.wisp.getTarget() == null;
        }
        
        @Override
        public boolean canContinueToUse() {
            return wisp.getTarget() == null && !this.wisp.isDeadOrDying();
        }
        
        @Override
        public void start() {
            this.chargeTicks = 0;
        }
        
        @Override
        public void stop() {
            chargeTicks = 0;
        }
        
        @Override
        public void tick() {
            chargeTicks++;

            // Optional: ensure rotations are consistent
            wisp.yBodyRot = wisp.getYRot();
            wisp.yHeadRot = wisp.getYRot();

            // Calculate forward motion from pitch/yaw
            Vec3 forward = Vec3.directionFromRotation(wisp.getXRot(), wisp.getYRot()).normalize();
            double speed = wisp.getAttributeValue(Attributes.FLYING_SPEED);
            wisp.setDeltaMovement(forward.scale(speed));

            if (wisp.horizontalCollision || wisp.minorHorizontalCollision || wisp.verticalCollision || wisp.verticalCollisionBelow || chargeTicks >= MAX_CHARGE_TICKS) {
                wisp.setSwellDir(1);
            }
        }
    }
}
