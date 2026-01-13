package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.BusinessGolem;
import com.danielkkrafft.wilddungeons.entity.BusinessVindicator;
import com.danielkkrafft.wilddungeons.entity.EmeraldProjectileEntity;
import com.danielkkrafft.wilddungeons.entity.EmeraldWisp;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;
import java.util.List;

public class BusinessCEO extends WDBoss {
    private static final EntityDataAccessor<Boolean> SECOND_PHASE_TRIGGERED = SynchedEntityData.defineId(BusinessCEO.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKS_INVULNERABLE = SynchedEntityData.defineId(BusinessCEO.class,EntityDataSerializers.INT);
    private static final String ceo_controller = "ceo_controller";
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

    private final AnimationController<BusinessCEO> mainController = new AnimationController<>(this, ceo_controller, 5,
            state -> state.setAndContinue(idleAnim))
            .triggerableAnim(idle, idleAnim)
            .triggerableAnim(stand_up, standUpAnim)
            .triggerableAnim(punch, punchAnim)
            .triggerableAnim(kick, kickAnim)
            .triggerableAnim(point, pointAnim)
            .triggerableAnim(ascend, ascendAnim)
            .triggerableAnim(dash, dashAnim);

    private static final int MELEE_COOLDOWN = 5;
    private static final int DASH_COOLDOWN = 20;
    private static final int ASCEND_COOLDOWN = 200; // 10 seconds
    private static final int POINT_COOLDOWN = 600; // 30 seconds
    private int lastMeleeGoalTick = -MELEE_COOLDOWN;
    private int lastDashGoalTick = -DASH_COOLDOWN;
    private int lastAscendGoalTick = -ASCEND_COOLDOWN;
    private int lastPointGoalTick = -POINT_COOLDOWN;

    public static Class[] FRIENDLIES = {Witch.class, BusinessCEO.class, BusinessGolem.class, AbstractIllager.class};

    public BusinessCEO(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
        this.summonTicks = 70;
        this.xpReward = 200;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBossSummonGoal(this));
        goalSelector.addGoal(1, new BusinessCEOPointGoal());
        goalSelector.addGoal(2, new BusinessCEOAscendGoal());
        goalSelector.addGoal(3, new BusinessCEODashGoal());
        goalSelector.addGoal(4, new BusinessCEOMeleeAttackGoal());

        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> {
            for (Class<?> friendly : FRIENDLIES) {
                if (friendly.isInstance(li)) {
                    return false;
                }
            }
            if (li instanceof EmeraldWisp) {
                return false;
            }
            return true;
        }));
        targetSelector.addGoal(1, new BusinessCEOTargetPlayerGoal());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 175)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 8)
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
        if (hp < 0.5f && !isSecondPhaseTriggered()) {
            setSecondPhaseTriggered(true);
        }
        updateBossBar();
        if (!level.isClientSide && !isDeadOrDying()) {
            if (isSecondPhaseTriggered() && tickCount % 10 == 0){
                UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.ANGRY_VILLAGER, true, 1, getX(), getY() + 1.5f, getZ(), 0.5f, 0.5f, 0.5f, 0.05f);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TICKS_INVULNERABLE, 0);
        builder.define(SECOND_PHASE_TRIGGERED, false);
    }

    @Override
    protected void saveBossData(CompoundTag compound) {
        compound.putBoolean("SP", isSecondPhaseTriggered());
    }

    @Override
    protected void loadBossData(CompoundTag compound) {
        if (compound.contains("SP")) {
            setSecondPhaseTriggered(compound.getBoolean("SP"));
        }
    }

    @Override
    protected void summonAnimation() {
        triggerAnim(ceo_controller, stand_up);
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        UtilityMethods.sendParticles((ServerLevel) level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles((ServerLevel) level(), ParticleTypes.ENCHANT, true, 200, pos.x, pos.y, pos.z, 1, 2, 1, 0.06f);
        UtilityMethods.sendParticles((ServerLevel) level(), ParticleTypes.COMPOSTER, true, 400, pos.x, pos.y, pos.z, 1, 2, 1, 0.08f);
        triggerAnim(ceo_controller, idle);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public void setSecondPhaseTriggered(boolean triggered) {
        this.entityData.set(SECOND_PHASE_TRIGGERED, triggered);
        if (triggered){
            bossEvent.setColor(BossEvent.BossBarColor.RED);
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.GREEN);
        }
    }

    public boolean isSecondPhaseTriggered() {
        return this.entityData.get(SECOND_PHASE_TRIGGERED);
    }

    @Override
    public boolean isInSummonPhase() {
        return getTicksInvulnerable() <= summonTicks;
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
        private boolean stopped = false;

        public BusinessCEOMeleeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.distanceToSqr(BusinessCEO.this.getTarget()) < 12 &&
                    !BusinessCEO.this.isInSummonPhase() &&
                    (BusinessCEO.this.tickCount - BusinessCEO.this.lastMeleeGoalTick >= MELEE_COOLDOWN);
        }

        public boolean canContinueToUse() {
            return !stopped && BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.getTarget().isAlive() &&
                    !BusinessCEO.this.isInSummonPhase();
        }

        public void start() {
            this.ticks = 20;
            this.stopped = false;
            this.hasAttacked = false;
            BusinessCEO.this.triggerAnim(ceo_controller, BusinessCEO.this.random.nextBoolean() ? punch : kick);
        }

        public void stop() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                BusinessCEO.this.setTarget(null);
            }
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
            stopped = true;
            BusinessCEO.this.lastMeleeGoalTick = BusinessCEO.this.tickCount;
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
                    if (BusinessCEO.this.distanceToSqr(BusinessCEO.this.getTarget()) < 12){
                        BusinessCEO.this.doHurtTarget(livingentity);
                    }
                    BusinessCEO.this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2, 0.8f);
                    this.hasAttacked = true;
                    this.ticks = 60;
                } else if (this.hasAttacked) {
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
        boolean stopped = false;

        public BusinessCEODashGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.distanceToSqr(BusinessCEO.this.getTarget()) > 12 &&
                    !BusinessCEO.this.isInSummonPhase() &&
                    (BusinessCEO.this.tickCount - BusinessCEO.this.lastDashGoalTick >= DASH_COOLDOWN);
        }

        @Override
        public boolean canContinueToUse() {
            return !stopped && BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.getTarget().isAlive() &&
                    BusinessCEO.this.distanceToSqr(BusinessCEO.this.getTarget()) > 12 &&
                    !BusinessCEO.this.isInSummonPhase();
        }

        public void start() {
            isDashing = false;
            stopped = false;
            this.ticks = 15;
            BusinessCEO.this.triggerAnim(ceo_controller, dash);
        }

        public void stop() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                BusinessCEO.this.setTarget(null);
            }
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
            BusinessCEO.this.setDeltaMovement(0, 0, 0);
            stopped = true;
            BusinessCEO.this.lastDashGoalTick = BusinessCEO.this.tickCount;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = BusinessCEO.this.getTarget();
            if (livingentity != null) {
                UtilityMethods.sendParticles(
                        (ServerLevel) BusinessCEO.this.level(),
                        ParticleTypes.COMPOSTER,
                        true,
                        5,
                        BusinessCEO.this.getX(), BusinessCEO.this.getY() + 1.5f, BusinessCEO.this.getZ(),
                        0.5f, 0.5f, 0.5f,
                        0.06f
                );
                Vec3 dir = livingentity.position().subtract(BusinessCEO.this.position()).normalize();
                BusinessCEO.this.setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));
                BusinessCEO.this.lookAt(livingentity, 30.0F, 30.0F);
                if (!isDashing) {
                    this.ticks = Math.max(this.ticks - 1, 0);
                    if (this.ticks <= 0) {
                        BusinessCEO.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                        BusinessCEO.this.setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));
                        BusinessCEO.this.lookAt(livingentity, 30.0F, 30.0F);
                        Vec3 move = new Vec3(dir.x, 0, dir.z).normalize().scale(5);
                        BusinessCEO.this.setDeltaMovement(move);
                        isDashing = true;
                        ticks = 30;
                    }
                } else {
                    ticks = Math.max(ticks - 1, 0);
                    if (ticks <= 0) {
                        stop();
                    }
                }
            }
        }
    }

    class BusinessCEOAscendGoal extends Goal {
        private int hoveredTicks = 0;
        Vec3 hoverPosition = new Vec3(0, 0, 0);

        private final int maxHoverTicks = 80; // Maximum hover time

        public BusinessCEOAscendGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return isSecondPhaseTriggered() &&
                    BusinessCEO.this.getTarget() != null &&
                    !BusinessCEO.this.isInSummonPhase() &&
                    (BusinessCEO.this.tickCount - BusinessCEO.this.lastAscendGoalTick >= ASCEND_COOLDOWN);
        }

        @Override
        public boolean canContinueToUse() {
            return BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.getTarget().isAlive() &&
                    hoveredTicks < maxHoverTicks && // Limit the hover time
                    !BusinessCEO.this.isInSummonPhase();
        }

        @Override
        public void start() {
            hoveredTicks = 0;
            BusinessCEO.this.triggerAnim(ceo_controller, ascend);
            BusinessCEO.this.playSound(SoundEvents.PHANTOM_FLAP, 2, 0.8F);
            BusinessCEO.this.setNoGravity(true);
            hoverPosition = BusinessCEO.this.position().add(0, 1, 0); // Start hovering above the current position
        }

        @Override
        public void tick() {
            LivingEntity target = BusinessCEO.this.getTarget();
            if (target == null) return;
            UtilityMethods.sendParticles(
                    (ServerLevel) BusinessCEO.this.level(),
                    ParticleTypes.COMPOSTER,
                    true,
                    5,
                    BusinessCEO.this.getX(), BusinessCEO.this.getY() + 1.5f, BusinessCEO.this.getZ(),
                    0.5f, 0.5f, 0.5f,
                    0.06f
            );
            // Health regeneration effect
            if (hoveredTicks % 10 == 0) {
                BusinessCEO.this.heal(2);
                UtilityMethods.sendParticles(
                        (ServerLevel) BusinessCEO.this.level(),
                        ParticleTypes.HEART,
                        true,
                        5,
                        BusinessCEO.this.getX(), BusinessCEO.this.getY() + 1.5f, BusinessCEO.this.getZ(),
                        0.5f, 0.5f, 0.5f,
                        0.06f
                );
            }

            // Calculate hover position that circles around the target
            double radius = 5.0; // Distance from target
            double angle = (Math.PI * 2 * hoveredTicks) / maxHoverTicks; // Complete a circle
            Vec3 hoverPosition = new Vec3(
                    this.hoverPosition.x + radius * Math.cos(angle),
                    this.hoverPosition.y,
                    this.hoverPosition.z + radius * Math.sin(angle)
            );

            BusinessCEO.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            hoveredTicks++;

            Vec3 currentPos = BusinessCEO.this.position();
            Vec3 targetPos = hoverPosition.subtract(currentPos);
            BusinessCEO.this.setDeltaMovement(targetPos.scale(0.25));
            Vec3 dir = target.position().subtract(BusinessCEO.this.position()).normalize();
            BusinessCEO.this.setYRot((float) Math.toDegrees(Math.atan2(-dir.x, dir.z)));

            // Every 20 ticks, shoot projectiles
            if (hoveredTicks % 20 == 0) {
                // Calculate targeting vectors
                double distanceSquared = BusinessCEO.this.distanceToSqr(target);

                // Get direction components to target
                double xDiff = target.getX() - BusinessCEO.this.getX();
                double yDiff = target.getY(0.5) - BusinessCEO.this.getY(0.5);
                double zDiff = target.getZ() - BusinessCEO.this.getZ();

                // Calculate spread factor based on distance
                double spreadFactor = Math.sqrt(Math.sqrt(distanceSquared)) * 0.5;

                // Create trajectory with randomized spread
                Vec3 trajectory = new Vec3(
                        BusinessCEO.this.getRandom().triangle(xDiff, 2.297 * spreadFactor),
                        yDiff,
                        BusinessCEO.this.getRandom().triangle(zDiff, 2.297 * spreadFactor)
                );

                // Create and position fireball
                EmeraldProjectileEntity fireball = new EmeraldProjectileEntity(
                        BusinessCEO.this.level(),
                        BusinessCEO.this,
                        trajectory.normalize()
                );
                fireball.setPos(fireball.getX(), BusinessCEO.this.getY(0.5) + 1.5, fireball.getZ());

                // Add to world and play sound
                BusinessCEO.this.level().addFreshEntity(fireball);
                BusinessCEO.this.playSound(SoundEvents.BREEZE_SHOOT, 2, 1.0F);
            }
        }

        @Override
        public void stop() {
            BusinessCEO.this.setNoGravity(false);
            BusinessCEO.this.setDeltaMovement(0, 0, 0);
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
            BusinessCEO.this.lastAscendGoalTick = BusinessCEO.this.tickCount;
        }
    }

    class BusinessCEOPointGoal extends Goal {
        private int pointTicks = 0;
        private int summonsCreated = 0;
        private static final int MAX_SUMMONS = 3;

        public BusinessCEOPointGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return isSecondPhaseTriggered() &&
                    BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.distanceToSqr(BusinessCEO.this.getTarget()) > 12 &&
                    !BusinessCEO.this.isInSummonPhase() &&
                    (BusinessCEO.this.tickCount - BusinessCEO.this.lastPointGoalTick >= POINT_COOLDOWN);
        }

        @Override
        public boolean canContinueToUse() {
            return BusinessCEO.this.getTarget() != null &&
                    BusinessCEO.this.getTarget().isAlive() &&
                    pointTicks < 60 &&
                    summonsCreated < MAX_SUMMONS;
        }

        @Override
        public void start() {
            pointTicks = 0;
            summonsCreated = 0;
            BusinessCEO.this.triggerAnim(ceo_controller, point);
            BusinessCEO.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 2, 1.0F);
        }

        @Override
        public void tick() {
            BusinessCEO.this.triggerAnim(ceo_controller, point);
            LivingEntity target = BusinessCEO.this.getTarget();
            if (target == null) return;

            pointTicks++;
            BusinessCEO.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            UtilityMethods.sendParticles(
                    (ServerLevel) BusinessCEO.this.level(),
                    ParticleTypes.COMPOSTER,
                    true,
                    5,
                    BusinessCEO.this.getX(), BusinessCEO.this.getY() + 1.5f, BusinessCEO.this.getZ(),
                    0.5f, 0.5f, 0.5f,
                    0.06f
            );
            // Every 20 ticks, create a summon
            if (pointTicks % 20 == 0 && summonsCreated < MAX_SUMMONS) {
                Level level = BusinessCEO.this.level();

                // Calculate position near the target
                double offsetX = BusinessCEO.this.random.nextDouble() * 4 - 2;
                double offsetZ = BusinessCEO.this.random.nextDouble() * 4 - 2;

                if (level instanceof ServerLevel serverLevel) {

                    // Create summon particle effect
                    Vec3 summonPos = new Vec3(target.getX() + offsetX, target.getY(), target.getZ() + offsetZ);
                    UtilityMethods.sendParticles(serverLevel, ParticleTypes.PORTAL, true, 50,
                            summonPos.x, summonPos.y, summonPos.z, 1, 1, 1, 0.05f);
                    EntityType<?> summonType = BusinessCEO.this.random.nextFloat() > 0.7f ? WDEntities.BUSINESS_VINDICATOR.get() : WDEntities.BUSINESS_EVOKER.get();
                    LivingEntity summon = (LivingEntity) summonType.create(serverLevel);
                    if (summon != null) {
                        summon.moveTo(summonPos);
                        summon.setPos(summonPos.x, summonPos.y, summonPos.z);
                        if (summon instanceof BusinessVindicator){
                            ((BusinessVindicator) summon).equipItemIfPossible(Items.IRON_AXE.getDefaultInstance());
                        }
                        serverLevel.addFreshEntity(summon);
                    }

                    BusinessCEO.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 2f, 1.0F);
                    summonsCreated++;
                }
            }
        }

        @Override
        public void stop() {
            BusinessCEO.this.triggerAnim(ceo_controller, idle);
            BusinessCEO.this.lastPointGoalTick = BusinessCEO.this.tickCount;
        }
    }
}