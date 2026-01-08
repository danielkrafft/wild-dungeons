package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.WindChargeProjectile;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;
import java.util.List;

/**
 * Breeze Golem
 * Spawning: Heavy core placed on top of heavy rune
 * Multicolor particles from heavy core, hissing sound, beeping sound, after 5s explosion (doesn't break blocks) + breeze golem
 * Drops 32-64 breeze rods + jetpack
 * Stats: 225 hp, immune to fire/lava, drowning, freezing, status effects
 * Movement: flies (like a blaze), stationary if target is in range, otherwise moves slowly [inflicts fire onto entities and flammable blocks]
 * 3 attacks(3s cooldown)
 * Heavy core spin: 5 hp melee damage to every entity within 3 block range [triggered when entity is 3b]
 * Rapid Cannon: Launches 10 small wind charges across 2.5s. Target position updated per shot. Triggered when has line of sight of target
 * Charged Cannon: Charges cannon 1-10 times every second (beeping sound each charge). Triggered if the target is obstructed by blocks. Wind charge shot if reacquire line of sight and below 10 charges
 */

//615 original -> 446 now (169 saved)
public class BreezeGolem extends WDBoss implements RangedAttackMob, GeoEntity {
    private static final String CONTROLLER="breezegolemcontroller";

    private static final String
            idle="idle",
            walk="walk",
            coreSpin="core_spin",
            rapidCannonStart="rapid_cannon_start",rapidCannonShoot="rapid_cannon_shoot",
            chargedCannonCharge="charged_canon_charge",chargedCannonShoot="charged_canon_shoot";

    private static final RawAnimation
            idleAnim=RawAnimation.begin().thenLoop(idle),
            walkAnim=RawAnimation.begin().thenLoop(walk),
            coreSpinAnim=RawAnimation.begin().thenPlay(coreSpin),
            rapidCannonStartAnim=RawAnimation.begin().thenPlay(rapidCannonStart),
            rapidCannonShootAnim=RawAnimation.begin().thenLoop(rapidCannonShoot),
            chargedCannonChargeAnim=RawAnimation.begin().thenLoop(chargedCannonCharge),
            chargedCanonShootAnim=RawAnimation.begin().thenPlay(chargedCannonShoot);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this,CONTROLLER, 2,
                state -> state.setAndContinue(state.isMoving() ? walkAnim : idleAnim)).
                triggerableAnim(idle, idleAnim).
                triggerableAnim(walk, walkAnim).
                triggerableAnim(coreSpin, coreSpinAnim).
                triggerableAnim(rapidCannonStart, rapidCannonStartAnim).
                triggerableAnim(rapidCannonShoot, rapidCannonShootAnim).
                triggerableAnim(chargedCannonCharge, chargedCannonChargeAnim).
                triggerableAnim(chargedCannonShoot, chargedCanonShootAnim)
        );
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 velocity) {
        super.move(type, velocity);
        if (velocity.lengthSqr() > 0.01) {
            if (tickCount % 7 == 0) {
                playSound(WDSoundEvents.BREEZE_GOLEM_WALK.value(), 0.6f, random.nextFloat() * 0.4f + 0.8f);
                }
        }
    }

    public enum AttackType {
        HEAVYCORESPIN(35), RAPIDCANNON(65), CHARGEDCANNON(220);

        AttackType(int duration) {
            this.duration = duration;
        }

        public final int duration;
    }

    //serverside variables
    public int targetTime = 30 * 20;
    public AttackType attackType;
    private LivingEntity currentTarget;
    private int cannonCharge = 1;
    private int attackTicks = 0;
    private static final float ASCENDRATE = 0.15f;
    public BreezeGolem(EntityType<? extends Monster> type, Level level) {
        super(type, level, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        moveControl = new FlyingMoveControl(this, 10, false);
        xpReward = 100;
        this.summonTicks = 50;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().
                add(Attributes.MAX_HEALTH, 100).
                add(Attributes.MOVEMENT_SPEED, 0.35).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 10).
                add(Attributes.ATTACK_KNOCKBACK, 2).
                add(Attributes.KNOCKBACK_RESISTANCE, 0.4).
                add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2).
                add(Attributes.FLYING_SPEED, 2).
                build();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1, 30));
        goalSelector.addGoal(2, new BreezeGolemAttackGoal(this, 2, 60, 5));
        goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, MutantBogged.class,BreezeGolem.class));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li->!(li instanceof BreezeGolem)));
        //targetSelector.addGoal(1, new NearestAttackableTargetGoal <>(this, LivingEntity.class, false, false));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation path = new FlyingPathNavigation(this, level);
        path.setCanFloat(false);
        path.setCanPassDoors(true);
        return path;
    }

    public void spawn(EntityType<?> entity, int count) {}

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        if (level.isClientSide || isDeadOrDying()) return;
        if (isInSummonPhase()) return;
        updateBossBar();
        Vec3 pos = position(), vel = getDeltaMovement();
        BlockPos block = blockPosition();

        setRemainingFireTicks(0);
        double diff = -1;

        // inflict fire below
        for (int y = 1; y < 5; y++) {
            BlockPos deltaBlock = block.below(y);
            List<LivingEntity> nearby = level.getNearbyEntities(
                    LivingEntity.class, TargetingConditions.DEFAULT, this,
                    AABB.ofSize(deltaBlock.getCenter(), 1, 1, 1)
            );
            nearby.forEach(li -> li.setRemainingFireTicks(li.getRemainingFireTicks() + 20));

            BlockState state = level.getBlockState(deltaBlock);
            if (!state.isAir()) {
                BlockState stateAbove = level.getBlockState(deltaBlock.above());
                diff = 0;
                if (stateAbove.isAir())
                    level.setBlockAndUpdate(deltaBlock.above(), BaseFireBlock.getState(level, deltaBlock.above()));
                break;
            }
        }
        if (currentTarget != null) {
            double targetY = currentTarget.position().y;
            diff = targetY - pos.y + (new Vec3(
                    currentTarget.position().x - pos.x, 0,
                    currentTarget.position().z - pos.z
            ).lengthSqr() < 9 ? 0.5 : 6);

            if (--targetTime <= 0 || currentTarget.isRemoved() || currentTarget.isDeadOrDying())
                currentTarget = null;
        }
        if (Math.abs(diff) > 0)
            setDeltaMovement(vel.x, Math.clamp(diff / 2, -1, 1) * ASCENDRATE, vel.z);
        if (attacking) {
            if (currentTarget == null || currentTarget.isRemoved() || currentTarget.isDeadOrDying() || !canAttack(currentTarget)) {
                EndAttack();
            } else {
                switch (attackType) {
                    case HEAVYCORESPIN -> {
                        if (attackTicks >= 25 && attackTicks <= 29) {
                            List<LivingEntity> nearby = level.getNearbyEntities(
                                    LivingEntity.class, TargetingConditions.DEFAULT, this,
                                    AABB.ofSize(pos, 10, 5, 10)
                            );
                            nearby.forEach(li -> {
                                if (li.hurtTime <= 0) {
                                    Vec3 position = li.position();
                                    UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.SMALL_FLAME, true, 3,
                                            position.x, li.getY(0.5), position.z, 0, 0, 0, 0.1f);
                                    UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.CRIT, true, 10,
                                            position.x, li.getY(0.5), position.z, 0.4f, 0.4f, 0.4f, 0);
                                    playSound(WDSoundEvents.HAMMER_SMASH_LIGHT.value(), 1f, random.nextFloat() * 0.2f + 0.8f);
                                    Vec3 kb = new Vec3(pos.x - position.x, pos.y - position.y, pos.z - position.z)
                                            .normalize().scale(1);
                                    li.knockback(1.5, kb.x, kb.z);
                                    li.hurt(level.damageSources().generic(), 10);
                                }
                            });
                        }
                    }
                    case RAPIDCANNON -> {
                        if (attackTicks >= 10 && attackTicks % 5 == 0) {
                            playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_SHOOT.value(), 2f, 1f);
                            triggerAnim(CONTROLLER, rapidCannonShoot);
                            if (!hasLineOfSight(currentTarget)) {
                                EndAttack();
                                return;
                            }
                            WindChargeProjectile proj = WDEntities.WIND_CHARGE_PROJECTILE.get().create(level);
                            if (proj != null) {
                                Vec3 handPos = getPositionRelative(1, 2, 2.5);
                                UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.CLOUD, true, 10,
                                        getEyePosition().x, getEyePosition().y, getEyePosition().z, 0, 0, 0, 0.06f);
                                proj.defaultCharge(false, false, new Vec3(
                                                currentTarget.getX() - handPos.x,
                                                currentTarget.getY(0.5) - handPos.y,
                                                currentTarget.getZ() - handPos.z
                                        ).normalize().scale(1.3),
                                        this
                                );
                                proj.moveTo(handPos);
                                level.addFreshEntity(proj);
                                spawn(WDEntities.WIND_CHARGE_PROJECTILE.get(), 5);
                                spawn(WDEntities.WIND_CHARGE_PROJECTILE.get(), 5);
                            }
                        }
                    }
                    case CHARGEDCANNON -> {
                        if (cannonCharge == 14 || (hasLineOfSight(currentTarget) && cannonCharge <= 14)) {
                            if (attackTicks > 20) attackTicks = 1;
                            triggerAnim(CONTROLLER, chargedCannonShoot);
                            if (attackTicks % 5 == 0) {
                                WindChargeProjectile proj = WDEntities.WIND_CHARGE_PROJECTILE.get().create(level);
                                if (proj != null) {
                                    Vec3 handPos = getPositionRelative(1, 2, 2.5);
                                    UtilityMethods.sendParticles((ServerLevel) level, ParticleTypes.CLOUD, true,
                                            50 + cannonCharge,
                                            getEyePosition().x, getEyePosition().y, getEyePosition().z,
                                            0, 0, 0, 0.06f + (0.014f * cannonCharge)
                                    );
                                    proj.setCompressions(false, false, new Vec3(
                                                    currentTarget.getX() - handPos.x,
                                                    currentTarget.getY(0.5) - handPos.y,
                                                    currentTarget.getZ() - handPos.z
                                            ).normalize().scale(2),
                                            cannonCharge, this
                                    );
                                    proj.moveTo(handPos);
                                    level.addFreshEntity(proj);
                                    playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_SHOOT.value(), 1f, 1 - (cannonCharge / 14f * 0.3f));
                                }
                                cannonCharge = 15;
                            }
                        }
                        if (attackTicks % 20 == 0) {
                            if (cannonCharge == 15) EndAttack();
                            else {
                                playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 0.05f * cannonCharge + .8f);
                                playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_CHARGE.value(), 1f, 1f);
                                cannonCharge++;
                            }
                        }
                    }
                }
            }
            if (++attackTicks >= attackType.duration) EndAttack();
        } else {
            if (navigation.isInProgress()) triggerAnim(CONTROLLER, walk);
            else triggerAnim(CONTROLLER, idle);
        }
    }

    public Vec3 getPositionRelative(double forwardDistance, double rightDistance, double upDistance) {
        float viewXRot = (float)lerpXRot, viewYRot = (float)lerpYRot;
        return position().add(forwardDistance == 0 ? Vec3.ZERO : Vec3.directionFromRotation(viewXRot, viewYRot).scale(forwardDistance)).
                add(rightDistance == 0 ? Vec3.ZERO : Vec3.directionFromRotation(viewXRot,viewYRot+90).scale(rightDistance)).
                add(upDistance == 0 ? Vec3.ZERO : new Vec3(0, 1, 0).scale(upDistance));
    }

    private void EndAttack() {
        attacking = false;
        currentTarget = null;
    }

    @Override
    protected boolean isImmuneToDamageType(DamageSource source) {
        return source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.FREEZE)
                || source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.INDIRECT_MAGIC)
                || source.is(DamageTypes.FALL);
    }

    @Override
    protected void dropAllDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source) {
        super.dropAllDeathLoot(level, source);
        spawnAtLocation(new ItemStack(Items.BREEZE_ROD, UtilityMethods.RNG(32, 64)));
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float vel) {
        if (!level().isClientSide) {
            if (!attacking) {
                attacking = true;
                attackTicks = 0;
                if (this.currentTarget == null) this.currentTarget = target;
                cannonCharge = 3;
                targetTime = 600;
                double distSqr = distanceToSqr(target);
                if (hasLineOfSight(target)) {
                    if (distSqr < 8 * 8&&Math.abs(position().y - target.position().y) <= 5) {
                        playSound(WDSoundEvents.BREEZE_GOLEM_CORE.value(), 1f, 1f);
                        attackType = AttackType.HEAVYCORESPIN;//same as saying dist < 3, negate a sqrt function
                        triggerAnim(CONTROLLER, coreSpin);
                    } else {
                        playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_START.value(), 1f, 1f);
                        attackType = AttackType.RAPIDCANNON;
                        triggerAnim(CONTROLLER, rapidCannonStart);
                    }
                } else {
                    playSound(WDSoundEvents.BREEZE_GOLEM_CANNON_CHARGE.value(), 1f, 1f);
                    attackType = AttackType.CHARGEDCANNON;
                    triggerAnim(CONTROLLER, chargedCannonCharge);
                }
            }
        }
    }

    private static final BossSounds SOUNDS = new BossSounds(
            WDSoundEvents.BREEZE_GOLEM_AMBIENT.value(),
            WDSoundEvents.BREEZE_GOLEM_AMBIENT.value(),
            WDSoundEvents.BREEZE_GOLEM_DEATH.value()
    );

    @Override
    protected BossSounds bossSounds() {
        return SOUNDS;
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        if (!(level() instanceof ServerLevel server)) return;

        UtilityMethods.sendParticles(server, ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles(server, ParticleTypes.LAVA, true, 200, pos.x, pos.y, pos.z, 2, 2, 2, 0.06f);
        UtilityMethods.sendParticles(server, ParticleTypes.FLAME, true, 400, pos.x, pos.y, pos.z, 4, 4, 4, 0.08f);
    }

    public static class BreezeGolemAttackGoal extends Goal {
        private final BreezeGolem mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private final int attackInterval;
        private final float attackRadiusSqr;

        public BreezeGolemAttackGoal(BreezeGolem pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
            this.rangedAttackMob = pRangedAttackMob;
            this.mob = pRangedAttackMob;
            this.speedModifier = pSpeedModifier;
            this.attackInterval = pAttackInterval;
            this.attackRadiusSqr = pAttackRadius*pAttackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = mob.currentTarget != null ? mob.currentTarget : this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTime = -1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            double d0 = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean flag = mob.getSensing().hasLineOfSight(target);

            if (flag && d0 <= attackRadiusSqr) mob.getNavigation().stop();
            else mob.getNavigation().moveTo(this.target, this.speedModifier);

            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (mob.isAttacking()) attackTime = mob.attackType == AttackType.HEAVYCORESPIN ? 2 : attackInterval;
            else if (--this.attackTime <= 0) {
                this.rangedAttackMob.performRangedAttack(this.target, 1);
                attackTime = attackInterval;
            }
        }
    }
}