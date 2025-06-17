package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import java.util.concurrent.atomic.AtomicBoolean;

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
        public static final float SuicideBombSpeedBoost = 1.5f;
        public static final float SuicideBombRange = 1;

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
            double speed = wisp.getAttributeValue(Attributes.FLYING_SPEED) * SuicideBombSpeedBoost;
            wisp.setDeltaMovement(forward.scale(speed));

            // Check if the wisp is within range of a block to trigger the swell manually because the vanilla logic is poor and doesn't work well
            AtomicBoolean blockWithinRange = new AtomicBoolean(false);
            if (chargeTicks >= MAX_CHARGE_TICKS*0.5f){//only after 50% of charge time to prevent immediate explosion
                Vec3 wispPos = wisp.position();
                int range = (int) Math.ceil(SuicideBombRange);
                for (int dx = -range; dx <= range; dx++) {
                    for (int dy = -range; dy <= range; dy++) {
                        for (int dz = -range; dz <= range; dz++) {
                            Vec3 checkPos = wispPos.add(dx, dy, dz);
                            Vec3i checkBlockPos = new Vec3i((int) checkPos.x, (int) checkPos.y, (int) checkPos.z);
                            if (wisp.level().getBlockState(new BlockPos(checkBlockPos)).isSolid()) {
                                if (wispPos.distanceTo(checkPos) <= SuicideBombRange) {
                                    blockWithinRange.set(true);
                                    break;
                                }
                            }
                        }
                        if (blockWithinRange.get()) break;
                    }
                    if (blockWithinRange.get()) break;
                }
            }

            if (wisp.horizontalCollision || wisp.minorHorizontalCollision || wisp.verticalCollision || wisp.verticalCollisionBelow || chargeTicks >= MAX_CHARGE_TICKS || blockWithinRange.get()) {
                wisp.setSwellDir(1);
            }
        }
    }
}
