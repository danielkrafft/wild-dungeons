package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
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

public class CopperSentinel extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_6);
    private static final String COPPER_SENTINEL_CONTROLLER = "copper_sentinel_controller";
    private final AnimationController<CopperSentinel> mainController = new AnimationController<>(this, COPPER_SENTINEL_CONTROLLER, 5,
            state -> state.setAndContinue(idleAnim))
            .triggerableAnim(startup, startupAnim)
            .triggerableAnim(walk, walkAnim)
            .triggerableAnim(shoot, shootAnim)
            .triggerableAnim(slash, slashAnim)
            .triggerableAnim(idle, idleAnim);
    private static final String
            idle = "idle",
            startup = "animation.model.transform",
            walk = "walk",
            shoot = "shoot",
            slash = "slash";
    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            startupAnim = RawAnimation.begin().thenPlay(startup).thenLoop(idle),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            shootAnim = RawAnimation.begin().thenPlay(shoot).thenLoop(idle),
            slashAnim = RawAnimation.begin().thenPlay(slash).thenLoop(idle);
    private static final EntityDataAccessor<Integer> TICKS_INVULNERABLE = SynchedEntityData.defineId(CopperSentinel.class, EntityDataSerializers.INT);
    private static final int SUMMON_TICKS = 80;

    public CopperSentinel(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SummonGoal(this));

        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof CopperSentinel)));
    }

    @Override
    public void tick() {
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        super.tick();
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TI", this.getTicksInvulnerable());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }

        if (compound.contains("TI")) {
            this.setTicksInvulnerable(compound.getInt("TI"));
        }
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

    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 50)
                .add(Attributes.ATTACK_DAMAGE, 8)
                .add(Attributes.ATTACK_KNOCKBACK, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(mainController);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public class SummonGoal extends Goal {//todo time this to the animation

        public SummonGoal(@NotNull Mob mob) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
        }

        @Override
        public void tick() {
            int ticks = CopperSentinel.this.getTicksInvulnerable();
            CopperSentinel.this.setTicksInvulnerable(CopperSentinel.this.getTicksInvulnerable() + 1);
            if (ticks % 20 == 0)
                CopperSentinel.this.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2, 2f);
        }

        @Override
        public void start() {
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, startup);
            CopperSentinel.this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 2, 0.7f);
            CopperSentinel.this.setInvulnerable(true);
        }

        @Override
        public boolean canUse() {
            return CopperSentinel.this.isInvulnerable();
        }

        @Override
        public void stop() {
            //pseudo explosion
            Vec3 pos = CopperSentinel.this.position();
            List<LivingEntity> list = CopperSentinel.this.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(CopperSentinel.this.position(), 10, 10, 10), CopperSentinel.this::hasLineOfSight);
            list.remove(CopperSentinel.this);
            for (LivingEntity li : list) {
                Vec3 kb = new Vec3(pos.x - li.position().x, pos.y - li.position().y, pos.z - li.position().z).
                        normalize().scale(2);
                li.knockback(1.5, kb.x, kb.z);
                li.setRemainingFireTicks(li.getRemainingFireTicks() + 100);
                li.hurt(new DamageSource(CopperSentinel.this.level().damageSources().generic().typeHolder()), 10);
            }
            CopperSentinel.this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 2f, 0.8f);
            UtilityMethods.sendParticles((ServerLevel) CopperSentinel.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
            UtilityMethods.sendParticles((ServerLevel) CopperSentinel.this.level(), ParticleTypes.LAVA, true, 200, pos.x, pos.y, pos.z, 1, 2, 1, 0.06f);
            CopperSentinel.this.setInvulnerable(false);
            CopperSentinel.this.triggerAnim(COPPER_SENTINEL_CONTROLLER, idle);
        }
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if ((source.getEntity() instanceof ToxicWisp)) {
            return super.hurt(source, 100);
        }
        return false;
    }
}
