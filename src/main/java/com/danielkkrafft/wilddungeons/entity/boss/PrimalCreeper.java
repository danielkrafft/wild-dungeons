
package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.PrimedDenseTnt;
import com.danielkkrafft.wilddungeons.registry.WDItems;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;

public class PrimalCreeper extends WDBoss implements GeoEntity {
    private static final String PRIMAL_CREEPER_CONTROLLER = "primal_creeper_controller";
    private static final String PRIMAL_CREEPER_ATTACK_CONTROLLER = "primal_creeper_attack_controller";
    private static final String
            idle = "animation.model.idle",
            summon = "animation.model.summon",
            walk = "animation.model.walk",
            run = "animation.model.run",
            dash = "animation.model.dash",
            throwing = "animation.model.throw",
            spread = "animation.model.spread",
            slow_spread = "animation.model.long_spread";

    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            summonAnim = RawAnimation.begin().thenPlay(summon),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            runAnim = RawAnimation.begin().thenLoop(run),
            dashAnim = RawAnimation.begin().thenLoop(dash),
            throwingAnim = RawAnimation.begin().thenPlay(throwing),
            spreadAnim = RawAnimation.begin().thenPlay(spread),
            slowSpreadAnim = RawAnimation.begin().thenPlay(slow_spread);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, PRIMAL_CREEPER_CONTROLLER, 5, state ->
                state.setAndContinue(idleAnim))
                .triggerableAnim("walk", walkAnim)
                .triggerableAnim("summon", summonAnim)
                .triggerableAnim("run", runAnim)
                .triggerableAnim("dash", dashAnim));

        registrar.add(new AnimationController<>(this, PRIMAL_CREEPER_ATTACK_CONTROLLER, 5,
                state -> PlayState.CONTINUE)
                .triggerableAnim("throwing", throwingAnim)
                .triggerableAnim("spread", spreadAnim)
                .triggerableAnim("slow_spread", slowSpreadAnim));
    }

    private static final double NORMAL_SPEED = 1.0;
    private static final double RETREAT_SPEED = 3.0;
    private static final double PANIC_SPEED = 4.5;
    private static final double RUN_THRESHOLD = 0.1;
    private static final double DASH_THRESHOLD = 0.2;
    private static final float MIN_DISTANCE = 6.0f;
    private static final float MAX_DISTANCE = 10.0f;
    private static final float DANGER_DISTANCE = 4.0f;

    private static final int THROW_TNT = 1;
    private static final int TNT_SPREAD = 2;
    private static final int LONG_TNT_SPREAD = 3;

    private static final EntityDataAccessor<Integer> ACTION =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COOLDOWN =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHINY =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.BOOLEAN);

    public PrimalCreeper(EntityType<? extends WDBoss> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
        this.xpReward = 100;
        this.summonTicks = 45;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Ocelot.class, 8.0F, 1.2, 1.5));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Cat.class, 8.0F, 1.2, 1.5));
        goalSelector.addGoal(1, new EmergencyRetreatGoal(this));

        goalSelector.addGoal(2, new ThoughtSelector(this));
        goalSelector.addGoal(3, new ThrowTNTGoal(this));
        goalSelector.addGoal(3, new TNTSpreadGoal(this));
        goalSelector.addGoal(3, new LongTNTSpreadGoal(this));

        goalSelector.addGoal(4, new CombatMovementGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 240.0)
                .add(Attributes.MOVEMENT_SPEED, 0.11)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.STEP_HEIGHT, 7);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTION, 0);
        builder.define(COOLDOWN, 0);
        builder.define(SHINY, false);
    }

    @Override
    protected int getBossAction() {
        return entityData.get(ACTION);
    }

    @Override
    protected void setBossAction(int action) {
        entityData.set(ACTION, action);
    }

    @Override
    protected int getBossCooldown() {
        return entityData.get(COOLDOWN);
    }

    @Override
    protected void setBossCooldown(int ticks) {
        entityData.set(COOLDOWN, ticks);
    }

    private void decreaseCooldown(int amount) {
        entityData.set(COOLDOWN, Math.max(getBossCooldown() - amount, 0));
    }

    @Override
    public void tick() {
        updateBossBar();

        LivingEntity target = getTarget();
        if (target != null) {
            if (target instanceof Player player && player.isCreative()) {
                setTarget(null);
            } else if (getBossCooldown() > 0 && getBossAction() == 0) {
                decreaseCooldown(1);
            }

            getLookControl().setLookAt(target, 180.0F, 180.0F);
        }

        handleMovementAnimations(getCurrentSpeed());
        super.tick();
    }

    private void handleMovementAnimations(double speed) {
        if (speed > DASH_THRESHOLD) {
            triggerAnim(PRIMAL_CREEPER_CONTROLLER, "dash");
        } else if (speed > RUN_THRESHOLD) {
            triggerAnim(PRIMAL_CREEPER_CONTROLLER, "run");
        } else if (speed > 0) {
            triggerAnim(PRIMAL_CREEPER_CONTROLLER, "walk");
        } else {
            stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, "walk");
            stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, "run");
            stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, "dash");
        }
    }

    private double getCurrentSpeed() {
        Vec3 delta = getDeltaMovement();
        return Math.sqrt(delta.x * delta.x + delta.z * delta.z);
    }

    private Vec3 throwTNTWithOffset(LivingEntity target, float angleOffset, float lengthOffset, float arcBoost) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        double baseAngle = Math.atan2(dz, dx);
        double offsetAngle = baseAngle + Math.toRadians(angleOffset);

        Vec3 direction = new Vec3(Math.cos(offsetAngle), 0, Math.sin(offsetAngle)).normalize();
        Vec3 throwVector = direction.add(0, 0.4 + arcBoost, 0);

        double dist = target.position().distanceTo(position());
        int fuseTicks = (int) (dist * 5);
        Vec3 vel = throwVector.scale(0.075f * (dist + lengthOffset));

        if (isShiny()) {
            PrimedDenseTnt tnt = new PrimedDenseTnt(level(), getX(), getY() + 1.5, getZ(), this);
            tnt.setFuse(fuseTicks);
            tnt.setDeltaMovement(vel);
            level().addFreshEntity(tnt);
        } else {
            PrimedTnt tnt = new PrimedTnt(level(), getX(), getY() + 1.5, getZ(), this);
            tnt.setFuse(fuseTicks);
            tnt.setDeltaMovement(vel);
            level().addFreshEntity(tnt);
        }

        return throwVector;
    }

    private void spawnChargeParticles(int count, double radius, double y, double yVel) {
        for (int i = 0; i < count; i++) {
            double ox = (getRandom().nextDouble() - 0.5) * radius;
            double oz = (getRandom().nextDouble() - 0.5) * radius;
            level().addParticle(ParticleTypes.SMOKE, getX() + ox, getY() + y, getZ() + oz, 0, yVel, 0);
        }
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        UtilityMethods.sendParticles((ServerLevel) level(), ParticleTypes.EXPLOSION_EMITTER,
                true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles((ServerLevel) level(), ParticleTypes.SMOKE,
                true, 800, pos.x, pos.y, pos.z, 4, 4, 4, 0.08f);
    }

    @Override
    protected void summonAnimation() {
        triggerAnim(PRIMAL_CREEPER_CONTROLLER, "summon");
    }

    @Override
    protected boolean isImmuneToDamageType(DamageSource source) {
        return source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION);
    }

    public boolean isShiny() { return entityData.get(SHINY); }
    private void rollShiny() { entityData.set(SHINY, getRandom().nextFloat() < 0.05f); }

    @Override
    protected void dropAllDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source) {
        super.dropAllDeathLoot(level, source);
        if (!isShiny()) return;
        spawnAtLocation(new ItemStack(WDItems.DETONITE_CRYSTAL.get(), 32 + random.nextInt(65)));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (!level.isClientSide()) {
            rollShiny();
            this.xpReward = isShiny() ? 500 : 100;
        }
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    private static final BossSounds SOUNDS = new BossSounds(
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_DEATH
    );

    @Override
    protected BossSounds bossSounds() {
        return SOUNDS;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        playSound(SoundEvents.AZALEA_LEAVES_BREAK, 0.2F, 1.2F);
    }

    private static class ThoughtSelector extends Goal {
        private final PrimalCreeper creeper;
        private static final int[] WEIGHTS = {50, 30, 20}; // Throw, Spread, Long

        public ThoughtSelector(PrimalCreeper creeper) {
            this.creeper = creeper;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = creeper.getTarget();
            return target != null && target.isAlive()
                    && creeper.getBossAction() == 0
                    && creeper.getBossCooldown() <= 0
                    && creeper.distanceToSqr(target) <= (MAX_DISTANCE * MAX_DISTANCE);
        }

        @Override
        public void start() {
            int totalWeight = 0;
            for (int weight : WEIGHTS) totalWeight += weight;

            int rand = creeper.getRandom().nextInt(totalWeight);
            int cumulative = 0;

            for (int i = 0; i < WEIGHTS.length; i++) {
                cumulative += WEIGHTS[i];
                if (rand < cumulative) {
                    creeper.setBossAction(i + 1);
                    break;
                }
            }
        }
    }

    private class ThrowTNTGoal extends TimedActionGoal {
        private boolean hasThrown;
        private Pattern pattern;

        enum Pattern { SIMPLE, CROSS, T_PATTERN, CIRCLE }

        public ThrowTNTGoal(PrimalCreeper boss) {
            super(boss, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override protected int actionId() { return THROW_TNT; }
        @Override protected int maxTime() { return 65; }
        @Override protected int startCooldown() { return 120; }

        @Override
        protected void onStart(LivingEntity target) {
            hasThrown = false;
            pattern = Pattern.values()[random.nextInt(Pattern.values().length)];
        }

        @Override
        protected void onTick(LivingEntity target) {
            if (t == 30) {
                triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, "throwing");
                spawnChargeParticles(15, 0.5, 0.5, 0.2);
            } else if (t == 40 && !hasThrown) {
                executePattern(target);
                hasThrown = true;
            } else if (hasThrown && t >= 60 && getRandom().nextBoolean()) {
                getNavigation().moveTo(
                        target.getX() + (getRandom().nextBoolean() ? 5 : -5),
                        target.getY(),
                        target.getZ() + (getRandom().nextBoolean() ? 5 : -5),
                        NORMAL_SPEED
                );
            }
        }

        private void executePattern(LivingEntity target) {
            switch (pattern) {
                case SIMPLE -> {
                    double deltaY = target.position().subtract(position()).get(Direction.Axis.Y);
                    Vec3 throwVector = throwTNTWithOffset(target, 0f, 0f, (float) deltaY);
                    setDeltaMovement(getDeltaMovement().subtract(throwVector.scale(0.25)));
                    playSound(SoundEvents.CREEPER_PRIMED, 1.3F, 0.7F);
                }
                case CROSS -> {
                    throwTNTWithOffset(target, 0f, 0f, 0f);
                    throwTNTWithOffset(target, -90f, 0f, 0f);
                    throwTNTWithOffset(target, -180f, 0f, 0f);
                    throwTNTWithOffset(target, 90f, 0f, 0f);
                }
                case CIRCLE -> {
                    for (int i = 0; i < 24; i++) {
                        throwTNTWithOffset(target, (i < 13 ? -15f : 15f) * (i % 13), 0f, 0f);
                    }
                }
                case T_PATTERN -> {
                    throwTNTWithOffset(target, -15f, 0f, 0f);
                    throwTNTWithOffset(target, 0f, 0f, 0f);
                    throwTNTWithOffset(target, 15f, 0f, 0f);
                    throwTNTWithOffset(target, 0f, 1.5f, 0f);
                    throwTNTWithOffset(target, 0f, 3f, 0f);
                }
            }
        }
    }

    private class TNTSpreadGoal extends TimedActionGoal {
        private static final int[] THROW_TICKS = {32, 34, 36};
        private static final float[] ANGLES = {-15.0f, 0.0f, 15.0f};
        private int nextThrowIndex;

        public TNTSpreadGoal(PrimalCreeper boss) {
            super(boss, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override protected int actionId() { return TNT_SPREAD; }
        @Override protected int maxTime() { return 130; }
        @Override protected int startCooldown() { return 200; }

        @Override
        protected void onStart(LivingEntity target) {
            nextThrowIndex = 0;
        }

        @Override
        protected void onTick(LivingEntity target) {
            if (t == 30) {
                triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, "spread");
                spawnChargeParticles(25, 2.0, 1.5, 0.3);
            }

            if (nextThrowIndex < THROW_TICKS.length && t == THROW_TICKS[nextThrowIndex]) {
                throwTNTWithOffset(target, ANGLES[nextThrowIndex], 0f, 0f);
                nextThrowIndex++;
            }
        }
    }

    private class LongTNTSpreadGoal extends TimedActionGoal {
        private static final int[] SET_START_TICKS = {32, 40, 48};
        private static final int[] THROW_DELAYS = {0, 2, 4};
        private static final float[] ANGLES = {-15.0f, 0.0f, 15.0f};
        private boolean[] hasThrown;

        public LongTNTSpreadGoal(PrimalCreeper boss) {
            super(boss, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override protected int actionId() { return LONG_TNT_SPREAD; }
        @Override protected int maxTime() { return 150; }
        @Override protected int startCooldown() { return 180; }

        @Override
        protected void onStart(LivingEntity target) {
            hasThrown = new boolean[9];
        }

        @Override
        protected void onTick(LivingEntity target) {
            if (t == 30) {
                triggerAnim(PRIMAL_CREEPER_ATTACK_CONTROLLER, "slow_spread");
                spawnChargeParticles(35, 2.5, 1.7, 0.4);
            }

            for (int set = 0; set < 3; set++) {
                if (t >= SET_START_TICKS[set]) {
                    for (int i = 0; i < 3; i++) {
                        int throwTick = SET_START_TICKS[set] + THROW_DELAYS[i];
                        int throwIndex = set * 3 + i;

                        if (t == throwTick && !hasThrown[throwIndex]) {
                            throwTNTWithOffset(target, ANGLES[i], 0f, 0.1f);
                            hasThrown[throwIndex] = true;
                        }
                    }
                }
            }
        }
    }

    private static class CombatMovementGoal extends Goal {
        private final PrimalCreeper entity;
        private int lastRepositionTick;
        private Vec3 lastPosition;
        private static final int MIN_REPOSITION_DELAY = 60;
        private static final int MAX_REPOSITION_DELAY = 120;

        public CombatMovementGoal(PrimalCreeper entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            // Don't move if currently performing an attack
            return target != null && target.isAlive() && entity.getBossAction() == 0;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = entity.getTarget();
            // Stop moving if an attack starts
            return target != null && target.isAlive() && entity.getBossAction() == 0;
        }

        @Override
        public void tick() {
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            double distSqr = entity.distanceToSqr(target);

            if (distSqr < DANGER_DISTANCE * DANGER_DISTANCE) {
                moveAwayFrom(target, PANIC_SPEED);
            } else if (distSqr < MIN_DISTANCE * MIN_DISTANCE) {
                moveAwayFrom(target, RETREAT_SPEED);
            } else if (distSqr > MAX_DISTANCE * MAX_DISTANCE) {
                entity.getNavigation().moveTo(target, NORMAL_SPEED);
            } else {
                entity.getNavigation().stop();
                if (shouldReposition()) {
                    findNewPosition(target);
                    lastRepositionTick = entity.tickCount;
                }
            }
        }

        private boolean shouldReposition() {
            return entity.tickCount - lastRepositionTick > MIN_REPOSITION_DELAY
                    && (entity.getRandom().nextInt(MAX_REPOSITION_DELAY - MIN_REPOSITION_DELAY) == 0
                    || lastPosition == null
                    || entity.distanceToSqr(lastPosition) < 1.0);
        }

        private void findNewPosition(LivingEntity target) {
            double angle = entity.getRandom().nextDouble() * 2 * Math.PI;
            double distance = MIN_DISTANCE + entity.getRandom().nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);

            Vec3 newPos = target.position().add(
                    Math.cos(angle) * distance,
                    0,
                    Math.sin(angle) * distance
            );

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(newPos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, NORMAL_SPEED);
                lastPosition = newPos;
            }
        }

        private void moveAwayFrom(LivingEntity target, double speed) {
            Vec3 away = entity.position().subtract(target.position()).normalize();
            Vec3 newPos = entity.position().add(away.scale(2.0));

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(newPos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(newPos.x, newPos.y, newPos.z, speed);
            }
        }
    }

    private static class EmergencyRetreatGoal extends Goal {
        private final PrimalCreeper entity;
        private int retreatDuration;
        private static final int MAX_RETREAT_DURATION = 40;

        public EmergencyRetreatGoal(PrimalCreeper entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return target != null
                    && entity.distanceToSqr(target) < DANGER_DISTANCE * DANGER_DISTANCE
                    && retreatDuration <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return retreatDuration > 0;
        }

        @Override
        public void start() {
            retreatDuration = MAX_RETREAT_DURATION;
            findEscapePosition();
            entity.triggerAnim(PRIMAL_CREEPER_CONTROLLER, "dash");
        }

        @Override
        public void tick() {
            if (--retreatDuration <= 0) {
                entity.getNavigation().stop();
                entity.stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, "dash");
            }
        }

        @Override
        public void stop() {
            retreatDuration = 0;
            entity.getNavigation().stop();
            entity.stopTriggeredAnim(PRIMAL_CREEPER_CONTROLLER, "dash");
        }

        private void findEscapePosition() {
            LivingEntity target = entity.getTarget();
            if (target == null) return;

            Vec3 away = entity.position().subtract(target.position()).normalize();
            Vec3 escapePos = entity.position().add(away.scale(10.0));

            if (entity.level().noCollision(entity, entity.getBoundingBox().move(escapePos.subtract(entity.position())))) {
                entity.getNavigation().moveTo(escapePos.x, escapePos.y, escapePos.z, PANIC_SPEED);
            } else {
                for (int i = 0; i < 10; i++) {
                    double angle = entity.getRandom().nextDouble() * 2 * Math.PI;
                    Vec3 randomDir = new Vec3(Math.cos(angle), 0, Math.sin(angle));
                    Vec3 candidate = entity.position().add(randomDir.scale(8.0 + entity.getRandom().nextDouble() * 4.0));

                    if (entity.level().noCollision(entity, entity.getBoundingBox().move(candidate.subtract(entity.position())))) {
                        entity.getNavigation().moveTo(candidate.x, candidate.y, candidate.z, PANIC_SPEED);
                        return;
                    }
                }
            }
        }
    }
}