package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.entity.PiercingArrow;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//543 original -> 433 now (110 saved)
public class MutantBogged extends WDBoss implements RangedAttackMob, GeoEntity {
    private static final String CONTROLLER = "mutantboggedcontroller";
    private static final String idle = "idle", walk = "walk", arrowVolley = "arrow_volley", chargedArrow = "charged_arrow", dig = "dig";
    private static final RawAnimation idleAnim = RawAnimation.begin().thenLoop(idle), walkAnim = RawAnimation.begin().thenLoop(walk), arrowVolleyAnim = RawAnimation.begin().thenPlay(arrowVolley), chargedArrowAnim = RawAnimation.begin().thenPlay(chargedArrow), digAnim = RawAnimation.begin().thenLoop(dig);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER, 2,
                state -> state.setAndContinue(state.isMoving() ? walkAnim : idleAnim)).
                triggerableAnim(idle, idleAnim).
                triggerableAnim(walk, walkAnim).
                triggerableAnim(arrowVolley, arrowVolleyAnim).
                triggerableAnim(chargedArrow, chargedArrowAnim).
                triggerableAnim(dig, digAnim)
        );
    }

    public enum AttackType {
        ARROWVOLLEY(40), CHARGEDARROW(50), DIG(25);

        AttackType(int duration) {
            this.duration = duration;
        }

        public final int duration;
    }

    //serverside variables
    public int targetTime = 30 * 20;
    public AttackType attackType;
    private LivingEntity currentTarget;
    private int attackTicks = 0;
    private boolean attacking;
    private int maxDestroySpeed, currDestroySpeed;

    public MutantBogged(EntityType<? extends Monster> type, Level level) {
        super(type, level, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
        moveControl = new MoveControl(this);
        xpReward = 100;
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 120).
                add(Attributes.MOVEMENT_SPEED, 0.4).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 10).
                add(Attributes.ATTACK_KNOCKBACK, 2).
                add(Attributes.KNOCKBACK_RESISTANCE, 0.4).
                add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2).
                add(Attributes.STEP_HEIGHT, 1.1).
                build();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1, 15));
        goalSelector.addGoal(2, new MutantBoggedAttackGoal(this, 1, 60));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, MutantBogged.class, BreezeGolem.class));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof MutantBogged)));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
    }

    @Override
    public void tick() {
        super.tick();
        if (isInvulnerable()) {
            bossEvent.setVisible(false);
            return;
        }

        Level level = level();
        bossEvent.setVisible(true);
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        if (level.isClientSide || isDeadOrDying()) return;

        Vec3 pos = position();
        List<LivingEntity> nearby = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, this, AABB.ofSize(new Vec3(pos.x, pos.y + getEyeHeight() / 2f, pos.z), 4, 4, 4));
        nearby.forEach(li -> {
            int amplifier = li.distanceToSqr(this) < 1.3 ? 3 : 1;
            MobEffectInstance poisonEffect = li.getEffect(MobEffects.POISON);
            if (poisonEffect == null || poisonEffect.getAmplifier() < amplifier)
                li.addEffect(new MobEffectInstance(MobEffects.POISON, 20, amplifier));
        });
        if (tickCount % 20 == 0) {
            float newHp = getHealth() - 1;
            setHealth(newHp);
            if (newHp <= 0) {
                DamageSource source = getLastDamageSource();
                die(source != null ? source : damageSources().generic());
                return;
            }
        }
        if (currentTarget != null) {
            if (--targetTime <= 0 || currentTarget.isRemoved() || currentTarget.isDeadOrDying())
                currentTarget = null;
        }
        if (attacking) {
            if (currentTarget == null || currentTarget.isRemoved() || currentTarget.isDeadOrDying() || !canAttack(currentTarget))
                EndAttack();
            else {
                switch (attackType) {
                    case ARROWVOLLEY:
                        if (attackTicks == 20) {
                            for (int i = -1; i <= 1; i++) {
                                createArrow(level, 1.8f, 0, i);
                            }
                        }
                        break;
                    case CHARGEDARROW:
                        if (attackTicks == 30) {
                            createArrow(level, 1.6f, 2.5f, 0);
                        }
                        break;
                    case DIG:
                        if (attackTicks != 7) break;
                        Vec3 targetPos = currentTarget.getY() >= getY() ? currentTarget.position() : currentTarget.getEyePosition();
                        playSound(WDSoundEvents.MUTANT_BOGGED_DIG.value());
                        List<BlockPos> positionsToAttack = new ArrayList<>();
                        BlockParticleOption barrier = null;//new BlockParticleOption(ParticleTypes.BLOCK_MARKER,Blocks.BARRIER.defaultBlockState());
                        AtomicInteger count = new AtomicInteger();
                        //hard coded pattern
                        for (int f = 1; f <= 4; f++) {
                            Vec3[] blockAttack = {
                                    //XX
                                    getPositionRelative(barrier, pos, targetPos, f, 0.5, 3.5),
                                    getPositionRelative(barrier, pos, targetPos, f, -0.5, 3.5),
                                    //XXXX
                                    getPositionRelative(barrier, pos, targetPos, f, -0.5, 2.5),
                                    getPositionRelative(barrier, pos, targetPos, f, -1.5, 2.5),
                                    getPositionRelative(barrier, pos, targetPos, f, 0.5, 2.5),
                                    getPositionRelative(barrier, pos, targetPos, f, 1.5, 2.5),
                                    //XXXX
                                    getPositionRelative(barrier, pos, targetPos, f, -0.5, 1.5),
                                    getPositionRelative(barrier, pos, targetPos, f, -1.5, 1.5),
                                    getPositionRelative(barrier, pos, targetPos, f, 0.5, 1.5),
                                    getPositionRelative(barrier, pos, targetPos, f, 1.5, 1.5),
                                    //XX
                                    getPositionRelative(barrier, pos, targetPos, f, 0.5, 0.5),
                                    getPositionRelative(barrier, pos, targetPos, f, -0.5, 0.5)
                            };
                            for (int i = 0; i < blockAttack.length; i++) {
                                Vec3 vec3 = blockAttack[i];
                                BlockPos blockPos = new BlockPos((int) Math.floor(vec3.x), (int) Math.floor(vec3.y), (int) Math.floor(vec3.z));
                                if (canBeMined(blockPos)) {
                                    count.getAndIncrement();
                                    positionsToAttack.add(blockPos);
                                }
                            }
                        }
                        currDestroySpeed++;
                        for (BlockPos posAttack : positionsToAttack) {
                            BlockState state = level.getBlockState(posAttack);
                            if (getDestroySpeed(state.getDestroySpeed(level, posAttack)) <= currDestroySpeed)
                                level.destroyBlock(posAttack, true);
                            else {
                                level.playSound(null, posAttack, state.getSoundType().getHitSound(),
                                        SoundSource.BLOCKS, 2, random.nextFloat() * 0.2f + 0.9f);
                            }
                        }
                        break;
                }
            }
            if (++attackTicks >= attackType.duration) {
                if (attackType == AttackType.DIG && currDestroySpeed < maxDestroySpeed) attackTicks = 0;
                else EndAttack();
            }
        } else {
            if (navigation.isInProgress()) {
                triggerAnim(CONTROLLER, walk);
            } else triggerAnim(CONTROLLER, idle);
        }
    }

    public void createArrow(Level level, float velocity, float pierceAmount, double yDisplacement) {
        if (!level.isClientSide) {
            PiercingArrow arrow = new PiercingArrow(level, this, pierceAmount);
            double xDiff = currentTarget.getX() - arrow.getX();
            double yDiff = currentTarget.getY(0.333) - arrow.getY();
            double zDiff = currentTarget.getZ() - arrow.getZ();
            double xzLength = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            arrow.shoot(xDiff, (yDiff + yDisplacement) + xzLength * 0.2f, zDiff, velocity, 0.05f);
            level.addFreshEntity(arrow);
        }
    }

    private void EndAttack() {
        attacking = false;
        currentTarget = null;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        return super.hurt(source, damage * 0.5f);
    }

    private static final BossSounds SOUNDS = new BossSounds(
            WDSoundEvents.MUTANT_BOGGED_GROWL.value(),
            WDSoundEvents.MUTANT_BOGGED_HIT.value(),
            WDSoundEvents.MUTANT_BOGGED_DEATH.value()
    );

    @Override
    protected BossSounds bossSounds() {
        return SOUNDS;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        playSound(WDSoundEvents.MUTANT_BOGGED_WALK.value(), 1f, 1f);
        super.playStepSound(pPos, pState);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        level().setBlockAndUpdate(blockPosition(), WDBlocks.ROTTEN_MOSS.get().defaultBlockState());
        ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW, UtilityMethods.RNG(32, 64));
        tippedArrow.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
        spawnAtLocation(tippedArrow);
    }

    private int floor(double d) {
        return (int) Math.floor(d);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float vel) {
        if (!level().isClientSide) {
            if (!attacking) {
                attacking = true;
                attackTicks = 0;
                if (this.currentTarget == null) this.currentTarget = target;
                Vec3 positionToLook = currentTarget.getY() >= getY() ? currentTarget.position() : currentTarget.getEyePosition();
                double distToTarget = Math.sqrt(distanceToSqr(positionToLook));
                targetTime = 20 * 15;//stays focused on target for 15s
                Set<BlockPos> positions = new HashSet<>();
                int highestDestroySpeed = -1;
                //at most 14 iterations * 8 testVectors = 112 calculations
                //likely inefficient and there has to be a better way to do this
                for (float i = 0; i < distToTarget; i++) {
                    //Vec3 test=getPositionRelative(ParticleTypes.END_ROD,getEyePosition(),positionToLook,i,0,0);
                    Vec3 test = getPositionRelative(null, getEyePosition(), positionToLook, i, 0, 0);
                    BlockPos pos = new BlockPos(floor(test.x), floor(test.y), floor(test.z));
                    if (!positions.contains(pos)) {
                        BlockState state = level().getBlockState(pos);
                        //block not air, liquid, or invulnerable, dig
                        if (!state.isAir() && !state.liquid()) {
                            positions.add(pos);
                            if (i >= distToTarget - 3) {
                                int speed = getDestroySpeed(state.getDestroySpeed(level(), pos));
                                //System.out.println(state+",mine speed:"+speed);
                                if (speed > highestDestroySpeed) highestDestroySpeed = speed;
                            }
                        }
                    }
                }
                int blocksEncountered = positions.size();
                //DanielKrafftMod.sendClientMessage("Dist:"+Math.round(distToTarget)+",Blocks:"+blocksEncountered+",Highest:"+highestDestroySpeed);
                //adjust this value based on results
                if (blocksEncountered >= 3) {
                    maxDestroySpeed = highestDestroySpeed;
                    currDestroySpeed = 0;
                    triggerAnim(CONTROLLER, dig);
                    attackType = AttackType.DIG;
                } else {
                    if (blocksEncountered == 0) {
                        playSound(WDSoundEvents.MUTANT_BOGGED_ARROW_VOLLEY.value());
                        triggerAnim(CONTROLLER, arrowVolley);
                        attackType = AttackType.ARROWVOLLEY;
                    } else {
                        playSound(WDSoundEvents.MUTANT_BOGGED_CHARGED_ARROW.value());
                        triggerAnim(CONTROLLER, chargedArrow);
                        attackType = AttackType.CHARGEDARROW;
                    }
                }
            }
        }
    }

    public Vec3 getPositionRelative(ParticleOptions options, Vec3 originPos, Vec3 targetPos, double forwardDistance, double rightDistance, double upDistance) {
        Vec3 diff = targetPos.subtract(originPos);
        double xzDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float viewXRot = (float) Math.toDegrees(Mth.atan2(-diff.y, xzDist)), viewYRot = (float) Math.toDegrees(Mth.atan2(-diff.x, diff.z));
        Vec3 position = originPos.add(forwardDistance == 0 ? Vec3.ZERO : Vec3.directionFromRotation(viewXRot, viewYRot).scale(forwardDistance)).
                add(rightDistance == 0 ? Vec3.ZERO : Vec3.directionFromRotation(viewXRot, viewYRot + 90).scale(rightDistance)).
                add(upDistance == 0 ? Vec3.ZERO : new Vec3(0, 1, 0).scale(upDistance));
        if (options != null)
            UtilityMethods.sendParticles((ServerLevel) level(), options, true, 1, position.x, position.y, position.z, 0, 0, 0, 0);
        return position;
    }

    public boolean canBeMined(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        return !state.isAir() && !state.liquid() && getDestroySpeed(state.getDestroySpeed(level(), pos)) >= 0;
    }

    public int getDestroySpeed(float destroySpeed) {
        return destroySpeed < 0 ? -1 : (int) Math.ceil(destroySpeed / 5f);
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        UtilityMethods.sendParticles((ServerLevel) MutantBogged.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles((ServerLevel) MutantBogged.this.level(), ParticleTypes.SPORE_BLOSSOM_AIR, true, 200, pos.x, pos.y, pos.z, 0, 0, 0, 0.3f);
    }

    public static class MutantBoggedAttackGoal extends Goal {
        private final MutantBogged mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private final int attackInterval;

        public MutantBoggedAttackGoal(MutantBogged pRangedAttackMob, double pSpeedModifier, int pAttackInterval) {
            this.rangedAttackMob = pRangedAttackMob;
            this.mob = pRangedAttackMob;
            this.speedModifier = pSpeedModifier;
            this.attackInterval = pAttackInterval;
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

        @Override public boolean requiresUpdateEveryTick() {
            return true;
        }


        @Override
        public void tick() {
            if (target != null) {
                double d0 = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
                if (mob.isAttacking() || d0 < 9) {
                    mob.getNavigation().stop();
                } else {
                    mob.getNavigation().moveTo(this.target, this.speedModifier);
                }
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (mob.isAttacking()) attackTime = mob.attackType == AttackType.DIG ? 2 : attackInterval;
                else if (--this.attackTime <= 0) {
                    this.rangedAttackMob.performRangedAttack(this.target, 1);
                    attackTime = attackInterval;
                }
            }
        }
    }
}