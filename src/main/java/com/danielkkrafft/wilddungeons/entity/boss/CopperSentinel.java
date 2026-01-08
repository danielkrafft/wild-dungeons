package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.block.ToxicGasBlock;
import com.danielkkrafft.wilddungeons.entity.ToxicWisp;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;
import java.util.Optional;

//540 original -> 425 now (115 saved)
public class CopperSentinel extends WDBoss implements GeoEntity , RangedAttackMob {
    private static final String COPPER_SENTINEL_CONTROLLER = "copper_sentinel_controller";
    private final AnimationController<CopperSentinel> mainController = new AnimationController<>(this, COPPER_SENTINEL_CONTROLLER, 5,
            state -> state.setAndContinue(startupAnim))
            .triggerableAnim(startup, startupAnim)
            .triggerableAnim(walk, walkAnim)
            .triggerableAnim(shoot, shootAnim)
            .triggerableAnim(slash, slashAnim);
    private static final String
            startup = "animation.model.transform",
            walk = "animation.model.walking",
            shoot = "animation.model.shoot",
            slash = "animation.model.swing_charge";
    private static final RawAnimation
            startupAnim = RawAnimation.begin().thenPlayAndHold(startup),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            shootAnim = RawAnimation.begin().thenPlay(shoot).thenLoop(walk),
            slashAnim = RawAnimation.begin().thenPlay(slash).thenLoop(walk);
    private static final int MELEE_COOLDOWN = 20;
    private static final int RANGED_COOLDOWN = 120;
    private static final int EXPLODE_COOLDOWN = 200;

    // Tracking when goals were last used
    private int lastMeleeGoalTick = -MELEE_COOLDOWN;
    private int lastRangedGoalTick = -RANGED_COOLDOWN;
    private int lastExplodeGoalTick = EXPLODE_COOLDOWN;

    public CopperSentinel(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_6);
        this.summonTicks = 80;
    }


    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
        goalSelector.addGoal(1, new CopperSentinelMeleeAttackGoal());
        goalSelector.addGoal(1, new CopperSentinelRangedAttackGoal());
        goalSelector.addGoal(2, new CopperSentinelExplodeGoal());
        goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.35, 32) {
            @Override
            public boolean canUse() {
                return super.canUse() && !CopperSentinel.this.isInvulnerable();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !CopperSentinel.this.isInvulnerable();
            }

            @Override
            public void tick() {
                //play step sound every 10 ticks
                if (CopperSentinel.this.tickCount % 20 == 0) {
                    CopperSentinel.this.playSound(SoundEvents.IRON_GOLEM_STEP, 3, .7f);
                }
                super.tick();
            }
        });
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false,
                player -> !((Player) player).isCreative() && !((Player) player).isSpectator()));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof CopperSentinel) && !(li instanceof ToxicWisp)));
    }

    @Override
    public void tick() {
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        super.tick();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.ATTACK_KNOCKBACK, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        UtilityMethods.sendParticles((ServerLevel) CopperSentinel.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles((ServerLevel) CopperSentinel.this.level(), ParticleTypes.LAVA, true, 200, pos.x, pos.y, pos.z, 1, 2, 1, 0.06f);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
            if (this.isAlive() && !this.isInvulnerable()) {
                ItemStack arrowStack = new ItemStack(Items.ARROW,1);
                Arrow arrow = new Arrow(this.level(), this, arrowStack, null);
                arrow.setBaseDamage(8.0);
                Vec3 viewVec = this.getViewVector(1.0f);
                Vec3 rightVec = viewVec.cross(new Vec3(0,1,0)).normalize();
                Vec3 upVec = viewVec.cross(rightVec).normalize();
                Vec3 offset = new Vec3(1.3,2.3,-2);//offset from entity position to shoot from using .cross()
                arrow.moveTo(this.getX() + offset.x * rightVec.x + offset.y * viewVec.x + offset.z * upVec.x,
                        this.getY() + offset.x * rightVec.y + offset.y * viewVec.y + offset.z * upVec.y,
                        this.getZ() + offset.x * rightVec.z + offset.y * viewVec.z + offset.z * upVec.z);

                // Calculate the difference in position between the target and this entity
                double deltaX = target.getX() - arrow.getX();
                double deltaY = target.getY(0.333) - arrow.getY(); // Aim slightly above the target's base
                double deltaZ = target.getZ() - arrow.getZ();
                // Calculate horizontal distance factor for arrow trajectory
                double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 0.2;
                arrow.shoot(deltaX, deltaY + horizontalDistance, deltaZ, 1.6f, (float) (14 - this.level().getDifficulty().getId() * 4));
                this.playSound(SoundEvents.SKELETON_SHOOT, 2, 0.8f);
                this.level().addFreshEntity(arrow);
            }
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, .5f, 1.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if ((source.getEntity() instanceof ToxicWisp)) {
            return super.hurt(source, 100);
        } else if (source.is(Tags.DamageTypes.IS_POISON))
        {
            return super.hurt(source, 5);
        }
        return false;
    }

    class CopperSentinelMeleeAttackGoal extends Goal {
        private int ticks;
        private boolean hasAttacked;
        private boolean stopped = false;
        private static final float MELEE_DISTANCE = 18;

        public CopperSentinelMeleeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return CopperSentinel.this.getTarget() != null &&
                    CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) < MELEE_DISTANCE &&
                    !CopperSentinel.this.isInvulnerable() &&
                    (CopperSentinel.this.tickCount - CopperSentinel.this.lastMeleeGoalTick >= MELEE_COOLDOWN);
        }

        public boolean canContinueToUse() {
            return !stopped && CopperSentinel.this.getTarget() != null &&
                    CopperSentinel.this.getTarget().isAlive() &&
                    !CopperSentinel.this.isInvulnerable() &&
                    (CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) < MELEE_DISTANCE);
        }

        public void start() {
            this.ticks = 20;
            this.stopped = false;
            this.hasAttacked = false;
            CopperSentinel.this.getNavigation().stop();
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, slash);
            //rotate the entity to face the target
            double d0 = CopperSentinel.this.getTarget().getX() - CopperSentinel.this.getX();
            double d1 = CopperSentinel.this.getTarget().getZ() - CopperSentinel.this.getZ();
            CopperSentinel.this.setYRot((float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90F);
            CopperSentinel.this.yBodyRot = CopperSentinel.this.getYRot();
            CopperSentinel.this.yHeadRot = CopperSentinel.this.getYRot();}


        public void stop() {
            LivingEntity livingentity = CopperSentinel.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                CopperSentinel.this.setTarget(null);
            }
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, walk);
            stopped = true;
            CopperSentinel.this.lastMeleeGoalTick = CopperSentinel.this.tickCount;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = CopperSentinel.this.getTarget();
            if (livingentity != null) {
                CopperSentinel.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                this.ticks = Math.max(this.ticks - 1, 0);
                if (!this.hasAttacked && this.ticks <= 0 && this.canPerformAttack(livingentity)) {
                    if (CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) < MELEE_DISTANCE) {
                        CopperSentinel.this.doHurtTarget(livingentity);
                    }
                    CopperSentinel.this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2, 0.8f);
                    this.hasAttacked = true;
                    this.ticks = 60;
                } else if (this.hasAttacked) {
                    this.ticks = Math.max(this.ticks - 1, 0);
                    if (this.ticks <= 0) {
                        this.stop();
                    }
                }
            } else {
                this.ticks = Math.max(this.ticks - 1, 0);
                if (this.ticks <= 0) {
                    this.stop();
                }
            }
        }

        protected boolean canPerformAttack(LivingEntity entity) {
            return CopperSentinel.this.distanceToSqr(entity) < MELEE_DISTANCE && CopperSentinel.this.getSensing().hasLineOfSight(entity);
        }
    }

    class CopperSentinelRangedAttackGoal extends Goal {
        private int attackTime = 0;
        private boolean stopped = false;
        private int projectilesShot = 0;
        private static final int RANGED_PROJECTILES = 3; // Number of projectiles to shoot in one burst

        public CopperSentinelRangedAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return CopperSentinel.this.getTarget() != null &&
                    CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) >= 12 &&
                    CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) <= 400 &&
                    !CopperSentinel.this.isInvulnerable() &&
                    (CopperSentinel.this.tickCount - CopperSentinel.this.lastRangedGoalTick >= RANGED_COOLDOWN);
        }

        @Override
        public boolean canContinueToUse() {
            return !stopped && CopperSentinel.this.getTarget() != null &&
                    CopperSentinel.this.getTarget().isAlive() &&
                    !CopperSentinel.this.isInvulnerable();
        }

        @Override
        public void start() {
            this.attackTime = 17; // Initial delay before first attack
            this.projectilesShot = 0;
            this.stopped = false;
            CopperSentinel.this.getNavigation().stop();
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, shoot);
            //rotate the entity to face the target
            double d0 = CopperSentinel.this.getTarget().getX() - CopperSentinel.this.getX();
            double d1 = CopperSentinel.this.getTarget().getZ() - CopperSentinel.this.getZ();
            CopperSentinel.this.setYRot((float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90F);
            CopperSentinel.this.yBodyRot = CopperSentinel.this.getYRot();
            CopperSentinel.this.yHeadRot = CopperSentinel.this.getYRot();

        }

        @Override
        public void stop() {
            LivingEntity livingentity = CopperSentinel.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                CopperSentinel.this.setTarget(null);
            }
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, walk);
            stopped = true;
            CopperSentinel.this.lastRangedGoalTick = CopperSentinel.this.tickCount;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = CopperSentinel.this.getTarget();
            if (livingentity != null) {
                CopperSentinel.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                if (--this.attackTime <= 0 && this.projectilesShot < RANGED_PROJECTILES) {
                    if (CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) >= 12 && CopperSentinel.this.distanceToSqr(CopperSentinel.this.getTarget()) <= 400) {
                        this.projectilesShot++;
                        CopperSentinel.this.performRangedAttack(livingentity, 1.0f);
                        if (this.projectilesShot < RANGED_PROJECTILES) {
                            this.attackTime = 5; // Short delay between shots in a burst
                        } else {
                            this.attackTime = 15; // Longer delay after finishing a burst
                        }
                    } else {
                        this.stop();
                    }
                } else if (this.attackTime<=0) {
                    this.stop();
                }
            } else {
                this.stop();
            }
        }
    }

    class CopperSentinelExplodeGoal extends Goal {
        private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
                true, true, Optional.of(1.22F), Optional.empty());
        private static final float EXPLODE_DISTANCE = 3;
        private int ticks;
        public CopperSentinelExplodeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return CopperSentinel.this.getTarget() != null &&
                    !CopperSentinel.this.isInvulnerable() &&
                    (CopperSentinel.this.tickCount - CopperSentinel.this.lastExplodeGoalTick >= EXPLODE_COOLDOWN);
        }

        public boolean canContinueToUse() {
            return CopperSentinel.this.getTarget() != null &&
                    CopperSentinel.this.getTarget().isAlive() &&
                    !CopperSentinel.this.isInvulnerable() &&
                    (CopperSentinel.this.tickCount - CopperSentinel.this.lastExplodeGoalTick >= EXPLODE_COOLDOWN);
        }

        public void start() {
            CopperSentinel.this.level().explode(CopperSentinel.this, CopperSentinel.this.getX(), CopperSentinel.this.getY()+2, CopperSentinel.this.getZ(), EXPLODE_DISTANCE, false, Level.ExplosionInteraction.NONE);
            //particles
            if (CopperSentinel.this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, CopperSentinel.this.getX(), CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 20, 1, 1, 1, 0.1);
                serverLevel.sendParticles(ParticleTypes.SMOKE, CopperSentinel.this.getX(), CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 50, 1, 1, 1, 0.05f);
                serverLevel.sendParticles(ParticleTypes.FLAME, CopperSentinel.this.getX(), CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 30, 1, 1, 1, 0.05f);
            }
            //search nearby for gasblocks to trigger
            BlockPos.betweenClosedStream(CopperSentinel.this.blockPosition().offset(-5, -3, -5), CopperSentinel.this.blockPosition().offset(5, 10, 5)).forEach(pos -> {
                BlockState state = CopperSentinel.this.level().getBlockState(pos);
                if (state.is(WDBlocks.TOXIC_GAS.get())){
                    ToxicGasBlock tgb = (ToxicGasBlock)state.getBlock();
                    tgb.Explode(CopperSentinel.this.level(), pos);
                }
            });
        }

        public void stop() {
            LivingEntity livingentity = CopperSentinel.this.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                CopperSentinel.this.setTarget(null);
            }
            this.ticks = 0;
            CopperSentinel.this.lastExplodeGoalTick = CopperSentinel.this.tickCount;
        }

        @Override
        public void tick() {
            if (ticks++ < 5) {
                return;
            }
            //spawn a wisp
            ToxicWisp wisp = WDEntities.SMALL_TOXIC_WISP.get().create(CopperSentinel.this.level());
            //position it around the sentinel randomly at least 4 blocks away
            Vec3 pos = CopperSentinel.this.position().add((CopperSentinel.this.level().random.nextDouble() - 0.5) * 8, 1, (CopperSentinel.this.level().random.nextDouble() - 0.5) * 8);
            wisp.setPos(pos.x, pos.y, pos.z);
            CopperSentinel.this.level().addFreshEntity(wisp);
            //push the wisp away from the sentinel
            Vec3 kb = wisp.position().subtract(CopperSentinel.this.position()).normalize().scale(1);
            wisp.setDeltaMovement(kb);
            CopperSentinel.this.playSound(SoundEvents.BEEHIVE_EXIT, 2, 0.7f);
            this.stop();
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}