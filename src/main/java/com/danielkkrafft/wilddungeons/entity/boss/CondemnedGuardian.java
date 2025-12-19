package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;

public class CondemnedGuardian extends PathfinderMob implements GeoEntity {
    private static final int SEGMENT_COUNT = 12;
    private static final double SEGMENT_SPACING = 1.5D;
    private static final double WAVE_AMPLITUDE = 0.25D;

    private final List<CondemnedGuardianSegment> segments = new ArrayList<>();
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Float> DATA_HEALTH_SYNC = SynchedEntityData.defineId(CondemnedGuardian.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BOSS_PROGRESS = SynchedEntityData.defineId(CondemnedGuardian.class, EntityDataSerializers.FLOAT);

    public boolean isShootingLaser = false;

    public CondemnedGuardian(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100;
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEALTH_SYNC, 1000.0f);
        builder.define(DATA_BOSS_PROGRESS, 1.0f);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new CondemnedChargeAttackGoal(this));
        //goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 20));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(3, new WaterMovementGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        handleWaterPhysics();
        if (isInWater()) {
            setAirSupply(getMaxAirSupply());
        }

        if (level().isClientSide) {
            setHealth(this.entityData.get(DATA_HEALTH_SYNC));
            this.bossEvent.setProgress(this.entityData.get(DATA_BOSS_PROGRESS));
            return;
        }

        updateSegmentPositions();
        updateBossHealth();
        handleAttacks();
    }

    public float getAttackAnimationScale(float partialTick) {
        return 0.5f;
    }

    public float getClientSideAttackTime() {
        return 0.5f;
    }

    private void handleWaterPhysics() {
        setNoGravity(isInWater());
        if (isInWater()) {
            if (getDeltaMovement().y < 0.05D && getY() < 48) {
                setDeltaMovement(getDeltaMovement().add(0.0D, 0.01D, 0.0D));
            }
        }
    }

    private void updateSegmentPositions() {
        if (this.segments.isEmpty()) return;

        float yWaveOffset = (float)(this.tickCount * 0.15D);

        for (int i = 0; i < this.segments.size(); i++) {
            CondemnedGuardianSegment segment = this.segments.get(i);
            if (segment == null || !segment.isAlive()) continue;

            double segmentDistance = (i + 1) * SEGMENT_SPACING;
            Vec3 lookDir = getLookAngle().normalize();


            double waveY = Math.cos(yWaveOffset + i * 0.8D) * 0.3;
            Vec3 targetPos = position().add(
                    lookDir.x * -segmentDistance,
                    lookDir.y * -segmentDistance,
                    lookDir.z * -segmentDistance
            );

            if (isInWater()) {
                targetPos = new Vec3(targetPos.x, getY()+ waveY, targetPos.z);
            }

            Vec3 currentPos = segment.position();
            Vec3 direction = targetPos.subtract(currentPos);
            double distance = direction.length();

            if (distance > 0.1D) {
                direction = direction.normalize();
                Vec3 newPos = currentPos.add(direction.scale(Math.min(distance * 0.85D, 0.4D)));
                segment.teleportTo(newPos.x, newPos.y, newPos.z);
            }

            if (i == 0) {
                segment.lookAt(EntityAnchorArgument.Anchor.EYES, position().add(0,0.5,0));
            } else {
                CondemnedGuardianSegment prevSegment = this.segments.get(i - 1);
                if (prevSegment != null && prevSegment.isAlive()) {
                    segment.lookAt(EntityAnchorArgument.Anchor.EYES, prevSegment.position().add(0,0.5,0));
                }
            }
        }
    }

    private void updateBossHealth() {
        float totalHealth = 0.0f;
        float totalMaxHealth = 0.0f;
        totalHealth += getHealth();
        totalMaxHealth += getMaxHealth();
        for (CondemnedGuardianSegment segment : this.segments) {
            if (segment != null && segment.isAlive()) {
                totalHealth += segment.getHealth();
                totalMaxHealth += segment.getMaxHealth();
            }
        }

        float progress = totalMaxHealth > 0 ? totalHealth / totalMaxHealth : 0.0f;
        this.entityData.set(DATA_HEALTH_SYNC, totalHealth);
        this.entityData.set(DATA_BOSS_PROGRESS, progress);
        this.bossEvent.setProgress(progress);
    }

    private void handleAttacks() {
        LivingEntity target = getTarget();
        if (target != null && isWithinMeleeAttackRange(target) && this.tickCount % 20 == 0) {
            doHurtTarget(target);
            playSound(SoundEvents.GUARDIAN_ATTACK, 1.0F, 1.0F);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData groupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (level.getLevel() instanceof ServerLevel serverLevel) {
            spawnSegments(serverLevel);
        }
        return groupData;
    }

    private void spawnSegments(ServerLevel serverLevel) {
        Vec3 lookDir = getLookAngle().normalize();
        Vec3 startPos = position();
        float waveOffset = 0.0F;
        List<CondemnedGuardianSegment> newSegments = new ArrayList<>();

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            CondemnedGuardianSegment segment = new CondemnedGuardianSegment(WDEntities.CONDEMNED_GUARDIAN_SEGMENT.get(), level());
            double waveX = Math.sin(waveOffset + i * 0.3D) * WAVE_AMPLITUDE;
            double waveZ = Math.cos(waveOffset + i * 0.3D) * WAVE_AMPLITUDE;

            Vec3 spawnPos = startPos.add(
                    lookDir.x * -(i + 1) * SEGMENT_SPACING + waveX,
                    lookDir.y * -(i + 1) * SEGMENT_SPACING,
                    lookDir.z * -(i + 1) * SEGMENT_SPACING + waveZ
            );

            segment.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, getYRot(), 0.0F);
            segment.setHeadEntityId(getId());
            segment.setSegmentIndex(i + 1);

            serverLevel.addFreshEntity(segment);
            newSegments.add(segment);
        }

        for (int i = 0; i < newSegments.size(); i++) {
            CondemnedGuardianSegment segment = newSegments.get(i);
            if (i == 0) {
                segment.setFollowEntityId(getId());
                segment.setPrevEntityId(getId());
            } else {
                CondemnedGuardianSegment prevSegment = newSegments.get(i - 1);
                segment.setFollowEntityId(prevSegment.getId());
                segment.setPrevEntityId(prevSegment.getId());
            }

            if (i == newSegments.size() - 1) {
                segment.setNextEntityId(-1);
            } else {
                CondemnedGuardianSegment nextSegment = newSegments.get(i + 1);
                segment.setNextEntityId(nextSegment.getId());
            }
        }

        this.segments.addAll(newSegments);
    }

    public void onSegmentDeath(CondemnedGuardianSegment deadSegment) {
        this.segments.remove(deadSegment);
        if (!this.segments.isEmpty()) {
            CondemnedGuardianSegment newTail = this.segments.get(this.segments.size() - 1);
            newTail.setNextEntityId(-1);
        }
    }

    @Override
    public void die(DamageSource source) {
        for (CondemnedGuardianSegment segment : new ArrayList<>(this.segments)) {
            if (segment.isAlive()) {
                segment.remove(RemovalReason.DISCARDED);
            }
        }
        this.segments.clear();

        this.bossEvent.setVisible(false);
        super.die(source);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    public static boolean checkGuardianSpawnRules(EntityType<CondemnedGuardian> entityType, ServerLevelAccessor level,
                                                  SpawnGroupData spawnData, BlockPos pos, ChunkGenerator generator) {
        return level.getFluidState(pos).is(FluidTags.WATER) &&
                level.getFluidState(pos.above()).is(FluidTags.WATER) &&
                level.getFluidState(pos.above(2)).is(FluidTags.WATER) &&
                level.getRandom().nextInt(15) == 0;
    }

    @Override
    public int getMaxAirSupply() {
        return 300;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (isInWater()) {
            setAirSupply(getMaxAirSupply());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 5, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static class CondemnedChargeAttackGoal extends Goal {

        private final CondemnedGuardian guardian;
        private LivingEntity target;

        private int phaseTimer;
        private int laserTime;

        private ChargePhase currentPhase = ChargePhase.IDLE;

        private static final int CIRCLE_DURATION = 160;
        private static final int LASER_WINDUP = 40;

        private double circleRadius = 20.0D;
        private Vec3 chargeDirection;

        private enum ChargePhase { //THIS is the thing where The guardian choose his attacks, like circle, charging ect, ect
            IDLE,
            CIRCLING,
            CHARGING,
            LASER
        }

        public CondemnedChargeAttackGoal(CondemnedGuardian guardian) {
            this.guardian = guardian;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.target = this.guardian.getTarget();
            return target != null
                    && this.target.isAlive()
                    && this.guardian.isInWater()
                    && this.guardian.distanceToSqr(this.target) < 400.0D
                    && this.guardian.hasLineOfSight(this.target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.guardian.isInWater()
                    && this.currentPhase != ChargePhase.IDLE;
        }

        @Override
        public void start() {
            this.currentPhase = ChargePhase.CIRCLING;
            this.phaseTimer = 0;
            this.circleRadius = 12.0D + this.guardian.getRandom().nextDouble() * 6.0D;
        }

        @Override
        public void tick() {
            if (target == null) {
                resetGoal();
                return;
            }

            switch (currentPhase) {
                case CIRCLING -> handleCirclingPhase();
                case CHARGING -> handleChargingPhase();
                case LASER -> handleLaserPhase();
            }
        }

        private void handleCirclingPhase() {
            this.phaseTimer++;

            Vec3 targetPos = this.target.position();
            Vec3 selfPos = this.guardian.position();

            Vec3 offset = selfPos.subtract(targetPos);
            double dist = offset.length();
            if (dist < 0.001) return;

            Vec3 radial = offset.normalize();
            Vec3 tangent = new Vec3(-radial.z, 0, radial.x);

            double distanceError = dist - this.circleRadius;
            double radialCorrection = Mth.clamp(distanceError * 0.15D, -0.3D, 0.3D);

            double yError = this.target.getY() - this.guardian.getY();
            double yCorrection = Mth.clamp(yError * 0.1D, -0.08D, 0.08D);

            Vec3 velocity = tangent.scale(0.1D)
                    .add(radial.scale(-radialCorrection))
                    .add(0, yCorrection, 0);

            this.guardian.setDeltaMovement(
                    this.guardian.getDeltaMovement().scale(0.8D).add(velocity)
            );

            Vec3 move = this.guardian.getDeltaMovement();
            if (move.lengthSqr() > 0.01) {
                float yaw = (float)(Math.atan2(move.z, move.x) * (180F / Math.PI)) - 90F;
                this.guardian.setYRot(yaw);
                this.guardian.yBodyRot = yaw;
            }

            if (phaseTimer >= CIRCLE_DURATION) {
                this.phaseTimer = 0;
                if (this.guardian.getRandom().nextFloat() < 0.35F && dist > 8.0D) {
                    this.currentPhase = ChargePhase.LASER;
                    this.guardian.isShootingLaser = true;
                    this.laserTime = -10;
                } else {
                    this.currentPhase = ChargePhase.CHARGING;
                }
            }
        }

        private void handleChargingPhase() {
            this.phaseTimer++;

            if (this.phaseTimer == 1) {
                this.guardian.playSound(SoundEvents.GUARDIAN_ATTACK, 2.0F, 0.7F);

                this.chargeDirection = this.target.position()
                        .subtract(this.guardian.position())
                        .normalize();

                this.guardian.setDeltaMovement(this.chargeDirection.scale(3.8D));
            }

            this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().scale(0.98D));
            damageConeAttack();
        }

        private void handleLaserPhase() {
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(target, 90.0F, 90.0F);

            this.laserTime++;

            if (this.laserTime == 0) {
                this.guardian.playSound(SoundEvents.GUARDIAN_ATTACK, 2.0F, 0.7F);
            }

            if (this.laserTime >= LASER_WINDUP) {
                this.target.hurt(
                        this.guardian.damageSources().indirectMagic(this.guardian, this.guardian),
                        10.0F
                );
                this.guardian.isShootingLaser = false;
                this.currentPhase = ChargePhase.CIRCLING;
                this.phaseTimer = 0;
                this.circleRadius = 12.0D + guardian.getRandom().nextDouble() * 6.0D;
            }
        }

        private void damageConeAttack() {
            Vec3 headPos = this.guardian.position();
            Vec3 dir = this.chargeDirection != null ? this.chargeDirection : this.guardian.getLookAngle();

            AABB box = new AABB(
                    headPos.x - 6, headPos.y - 3, headPos.z - 6,
                    headPos.x + 6, headPos.y + 3, headPos.z + 6
            );

            List<LivingEntity> entities = this.guardian.level().getEntitiesOfClass(
                    LivingEntity.class,
                    box,
                    e -> e != this.guardian
                            && !(e instanceof CondemnedGuardian)
                            && !(e instanceof CondemnedGuardianSegment)
                            && hasConeLineOfSight(headPos, dir, e.position())
            );

            if (entities.isEmpty()) return;

            LivingEntity entity = entities.get(0);

            entity.hurt(this.guardian.damageSources().mobAttack(this.guardian), 14.0F);
            entity.knockback(1.5D,
                    entity.getX() - headPos.x,
                    entity.getZ() - headPos.z
            );

            this.currentPhase = ChargePhase.CIRCLING;
            this.phaseTimer = 0;
        }

        private boolean hasConeLineOfSight(Vec3 origin, Vec3 direction, Vec3 targetPos) {
            return direction.dot(targetPos.subtract(origin).normalize()) > 0.7D;
        }

        private void resetGoal() {
            currentPhase = ChargePhase.IDLE;
            phaseTimer = 0;
            laserTime = 0;
        }

        @Override
        public void stop() {
            resetGoal();
        }
    }
    private static class WaterMovementGoal extends Goal {
        private final CondemnedGuardian guardian;
        private int idleTime;

        public WaterMovementGoal(CondemnedGuardian guardian) {
            this.guardian = guardian;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.guardian.isInWater();
        }

        @Override
        public void tick() {
            this.idleTime++;

            if (this.idleTime > 40 && this.guardian.getRandom().nextInt(20) == 0) {
                float randomYaw = this.guardian.getYRot() + (this.guardian.getRandom().nextFloat() - 0.5F) * 60.0F;
                this.guardian.setYRot(randomYaw);
                this.guardian.yBodyRot = randomYaw;
                this.guardian.yHeadRot = randomYaw;

                if (this.guardian.getRandom().nextBoolean()) {
                    this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().add(0.0D, 0.03D, 0.0D));
                }

                this.idleTime = 0;
            }
        }
    }
}