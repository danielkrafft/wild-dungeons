package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.WDEntities;
import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class NetherDragonEntity extends Monster implements RangedAttackMob, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_20
    );

    public NetherDragonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        moveControl = new FlyingMoveControl(this, 10, true);
        xpReward = 200;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1, 30));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof NetherDragonEntity)));
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 225).
                add(Attributes.MOVEMENT_SPEED, 0.5).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 10).
                add(Attributes.ATTACK_KNOCKBACK, 2).
                add(Attributes.KNOCKBACK_RESISTANCE, 0.4).
                add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2).
                add(Attributes.FLYING_SPEED, 1).
                build();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation path = new FlyingPathNavigation(this, level);
        path.setCanFloat(true);
        path.setCanPassDoors(true);
        return path;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        super.defineSynchedData(builder);
//        builder.define(TICKSINVULNERABLE,0);
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getAmbientSound() { return WDSoundEvents.BREEZE_GOLEM_AMBIENT.value(); }//todo

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return getAmbientSound();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return WDSoundEvents.BREEZE_GOLEM_DEATH.value();
    }//todo

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) { bossEvent.addPlayer(serverPlayer); }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getCommandSenderWorld();
        bossEvent.setVisible(true);
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        if (!level.isClientSide && !isDeadOrDying()) {
            //logic
        }
    }
}