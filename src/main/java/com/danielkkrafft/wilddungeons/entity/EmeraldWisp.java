package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EmeraldWisp extends PathfinderMob implements TraceableEntity {
    @javax.annotation.Nullable
    Entity owner;
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(EmeraldWisp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(EmeraldWisp.class, EntityDataSerializers.BOOLEAN);
    private static final boolean INSTANT_EXPLODE = true;
    protected int oldSwell;
    protected int swell;
    private int explosionRadius = 2;

    public EmeraldWisp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    public @Nullable Entity getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        registerSpecificGoals();
    }

    protected void registerSpecificGoals() {
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0F, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, (target) -> target != this.getOwner()));
    }

    public int getMaxSwell() {
        return 30;
    }

    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SWELL_DIR, -1);
        builder.define(DATA_IS_IGNITED, false);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putByte("ExplosionRadius", (byte) this.explosionRadius);
        compound.putBoolean("ignited", this.isIgnited());
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);


        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }

        if (compound.getBoolean("ignited")) {
            this.ignite();
        }

    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.getMaxSwell() - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int state) {
        this.entityData.set(DATA_SWELL_DIR, state);
    }

    public void explodeWisp() {
        if (!this.level().isClientSide) {
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius, Level.ExplosionInteraction.MOB);
            this.triggerOnDeathMobEffects(RemovalReason.KILLED);
            this.discard();
        }
    }

    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int i = this.getSwellDir();
            if (i > 0 && this.swell == 0) {

                if (INSTANT_EXPLODE) {
                    explodeWisp();
                    return;
                }

                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.getMaxSwell()) {
                this.swell = this.getMaxSwell();
                this.explodeWisp();
            }
        }

        float xRot = this.getXRot();
        super.tick();
        this.setXRot(xRot);
    }

    public void setTarget(@javax.annotation.Nullable LivingEntity target) {
        if (!(target instanceof Goat)) {
            super.setTarget(target);
        }

    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.VEX_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    public void extinguish() {
        this.entityData.set(DATA_IS_IGNITED, false);
        this.swell = 0;
        this.oldSwell = 0;
        this.setSwellDir(-1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16f)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.MOVEMENT_SPEED, 0.15F)
                .add(Attributes.ATTACK_DAMAGE, 2.0F)
                .add(Attributes.FOLLOW_RANGE, 48.0F);
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    public static class SwellGoal extends Goal {
        private final EmeraldWisp wisp;
        @javax.annotation.Nullable
        private LivingEntity target;

        public SwellGoal(EmeraldWisp wisp) {
            this.wisp = wisp;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.wisp.getTarget();
            return this.wisp.getSwellDir() > 0 || livingentity != null && this.wisp.distanceToSqr(livingentity) < (double) 9.0F;
        }

        public void start() {
            this.wisp.getNavigation().stop();
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
                this.wisp.setSwellDir(-1);
            } else if (this.wisp.distanceToSqr(this.target) > (double) 49.0F) {
                this.wisp.setSwellDir(-1);
            } else if (!this.wisp.getSensing().hasLineOfSight(this.target)) {
                this.wisp.setSwellDir(-1);
            } else {
                this.wisp.setSwellDir(1);
            }
        }
    }

    @Override
    public boolean isDamageSourceBlocked(DamageSource damageSource) {
        if (damageSource.is(DamageTypes.EXPLOSION))
            return true;
        return super.isDamageSourceBlocked(damageSource);
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        if (damageSource.is(DamageTypes.EXPLOSION))
            return;
        super.handleDamageEvent(damageSource);
    }

    @Override
    public void onDamageTaken(DamageContainer damageContainer) {
        if (damageContainer.getSource().is(DamageTypes.EXPLOSION)){
            damageContainer.setNewDamage(0);
            damageContainer.setPostAttackInvulnerabilityTicks(0);
        }
        super.onDamageTaken(damageContainer);
    }
}