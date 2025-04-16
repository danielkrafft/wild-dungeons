package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LargeEmeraldWisp extends EmeraldWisp {

    public LargeEmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 16f, 0.6, 1.0F));
        this.goalSelector.addGoal(2, new SummonMoreGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, (target) -> target != this.getOwner()));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public void explodeWisp() {
        this.extinguish();
        if (!this.level().isClientSide) {
            //summon two small wisps
            for (int i = 0; i < 2 ; i++) {
                EmeraldWisp smallWisp = WDEntities.SMALL_EMERALD_WISP.get().create(this.level());
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