package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

import static com.danielkkrafft.wilddungeons.entity.boss.BusinessCEO.AttackPhase.*;

public class BusinessCEO extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
    private static final String
            idle = "idle",
            stand_up = "stand_up",
            punch = "punch",
            kick = "kick",
            point = "point",
            ascend = "ascend",
            dash = "dash";
    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            standUpAnim = RawAnimation.begin().thenPlay(stand_up),
            punchAnim = RawAnimation.begin().thenPlay(punch),
            kickAnim = RawAnimation.begin().thenPlay(kick),
            pointAnim = RawAnimation.begin().thenPlay(point),
            ascendAnim = RawAnimation.begin().thenPlay(ascend),
            dashAnim = RawAnimation.begin().thenPlay(dash);
    private static final EntityDataAccessor<Integer> ATTACK_PHASE = SynchedEntityData.defineId(BusinessCEO.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_INVULNERABLE = SynchedEntityData.defineId(BusinessCEO.class, EntityDataSerializers.INT);
    private static final int SUMMON_TICKS = 70;
    private static final String
            ceo_controller = "ceo_controller";
    private final AnimationController<BusinessCEO> mainController = new AnimationController<>(this, ceo_controller, 5,
            state -> state.setAndContinue(idleAnim))
            .triggerableAnim(idle, idleAnim)
            .triggerableAnim(stand_up, standUpAnim)
            .triggerableAnim(punch, punchAnim)
            .triggerableAnim(kick, kickAnim)
            .triggerableAnim(point, pointAnim)
            .triggerableAnim(ascend, ascendAnim)
            .triggerableAnim(dash, dashAnim);

    public BusinessCEO(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 200;
        this.setAttackPhase(IDLE);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SummonGoal(this));
        goalSelector.addGoal(1, new BusinessCEOMeleeAttackGoal());
        goalSelector.addGoal(1, new BusinessCEODashGoal());
        goalSelector.addGoal(2, new BusinessCEOAttackStrategyGoal());
//        goalSelector.addGoal(2, new BusinessCEOSummonTargetGoal());
//        goalSelector.addGoal(3, new BusinessCEOAscendGoal());
//        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 30));
//        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof BusinessCEO)));
        targetSelector.addGoal(1, new BusinessCEOTargetPlayerGoal());
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 225)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.ATTACK_KNOCKBACK, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2)
                .build();
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getCommandSenderWorld();
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        //get the current goal
        WrappedGoal goal = goalSelector.getAvailableGoals().stream().filter(WrappedGoal::isRunning).findFirst().orElse(null);
        if (goal != null) {
            bossEvent.setName(Component.literal(getAttackPhase().name() +" "+ goal.getGoal().getClass().getSimpleName()));
        } else {
            bossEvent.setName(Component.literal(getAttackPhase().name()));
        }
        if (!level.isClientSide && !isDeadOrDying()) {
            //logic
            if (getTarget() !=null){
                //particle effects to indicate targeting
                Vec3 pos = getTarget().position();
                UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.ELECTRIC_SPARK, true, 25, pos.x, pos.y + 1.5f, pos.z, 1, 1, 1, 0.06f);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AP", this.getAttackPhase().ordinal());
        compound.putInt("TI", this.getTicksInvulnerable());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        if (compound.contains("AP")) {
            this.setAttackPhase(AttackPhase.values()[compound.getInt("AP")]);
        }
        if (compound.contains("TI")) {
            this.setTicksInvulnerable(compound.getInt("TI"));
        }
    }

    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }


    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.removePlayer(serverPlayer);
    }

    public AttackPhase getAttackPhase() {
        return AttackPhase.values()[this.entityData.get(ATTACK_PHASE)];
    }

    public void setAttackPhase(AttackPhase phase) {
        this.entityData.set(ATTACK_PHASE, phase.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_PHASE, 0);
        builder.define(TICKS_INVULNERABLE, 0);
    }

    public void setTicksInvulnerable(int ticks) {
        this.entityData.set(TICKS_INVULNERABLE, ticks);
    }

    public int getTicksInvulnerable() {
        return this.entityData.get(TICKS_INVULNERABLE);
    }

    public boolean isInvulnerable() {
        return getTicksInvulnerable() <= SUMMON_TICKS;
    }

    public enum AttackPhase {
        IDLE,
        POINT,
        ASCEND,
        DASH,
        MELEE,
    }

    public class SummonGoal extends Goal {
        public SummonGoal(@NotNull Mob mob) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
        }

        @Override
        public void tick() {
            int ticks = BusinessCEO.this.getTicksInvulnerable();
            BusinessCEO.this.setTicksInvulnerable(BusinessCEO.this.getTicksInvulnerable() + 1);
            if (ticks % 20 == 0)
                BusinessCEO.this.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1, 2f);
        }

        @Override
        public void start() {
            BusinessCEO.this.triggerAnim(ceo_controller, stand_up);
            BusinessCEO.this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 1, 0.7f);
            BusinessCEO.this.setInvulnerable(true);
        }

        @Override
        public boolean canUse() {
            return BusinessCEO.this.isInvulnerable();
        }

        @Override
        public void stop() {
            //psuedo explosion
            Vec3 pos = BusinessCEO.this.position();
            List<LivingEntity> list = BusinessCEO.this.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(BusinessCEO.this.position(), 10, 10, 10), BusinessCEO.this::hasLineOfSight);
            list.remove(BusinessCEO.this);
            for (LivingEntity li : list) {
                Vec3 kb = new Vec3(pos.x - li.position().x, pos.y - li.position().y, pos.z - li.position().z).
                        normalize().scale(2);
                li.knockback(1.5, kb.x, kb.z);
                li.setRemainingFireTicks(li.getRemainingFireTicks() + 100);
                li.hurt(new DamageSource(BusinessCEO.this.level().damageSources().generic().typeHolder()), 10);
            }
            BusinessCEO.this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 2f, 0.8f);
            UtilityMethods.sendParticles((ServerLevel) BusinessCEO.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
            UtilityMethods.sendParticles((ServerLevel) BusinessCEO.this.level(), ParticleTypes.ENCHANT, true, 200, pos.x, pos.y, pos.z, 1, 2, 1, 0.06f);
            UtilityMethods.sendParticles((ServerLevel) BusinessCEO.this.level(), ParticleTypes.COMPOSTER, true, 400, pos.x, pos.y, pos.z, 1, 2, 1, 0.08f);
            BusinessCEO.this.setInvulnerable(false);
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
        }
    }

    class BusinessCEOAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        BusinessCEOAttackStrategyGoal() {
        }

        public boolean canUse() {
            if (BusinessCEO.this.getAttackPhase() == IDLE) {
                LivingEntity livingentity = BusinessCEO.this.getTarget();
                return livingentity != null && BusinessCEO.this.canAttack(livingentity, TargetingConditions.DEFAULT.range(100));
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            if (BusinessCEO.this.getAttackPhase() == IDLE) {
                LivingEntity livingentity = BusinessCEO.this.getTarget();
                return livingentity != null && BusinessCEO.this.canAttack(livingentity, TargetingConditions.DEFAULT.range(100));
            } else {
                return false;
            }
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(20);
        }

        public void stop() {
        }

        public void tick() {
            if (!BusinessCEO.this.isInvulnerable()) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    this.nextSweepTick = this.adjustedTickDelay((8 + BusinessCEO.this.random.nextInt(3)) * 20);
                    AttackPhase newPhase = AttackPhase.values()[BusinessCEO.this.random.nextInt(0, 3)];
                    WildDungeons.getLogger().info("BusinessCEO: " + newPhase);
                    newPhase = IDLE;
                    BusinessCEO.this.setAttackPhase(newPhase);
                    BusinessCEO.this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 10.0F, 0.95F + BusinessCEO.this.random.nextFloat() * 0.1F);
                    switch (BusinessCEO.this.getAttackPhase()) {
                        case POINT -> {
                        }
                        case ASCEND -> {
                        }
                    }
                }
            }
        }
    }

    class BusinessCEOTargetPlayerGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0F);
        private int nextScanTick = reducedTickDelay(20);

        BusinessCEOTargetPlayerGoal() {
        }

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = BusinessCEO.this.level().getNearbyPlayers(this.attackTargeting, BusinessCEO.this, BusinessCEO.this.getBoundingBox().inflate(16.0F, 64.0F, 16.0F));
                if (!list.isEmpty()) {
                    list.sort((p1, p2) -> BusinessCEO.this.random.nextInt(3) - 1);
                    for (Player player : list) {
                        if (BusinessCEO.this.canAttack(player, attackTargeting)) {
                            BusinessCEO.this.setTarget(player);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            return livingentity != null && BusinessCEO.this.canAttack(livingentity, attackTargeting);
        }
    }

    class BusinessCEOMeleeAttackGoal extends Goal {
        private int ticks;
        private boolean hasAttacked;

        public BusinessCEOMeleeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            if (BusinessCEO.this.getAttackPhase() == IDLE || BusinessCEO.this.getAttackPhase() == MELEE) {
                LivingEntity livingentity = BusinessCEO.this.getTarget();
                return livingentity != null && this.canPerformAttack(livingentity);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (livingentity == null) {
                return false;
            } else return livingentity.isAlive() && BusinessCEO.this.distanceToSqr(livingentity) < 6;
        }

        public void start() {
            this.ticks = 10;
            this.hasAttacked = false;
            BusinessCEO.this.setAttackPhase(MELEE);
            BusinessCEO.this.triggerAnim(ceo_controller, BusinessCEO.this.random.nextBoolean() ? punch : kick);
        }

        public void stop() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                BusinessCEO.this.setTarget(null);
            }
            BusinessCEO.this.setAttackPhase(IDLE);
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (livingentity != null) {
                BusinessCEO.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                this.ticks = Math.max(this.ticks - 1, 0);
                if (!this.hasAttacked && this.ticks <= 0 && this.canPerformAttack(livingentity)) {
                    BusinessCEO.this.doHurtTarget(livingentity);
                    BusinessCEO.this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 0.8f);
                    this.hasAttacked = true;
                    this.ticks = 40;
                } else if (this.hasAttacked){
                    this.ticks = Math.max(this.ticks - 1, 0);
                    if (this.ticks <= 0) {
                        this.stop();
                    }
                }
            } else {
                this.stop();
            }
        }

        protected boolean canPerformAttack(LivingEntity entity) {
            return BusinessCEO.this.distanceToSqr(entity) < 12 && BusinessCEO.this.getSensing().hasLineOfSight(entity);
        }
    }

    class BusinessCEODashGoal extends Goal {
        int ticks = 0;
        boolean isDashing = false;
        public BusinessCEODashGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (BusinessCEO.this.getAttackPhase() != IDLE) {
                return false;
            }
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            return livingentity != null && BusinessCEO.this.distanceToSqr(livingentity) > 36;
        }

        @Override
        public boolean canContinueToUse() {
            return BusinessCEO.this.getAttackPhase() == DASH && ticks > 0;
        }

        public void start() {
            isDashing = false;
            BusinessCEO.this.setAttackPhase(DASH);
            this.ticks = 15;
            BusinessCEO.this.triggerAnim(ceo_controller, dash);
            LivingEntity livingentity = BusinessCEO.this.getTarget();
        }

        public void stop() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                BusinessCEO.this.setTarget(null);
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (livingentity != null) {
                Vec3 dir = livingentity.position().subtract(BusinessCEO.this.position()).normalize();
                BusinessCEO.this.setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));
                BusinessCEO.this.lookAt(livingentity, 30.0F, 30.0F);
                if (!isDashing) {
                    this.ticks = Math.max(this.ticks - 1, 0);
                    if (this.ticks <= 0) {
                        BusinessCEO.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                        BusinessCEO.this.setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));
                        BusinessCEO.this.lookAt(livingentity, 30.0F, 30.0F);
                        Vec3 move = new Vec3(dir.x, 0, dir.z).normalize().scale(3);
                        BusinessCEO.this.setDeltaMovement(move);
                        isDashing = true;
                        ticks = 30;
                    }
                } else {
                    ticks = Math.max(ticks - 1, 0);
                    if (ticks <= 0) {
                        BusinessCEO.this.setAttackPhase(IDLE);
                        BusinessCEO.this.triggerAnim(ceo_controller, idle);
                    }
                }
            }
        }
    }
}