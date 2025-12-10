package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class PrimalCreeper extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
    private static final String PRIMAL_CREEPER_CONTROLLER = "primal_creeper_controller";
    private static final String PRIMAL_CREEPER_ATTACK_CONTROLLER = "primal_creeper_attack_controller";

    private final AnimationController<PrimalCreeper> mainController = new AnimationController<>(this, PRIMAL_CREEPER_CONTROLLER, 5, state ->
            state.setAndContinue(idleAnim)
    )
            .triggerableAnim(walk, walkAnim)
            .triggerableAnim(run, runAnim)
            .triggerableAnim(dash, dashAnim);
    private final AnimationController<PrimalCreeper> attackController = new AnimationController<>(this, PRIMAL_CREEPER_ATTACK_CONTROLLER, 5, state ->
            PlayState.CONTINUE
    )
            .triggerableAnim(throwing, throwingAnim)
            .triggerableAnim(spread, spreadAnim)
            .triggerableAnim(slow_spread, slowSpreadAnim);
    private static final String
            idle = "animation.model.idle", //Idle
            walk = "animation.model.walk", //Walking
            run = "animation.model.run", //Running
            dash = "animation.model.dash", // Dashing (not really a dash but  it's like more than running so eh ig it's a dash)
            throwing = "animation.model.throw", //throwing a tnt
            spread = "animation.model.spread", //Spreading 3 tnt
            slow_spread = "animation.model.long_spread"; // spreading 3 tnt , 3 times

    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            runAnim = RawAnimation.begin().thenLoop(run),
            dashAnim = RawAnimation.begin().thenLoop(dash),
            throwingAnim = RawAnimation.begin().thenPlay(throwing),
            spreadAnim = RawAnimation.begin().thenPlay(spread),
            slowSpreadAnim = RawAnimation.begin().thenPlay(slow_spread);

    private static final double NORMAL_SPEED_MOD = 1.0D;
    private static final double RETREAT_SPEED_MOD = 3.0D;
    private static final double PANIC_SPEED_MOD = 4.5D;
    private static final double WALK_THRESHOLD = 0.05D;
    private static final double RUN_THRESHOLD = 0.2D;
    private static final double DASH_THRESHOLD = 0.35D;
    private static final float MIN_DISTANCE = 6.0f;
    private static final float MAX_DISTANCE = 10.0f;
    private static final float DANGER_DISTANCE = 4.0f;

    private static final EntityDataAccessor<Integer> CURRENT_ACTION =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COOLDOWN =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.INT);

    public PrimalCreeper(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Ocelot.class, 8.0F, 1.2, 1.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Cat.class, 8.0F, 1.2, 1.5));
        this.goalSelector.addGoal(1, new PrimalCreeperEmergencyRetreatGoal(this));

        this.goalSelector.addGoal(2, new PrimalCreeperCombatMovementGoal(this));
        this.goalSelector.addGoal(3, new ThoughtSelector(this));
        this.goalSelector.addGoal(4, new ThrowTNTGoal(this));
        this.goalSelector.addGoal(4, new TNTSpreadGoal(this));
        this.goalSelector.addGoal(4, new LongTNTSpreadGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.11f)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.STEP_HEIGHT, 7D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(mainController);
        controllerRegistrar.add(attackController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_ACTION, 0);
        builder.define(COOLDOWN, 0);
    }

    public int getCurrentAction() {
        return this.entityData.get(CURRENT_ACTION);
    }

    public void setCurrentAction(int actionID) {
        this.entityData.set(CURRENT_ACTION, actionID);
    }

    public int getCooldown() {
        return this.entityData.get(COOLDOWN);
    }

    public void setCooldown(int cooldown) {
        this.entityData.set(COOLDOWN, cooldown);
    }

    public void decreaseCooldown(int amount) {
        this.entityData.set(COOLDOWN, Math.max(this.getCooldown() - amount, 0));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void tick() {
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);

        if (this.getTarget() != null) {
            if (this.getTarget() instanceof Player player) {
                if (!player.isCreative()) {
                    if (this.getCooldown() > 0 && this.getCurrentAction() == 0) {
                        this.decreaseCooldown(1);
                    }
                } else {
                    this.setTarget(null);
                }
            } else {
                if (this.getCooldown() > 0 && this.getCurrentAction() == 0) {
                    this.decreaseCooldown(1);
                }
            }
        }

        double currentSpeed = getSQRTAbsVXZ();
        handleMovementAnimations(currentSpeed);

        super.tick();

        LivingEntity target = this.getTarget();
        if (target != null) {
            this.getLookControl().setLookAt(target, 180.0F, 180.0F);
        }
    }

    private void handleMovementAnimations(double currentSpeed) {
        //System.out.println(currentSpeed); That was just to check the speed and all ,no worries !
        if (currentSpeed > RUN_THRESHOLD) {
            this.triggerAnim(PRIMAL_CREEPER_CONTROLLER, dash);
        } else if (currentSpeed > 0.1) {
            this.triggerAnim(PRIMAL_CREEPER_CONTROLLER, run);
        } else if (currentSpeed > 0.0F) {
            this.triggerAnim(PRIMAL_CREEPER_CONTROLLER, walk);
        } else {
            this.stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, walk);
            this.stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, run);
            this.stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, dash);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public double getSQRTAbsVXZ() {
        return Math.sqrt(getDeltaMovement().x * getDeltaMovement().x + getDeltaMovement().z * getDeltaMovement().z);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!source.isDirect()) {
            amount *= 0.75f;
        }
        return super.hurt(source, amount);
    }

    protected static class PrimalCreeperCombatMovementGoal extends Goal {
        private final PrimalCreeper entity;
        private int lastRepositionTick = 0;
        private Vec3 lastPosition = null;
        private static final int MIN_REPOSITION_DELAY = 60;
        private static final int MAX_REPOSITION_DELAY = 120;

        public PrimalCreeperCombatMovementGoal(PrimalCreeper entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null && entity.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            double distSqr = entity.distanceToSqr(target);

            if (distSqr < (entity.DANGER_DISTANCE * entity.DANGER_DISTANCE)) {
                moveAwayFromTarget(target, entity.PANIC_SPEED_MOD);
            } else if (distSqr < (entity.MIN_DISTANCE * entity.MIN_DISTANCE)) {
                moveAwayFromTarget(target, entity.RETREAT_SPEED_MOD);
            } else if (distSqr > (entity.MAX_DISTANCE * entity.MAX_DISTANCE)) {
                moveToTarget(target, entity.NORMAL_SPEED_MOD);
            } else {
                entity.getNavigation().stop();

                if (shouldReposition()) {
                    findNewPosition(target);
                    lastRepositionTick = entity.tickCount;
                }
            }
        }

        private boolean shouldReposition() {
            return entity.tickCount - lastRepositionTick > MIN_REPOSITION_DELAY &&
                    (entity.getRandom().nextInt(MAX_REPOSITION_DELAY - MIN_REPOSITION_DELAY) == 0 ||
                            lastPosition == null ||
                            entity.distanceToSqr(lastPosition) < 1.0);
        }

        private void findNewPosition(LivingEntity target) {
            double angle = entity.getRandom().nextDouble() * 2 * Math.PI;
            double distance = entity.MIN_DISTANCE + entity.getRandom().nextDouble() * (entity.MAX_DISTANCE - entity.MIN_DISTANCE);

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            Vec3 targetPos = target.position();
            Vec3 newPos = new Vec3(
                    targetPos.x + offsetX,
                    targetPos.y,
                    targetPos.z + offsetZ
            );

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(newPos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, entity.NORMAL_SPEED_MOD);
                lastPosition = newPos;
            }
        }

        private void moveAwayFromTarget(LivingEntity target, double speedModifier) {
            Vec3 away = entity.position().subtract(target.position()).normalize();
            Vec3 newPos = entity.position().add(away.scale(2.0));

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(newPos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, speedModifier);
            }
        }

        private void moveToTarget(LivingEntity target, double speedModifier) {
            entity.getNavigation().moveTo(target, speedModifier);
        }
    }

    protected static class PrimalCreeperEmergencyRetreatGoal extends Goal {
        private final PrimalCreeper entity;
        private int retreatDuration = 0;
        private static final int MAX_RETREAT_DURATION = 40;

        public PrimalCreeperEmergencyRetreatGoal(PrimalCreeper entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return target != null &&
                    entity.distanceToSqr(target) < (entity.DANGER_DISTANCE * entity.DANGER_DISTANCE) &&
                    retreatDuration <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return retreatDuration > 0;
        }

        @Override
        public void start() {
            retreatDuration = MAX_RETREAT_DURATION;
            findEscapePosition();
            entity.triggerAnim(entity.PRIMAL_CREEPER_CONTROLLER, entity.dash);
        }

        @Override
        public void tick() {
            retreatDuration--;
            if (retreatDuration <= 0) {
                entity.getNavigation().stop();
                entity.stopTriggeredAnim(entity.PRIMAL_CREEPER_CONTROLLER, entity.dash);
            }
        }

        @Override
        public void stop() {
            retreatDuration = 0;
            entity.getNavigation().stop();
            entity.stopTriggeredAnim(entity.PRIMAL_CREEPER_CONTROLLER, entity.dash);
        }

        private void findEscapePosition() {
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            Vec3 away = entity.position().subtract(target.position()).normalize();
            Vec3 escapePos = entity.position().add(away.scale(10.0));

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(escapePos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(escapePos.x, escapePos.y, escapePos.z, entity.PANIC_SPEED_MOD);
            } else {
                for (int i = 0; i < 10; i++) {
                    double angle = entity.getRandom().nextDouble() * 2 * Math.PI;
                    Vec3 randomDir = new Vec3(Math.cos(angle), 0, Math.sin(angle));
                    Vec3 candidate = entity.position().add(randomDir.scale(8.0 + entity.getRandom().nextDouble() * 4.0));

                    if (entity.level().noCollision(entity, entity.getBoundingBox().move(candidate.subtract(entity.position())))) {
                        entity.getNavigation().moveTo(candidate.x, candidate.y, candidate.z, entity.PANIC_SPEED_MOD);
                        return;
                    }
                }
            }
        }
    }

    protected class ThoughtSelector extends Goal {
        private final PrimalCreeper entity;
        private static final int[] ATTACK_WEIGHTS = {50, 30, 20};

        public ThoughtSelector(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.isAlive()
                    && entity.getCurrentAction() == 0
                    && entity.getTarget() != null
                    && entity.getCooldown() <= 0
                    && entity.distanceToSqr(entity.getTarget()) <= (entity.MAX_DISTANCE * entity.MAX_DISTANCE);
        }

        @Override
        public void start() {
            super.start();

            int totalWeight = 0;
            for (int weight : ATTACK_WEIGHTS) totalWeight += weight;

            int rand = entity.getRandom().nextInt(totalWeight);
            int cumulative = 0;
            int chosenAttack = 1;

            for (int i = 0; i < ATTACK_WEIGHTS.length; i++) {
                cumulative += ATTACK_WEIGHTS[i];
                if (rand < cumulative) {
                    chosenAttack = i + 1;
                    break;
                }
            }

            entity.setCurrentAction(chosenAttack);
        }
    }

    protected class ThrowTNTGoal extends Goal {
        public static final int ID = 1;
        public static final int WINDUP_START = 30;
        public static final int THROW_TICK = 40;
        public static final int MAX_TIME = 45;
        public static final int POST_THROW_DELAY = 20;

        private int tickCount = 0;
        private final PrimalCreeper entity;
        private boolean hasThrown = false;
        private boolean isPostThrow = false;

        public ThrowTNTGoal(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null
                    && entity.getCurrentAction() == ID;
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarget() != null
                    && (tickCount < MAX_TIME || isPostThrow);
        }

        @Override
        public void start() {
            super.start();
            tickCount = 0;
            hasThrown = false;
            isPostThrow = false;
            entity.setCooldown(340);
            entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            tickCount++;

            if (tickCount == WINDUP_START) {
                entity.triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, throwing);
                spawnChargeParticles();
            }
            else if (tickCount == THROW_TICK && !hasThrown) {
                throwTNT(target);
                hasThrown = true;
                isPostThrow = true;
            }
            else if (hasThrown && tickCount >= THROW_TICK + POST_THROW_DELAY) {
                isPostThrow = false;
                if (entity.getRandom().nextBoolean()) {
                    entity.getNavigation().moveTo(
                            target.getX() + (entity.getRandom().nextBoolean() ? 5 : -5),
                            target.getY(),
                            target.getZ() + (entity.getRandom().nextBoolean() ? 5 : -5),
                            entity.NORMAL_SPEED_MOD
                    );
                }
            }
        }

        private void spawnChargeParticles() {
            for (int i = 0; i < 15; i++) {
                double offsetX = (entity.getRandom().nextDouble() - 0.5) * 1.5;
                double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 1.5;
                entity.level().addParticle(ParticleTypes.SMOKE,
                        entity.getX() + offsetX,
                        entity.getY() + 1.5,
                        entity.getZ() + offsetZ,
                        0, 0.2, 0);
            }
        }

        private void throwTNT(LivingEntity target) {
            PrimedTnt tnt = new PrimedTnt(
                    entity.level(),
                    entity.getX(),
                    entity.getY() + 1.5,
                    entity.getZ(),
                    entity
            );

            Vec3 throwVector = target.position().subtract(entity.position())
                    .normalize()
                    .add(0, 0.3, 0);

            tnt.setDeltaMovement(throwVector.scale((0.075f * target.position().distanceTo(entity.position())))); // Even faster throw
            tnt.setFuse(80);
            entity.level().addFreshEntity(tnt);

            entity.setDeltaMovement(entity.getDeltaMovement().subtract(throwVector.scale(0.25)));
            entity.playSound(SoundEvents.CREEPER_PRIMED, 1.3F, 0.7F);
        }

        @Override
        public void stop() {
            entity.setCurrentAction(0);
            entity.getNavigation().stop();
            super.stop();
        }
    }

    protected class TNTSpreadGoal extends Goal {
        public static final int ID = 2;
        public static final int WINDUP_START = 30;
        public static final int[] THROW_TICKS = {40, 50, 60};
        public static final float[] ANGLES = {-15.0f, 0.0f, 15.0f};
        public static final int MAX_TIME = 130;

        private int tickCount = 0;
        private final PrimalCreeper entity;
        private int nextThrowIndex = 0;
        private boolean[] hasThrown = new boolean[3];

        public TNTSpreadGoal(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null
                    && entity.getCurrentAction() == ID;
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarget() != null
                    && tickCount < MAX_TIME;
        }

        @Override
        public void start() {
            super.start();
            tickCount = 0;
            nextThrowIndex = 0;
            hasThrown = new boolean[3];
            entity.setCooldown(400);
            entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            tickCount++;

            if (tickCount == WINDUP_START) {
                entity.triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, spread);
                spawnChargeParticles();
            }

            if (nextThrowIndex < THROW_TICKS.length && tickCount == THROW_TICKS[nextThrowIndex]) {
                throwTNTWithOffset(target, ANGLES[nextThrowIndex]);
                hasThrown[nextThrowIndex] = true;
                nextThrowIndex++;
            }
        }

        private void spawnChargeParticles() {
            for (int i = 0; i < 25; i++) {
                double offsetX = (entity.getRandom().nextDouble() - 0.5) * 2.0;
                double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 2.0;
                entity.level().addParticle(ParticleTypes.SMOKE,
                        entity.getX() + offsetX,
                        entity.getY() + 1.5,
                        entity.getZ() + offsetZ,
                        0, 0.3, 0);
            }
        }

        private void throwTNTWithOffset(LivingEntity target, float angleOffset) {
            double d0 = target.getX() - entity.getX();
            double d2 = target.getZ() - entity.getZ();
            double baseAngle = Math.atan2(d2, d0);
            double offsetAngle = baseAngle + Math.toRadians(angleOffset);

            Vec3 direction = new Vec3(
                    Math.cos(offsetAngle),
                    0,
                    Math.sin(offsetAngle)
            ).normalize();
            Vec3 throwVector = direction.add(0, 0.4, 0);

            PrimedTnt tnt = new PrimedTnt(
                    entity.level(),
                    entity.getX(),
                    entity.getY() + 1.5,
                    entity.getZ(),
                    entity
            );

            tnt.setDeltaMovement(throwVector.scale((0.075f * target.position().distanceTo(entity.position()))));
            tnt.setFuse(80);
            entity.level().addFreshEntity(tnt);
        }

        @Override
        public void stop() {
            entity.setCurrentAction(0);
            entity.getNavigation().stop();
            super.stop();
        }
    }

    protected class LongTNTSpreadGoal extends Goal {
        public static final int ID = 3;
        public static final int WINDUP_START = 30;
        public static final int[] SET_START_TICKS = {40, 80, 120};
        public static final int[] THROW_DELAYS = {0, 10, 20};
        public static final float[] ANGLES = {-15.0f, 0.0f, 15.0f};
        public static final int MAX_TIME = 150;

        private int tickCount = 0;
        private final PrimalCreeper entity;
        private int currentSet = 0;
        private boolean[] hasThrown = new boolean[9];

        public LongTNTSpreadGoal(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null
                    && entity.getCurrentAction() == ID;
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarget() != null
                    && tickCount < MAX_TIME;
        }

        @Override
        public void start() {
            super.start();
            tickCount = 0;
            currentSet = 0;
            hasThrown = new boolean[9];
            entity.setCooldown(600);
            entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            tickCount++;

            if (tickCount == WINDUP_START) {
                entity.triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, slow_spread);
                spawnChargeParticles();
            }

            for (int set = 0; set < 3; set++) {
                if (tickCount >= SET_START_TICKS[set]) {
                    for (int i = 0; i < 3; i++) {
                        int throwTick = SET_START_TICKS[set] + THROW_DELAYS[i];
                        int throwIndex = set * 3 + i;

                        if (tickCount == throwTick && !hasThrown[throwIndex]) {
                            throwTNTWithOffset(target, ANGLES[i]);
                            hasThrown[throwIndex] = true;
                        }
                    }
                }
            }
        }

        private void spawnChargeParticles() {
            for (int i = 0; i < 35; i++) {
                double offsetX = (entity.getRandom().nextDouble() - 0.5) * 2.5;
                double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 2.5;
                entity.level().addParticle(ParticleTypes.SMOKE,
                        entity.getX() + offsetX,
                        entity.getY() + 1.7,
                        entity.getZ() + offsetZ,
                        0, 0.4, 0);
            }
        }

        private void throwTNTWithOffset(LivingEntity target, float angleOffset) {
            double d0 = target.getX() - entity.getX();
            double d2 = target.getZ() - entity.getZ();
            double baseAngle = Math.atan2(d2, d0);
            double offsetAngle = baseAngle + Math.toRadians(angleOffset);

            Vec3 direction = new Vec3(
                    Math.cos(offsetAngle),
                    0,
                    Math.sin(offsetAngle)
            ).normalize();

            Vec3 throwVector = direction.add(0, 0.5, 0);
            PrimedTnt tnt = new PrimedTnt(
                    entity.level(),
                    entity.getX(),
                    entity.getY() + 1.6,
                    entity.getZ(),
                    entity
            );
            tnt.setDeltaMovement(throwVector.scale((0.075f * target.position().distanceTo(entity.position()))));
            tnt.setFuse(80);
            entity.level().addFreshEntity(tnt);
        }

        @Override
        public void stop() {
            entity.setCurrentAction(0);
            entity.getNavigation().stop();
            super.stop();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new PrimalCreeperPathNavigation(this, level);
    }

    private static class PrimalCreeperPathNavigation extends GroundPathNavigation {
        public PrimalCreeperPathNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        public void tick() {
            super.tick();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.AZALEA_LEAVES_BREAK, 0.2F, 1.2F);
    }
}