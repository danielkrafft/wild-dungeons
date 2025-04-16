package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LargeEmeraldWisp extends EmeraldWisp {

    public LargeEmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void registerSpecificGoals() {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 16f, 0.6, 1.0F));
        this.goalSelector.addGoal(2, new SummonMoreGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, (target) -> target != this.getOwner()));
    }

    @Override
    public void explodeWisp() {
        this.extinguish();
        if (!this.level().isClientSide) {
            //summon two small wisps
            for (int i = 0; i < 2 ; i++) {
                EmeraldWisp smallWisp = (EmeraldWisp) getSummonType().create(this.level());
                if (smallWisp != null) {
                    BlockPos pos = this.blockPosition().offset(-2 + random.nextInt(5),1, -2 + random.nextInt(5));
                    smallWisp.setPos(pos.getCenter());
                    smallWisp.setOwner(this.getOwner());
                    smallWisp.setTarget(this.getTarget());
                    this.level().addFreshEntity(smallWisp);
                    this.level().playSound(this,this.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL,this.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }

    public EntityType<?> getSummonType() {
        return WDEntities.SMALL_EMERALD_WISP.get();
    }

    @Override
    public int getMaxSwell() {
        return 80;
    }

    public static class SummonMoreGoal extends Goal {
        private final LargeEmeraldWisp wisp;
        @javax.annotation.Nullable
        private LivingEntity target;

        public SummonMoreGoal(LargeEmeraldWisp wisp) {
            this.wisp = wisp;
        }

        public boolean canUse() {
            LivingEntity livingentity = this.wisp.getTarget();
            return this.wisp.isIgnited()|| livingentity != null && this.wisp.distanceToSqr(livingentity) > (double) 7.0F;
        }

        public void start() {
            this.target = this.wisp.getTarget();
        }

        public void stop() {
            this.target = null;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target == null) {
                this.wisp.extinguish();
            } else {
                this.wisp.ignite();
            }
        }
    }
}