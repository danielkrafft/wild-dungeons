package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.block.ToxicGasBlock;
import com.danielkkrafft.wilddungeons.entity.ToxicWisp;
import com.danielkkrafft.wilddungeons.registry.WDBlocks;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

//540 original -> 453 now (87 saved)
public class CopperSentinel extends WDBoss implements GeoEntity , RangedAttackMob {
    private static final String COPPER_SENTINEL_CONTROLLER = "copper_sentinel_controller";
    private static final String
            startup = "animation.model.transform",
            walk = "animation.model.walking",
            shoot = "animation.model.shoot",
            slash = "animation.model.swing_charge";

    private static final RawAnimation
            startupAnim = RawAnimation.begin().thenPlay(startup),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            shootAnim = RawAnimation.begin().thenPlay(shoot).thenLoop(walk),
            slashAnim = RawAnimation.begin().thenPlay(slash).thenLoop(walk);
    
    private final AnimationController<CopperSentinel> mainController = new AnimationController<>(this, COPPER_SENTINEL_CONTROLLER, 5,
            state -> state.setAndContinue(startupAnim))
            .triggerableAnim(startup, startupAnim)
            .triggerableAnim(walk, walkAnim)
            .triggerableAnim(shoot, shootAnim)
            .triggerableAnim(slash, slashAnim);
    
    private static final int MELEE_COOLDOWN = 20;
    private static final int RANGED_COOLDOWN = 120;
    private static final int EXPLODE_COOLDOWN = 200;
    private static final int MELEE_ACTION = 1;
    private static final int RANGED_ACTION = 2;
    private static final int EXPLODE_ACTION = 3;

    private static final EntityDataAccessor<Integer> ACTION =
            SynchedEntityData.defineId(CopperSentinel.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COOLDOWN =
            SynchedEntityData.defineId(CopperSentinel.class, EntityDataSerializers.INT);

    public CopperSentinel(EntityType<? extends WDBoss> type, Level level) {
        super(type, level, BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_6);
        this.summonTicks = 80;
        this.xpReward = 200;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTION, 0);
        builder.define(COOLDOWN, 0);
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

    @Override
    public void tick() {
        updateBossBar();
        
        if (getTarget() != null && getBossCooldown() > 0 && getBossAction() == 0) {
            setBossCooldown(getBossCooldown() - 1);
        }
        
        super.tick();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
        goalSelector.addGoal(0, new ActionSelector());
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
                if (CopperSentinel.this.tickCount % 20 == 0) {
                    CopperSentinel.this.playSound(SoundEvents.IRON_GOLEM_STEP, 3, .7f);
                }
                super.tick();
            }
        });
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, player -> !((Player) player).isCreative() && !((Player) player).isSpectator()));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof CopperSentinel) && !(li instanceof ToxicWisp)));
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
    protected void summonAnimation() {
        triggerAnim(COPPER_SENTINEL_CONTROLLER, startup);
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

    class ActionSelector extends Goal {
        @Override
        public boolean canUse() {
            LivingEntity target = CopperSentinel.this.getTarget();
            return target != null && target.isAlive()
                    && getBossAction() == 0
                    && getBossCooldown() <= 0
                    && !CopperSentinel.this.isInvulnerable();
        }

        @Override
        public void start() {
            LivingEntity target = CopperSentinel.this.getTarget();
            if (target == null) return;

            double dist = CopperSentinel.this.distanceTo(target);

            if (CopperSentinel.this.tickCount % EXPLODE_COOLDOWN == 0) {
                setBossAction(EXPLODE_ACTION);
            }
            else if (dist < 6) {
                setBossAction(MELEE_ACTION);
            }
            else if (dist >= 3.5 && dist <= 20) {
                setBossAction(RANGED_ACTION);
            }
            else {
                setBossAction(MELEE_ACTION);
            }
        }
    }

    class CopperSentinelMeleeAttackGoal extends TimedActionGoal {
        private static final float MELEE_DISTANCE = 18;
        private boolean hasAttacked;

        public CopperSentinelMeleeAttackGoal() {
            super(CopperSentinel.this, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected int actionId() {
            return MELEE_ACTION;
        }

        @Override
        protected int maxTime() {
            return 80;
        }

        @Override
        protected int startCooldown() {
            return MELEE_COOLDOWN;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && t < maxTime();
        }

        @Override
        protected void onStart(LivingEntity target) {
            hasAttacked = false;
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, slash);

            double d0 = target.getX() - CopperSentinel.this.getX();
            double d1 = target.getZ() - CopperSentinel.this.getZ();
            CopperSentinel.this.setYRot((float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90F);
            CopperSentinel.this.yBodyRot = CopperSentinel.this.getYRot();
            CopperSentinel.this.yHeadRot = CopperSentinel.this.getYRot();
        }

        @Override
        protected void onTick(LivingEntity target) {
            CopperSentinel.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!hasAttacked && t == 20) {
                if (CopperSentinel.this.distanceToSqr(target) < MELEE_DISTANCE * MELEE_DISTANCE
                        && CopperSentinel.this.getSensing().hasLineOfSight(target)) {
                    CopperSentinel.this.doHurtTarget(target);
                    CopperSentinel.this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2, 0.8f);
                }
                hasAttacked = true;
            }
        }

        @Override
        protected void onStop() {
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, walk);
        }
    }

    class CopperSentinelRangedAttackGoal extends TimedActionGoal {
        private int projectilesShot = 0;
        private static final int RANGED_PROJECTILES = 3;

        public CopperSentinelRangedAttackGoal() {
            super(CopperSentinel.this, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected int actionId() {
            return RANGED_ACTION;
        }

        @Override
        protected int maxTime() {
            return 50;
        }

        @Override
        protected int startCooldown() {
            return RANGED_COOLDOWN;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && t < maxTime();
        }

        @Override
        protected void onStart(LivingEntity target) {
            projectilesShot = 0;
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, shoot);

            double d0 = target.getX() - CopperSentinel.this.getX();
            double d1 = target.getZ() - CopperSentinel.this.getZ();
            CopperSentinel.this.setYRot((float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90F);
            CopperSentinel.this.yBodyRot = CopperSentinel.this.getYRot();
            CopperSentinel.this.yHeadRot = CopperSentinel.this.getYRot();
        }

        @Override
        protected void onTick(LivingEntity target) {
            CopperSentinel.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (projectilesShot < RANGED_PROJECTILES) {
                if (t == 17 + (projectilesShot * 5)) {
                    CopperSentinel.this.performRangedAttack(target, 1.0f);
                    projectilesShot++;
                }
            }
        }

        @Override
        protected void onStop() {
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, walk);
        }
    }

    class CopperSentinelExplodeGoal extends TimedActionGoal {
        private static final float EXPLODE_DISTANCE = 3;

        public CopperSentinelExplodeGoal() {
            super(CopperSentinel.this, EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected int actionId() {
            return EXPLODE_ACTION;
        }

        @Override
        protected int maxTime() {
            return 10;
        }

        @Override
        protected int startCooldown() {
            return EXPLODE_COOLDOWN;
        }

        @Override
        public boolean canContinueToUse() {
            return t < maxTime();
        }

        @Override
        protected void onStart(LivingEntity target) {
            CopperSentinel.this.level().explode(CopperSentinel.this,
                    CopperSentinel.this.getX(), CopperSentinel.this.getY() + 2,
                    CopperSentinel.this.getZ(), EXPLODE_DISTANCE, false,
                    Level.ExplosionInteraction.NONE);

            if (CopperSentinel.this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, CopperSentinel.this.getX(),
                        CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 20, 1, 1, 1, 0.1);
                serverLevel.sendParticles(ParticleTypes.SMOKE, CopperSentinel.this.getX(),
                        CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 50, 1, 1, 1, 0.05f);
                serverLevel.sendParticles(ParticleTypes.FLAME, CopperSentinel.this.getX(),
                        CopperSentinel.this.getY() + 2, CopperSentinel.this.getZ(), 30, 1, 1, 1, 0.05f);
            }

            BlockPos.betweenClosedStream(CopperSentinel.this.blockPosition().offset(-5, -3, -5),
                    CopperSentinel.this.blockPosition().offset(5, 10, 5)).forEach(pos -> {
                BlockState state = CopperSentinel.this.level().getBlockState(pos);
                if (state.is(WDBlocks.TOXIC_GAS.get())) {
                    ToxicGasBlock tgb = (ToxicGasBlock) state.getBlock();
                    tgb.Explode(CopperSentinel.this.level(), pos);
                }
            });
        }

        @Override
        protected void onTick(LivingEntity target) {
            if (t == 5) {
                ToxicWisp wisp = WDEntities.SMALL_TOXIC_WISP.get().create(CopperSentinel.this.level());
                Vec3 pos = CopperSentinel.this.position().add(
                        (CopperSentinel.this.level().random.nextDouble() - 0.5) * 8, 1,
                        (CopperSentinel.this.level().random.nextDouble() - 0.5) * 8);
                wisp.setPos(pos.x, pos.y, pos.z);
                CopperSentinel.this.level().addFreshEntity(wisp);

                Vec3 kb = wisp.position().subtract(CopperSentinel.this.position()).normalize().scale(1);
                wisp.setDeltaMovement(kb);
                CopperSentinel.this.playSound(SoundEvents.BEEHIVE_EXIT, 2, 0.7f);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}