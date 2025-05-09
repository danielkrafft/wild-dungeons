package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO.FRIENDLIES;

public class BusinessGolem extends AbstractGolem implements NeutralMob {
    public BusinessGolem(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(BusinessGolem.class, EntityDataSerializers.BYTE);
    private static final int IRON_INGOT_HEAL_AMOUNT = 25;
    private int attackAnimationTick;
    private int offerFlowerTick;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, (double)1.0F, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, FRIENDLIES).setAlertOthers(FRIENDLIES));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,(player)-> true));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)60f).add(Attributes.MOVEMENT_SPEED, (double)0.25F).add(Attributes.KNOCKBACK_RESISTANCE, (double)1.0F).add(Attributes.ATTACK_DAMAGE, (double)15.0F).add(Attributes.STEP_HEIGHT, (double)1.0F);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }

    protected void doPush(Entity entity) {
        if (entity instanceof Enemy && !(entity instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity)entity);
        }

        super.doPush(entity);
    }

    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.offerFlowerTick > 0) {
            --this.offerFlowerTick;
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

    }

    public boolean canSpawnSprintParticle() {
        return this.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F && this.random.nextInt(5) == 0;
    }

    public boolean canAttackType(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        } else {
            return type == EntityType.CREEPER ? false : super.canAttackType(type);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("PlayerCreated", this.isPlayerCreated());
        this.addPersistentAngerSaveData(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPlayerCreated(compound.getBoolean("PlayerCreated"));
        this.readPersistentAngerSaveData(this.level(), compound);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.remainingPersistentAngerTime = time;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level().broadcastEntityEvent(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        DamageSource damagesource = this.damageSources().mobAttack(this);
        boolean flag = entity.hurt(damagesource, f1);
        if (flag) {
            double var10000;
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                var10000 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            } else {
                var10000 = (double)0.0F;
            }

            double d0 = var10000;
            double d1 = Math.max((double)0.0F, (double)1.0F - d0);
            entity.setDeltaMovement(entity.getDeltaMovement().add((double)0.0F, (double)0.4F * d1, (double)0.0F));
            Level var11 = this.level();
            if (var11 instanceof ServerLevel) {
                ServerLevel serverlevel = (ServerLevel)var11;
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity, damagesource);
            }
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public boolean hurt(DamageSource source, float amount) {
        Crackiness.Level crackiness$level = this.getCrackiness();
        boolean flag = super.hurt(source, amount);
        if (flag && this.getCrackiness() != crackiness$level) {
            this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return flag;
    }

    public Crackiness.Level getCrackiness() {
        return Crackiness.GOLEM.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (id == 11) {
            this.offerFlowerTick = 400;
        } else if (id == 34) {
            this.offerFlowerTick = 0;
        } else {
            super.handleEntityEvent(id);
        }

    }

    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    public void offerFlower(boolean offeringFlower) {
        if (offeringFlower) {
            this.offerFlowerTick = 400;
            this.level().broadcastEntityEvent(this, (byte)11);
        } else {
            this.offerFlowerTick = 0;
            this.level().broadcastEntityEvent(this, (byte)34);
        }

    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!itemstack.is(Items.IRON_INGOT)) {
            return InteractionResult.PASS;
        } else {
            float f = this.getHealth();
            this.heal(IRON_INGOT_HEAL_AMOUNT);
            if (this.getHealth() == f) {
                return InteractionResult.PASS;
            } else {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
                itemstack.consume(1, player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
    }

    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public int getOfferFlowerTick() {
        return this.offerFlowerTick;
    }

    public boolean isPlayerCreated() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setPlayerCreated(boolean playerCreated) {
        byte b0 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (playerCreated) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
        }

    }

    public void die(DamageSource cause) {
        super.die(cause);
    }

    public boolean checkSpawnObstruction(LevelReader level) {
        BlockPos blockpos = this.blockPosition();
        BlockPos blockpos1 = blockpos.below();
        BlockState blockstate = level.getBlockState(blockpos1);
        if (!blockstate.entityCanStandOn(level, blockpos1, this)) {
            return false;
        } else {
            for(int i = 1; i < 3; ++i) {
                BlockPos blockpos2 = blockpos.above(i);
                BlockState blockstate1 = level.getBlockState(blockpos2);
                if (!NaturalSpawner.isValidEmptySpawnBlock(level, blockpos2, blockstate1, blockstate1.getFluidState(), EntityType.IRON_GOLEM)) {
                    return false;
                }
            }

            return NaturalSpawner.isValidEmptySpawnBlock(level, blockpos, level.getBlockState(blockpos), Fluids.EMPTY.defaultFluidState(), EntityType.IRON_GOLEM) && level.isUnobstructed(this);
        }
    }

    public Vec3 getLeashOffset() {
        return new Vec3((double)0.0F, (double)(0.875F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

}
