package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PrimalCreeper extends Monster implements GeoEntity, RangedAttackMob {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
    private static final String PRIMAL_CREEPER_CONTROLLER = "primal_creeper_controller";
    private final AnimationController<PrimalCreeper> mainController = new AnimationController<>(this, PRIMAL_CREEPER_CONTROLLER, 5,state ->
            state.setAndContinue(idleAnim));
    private static final String
            idle = "animation.model.idle", //The Idle Animation
            walk = "animation.model.walk", //The Walking Animation (Quite Slow)
            run = "animation.model.run", //The Running Animation (Normal Pace)
            dash = "animation.model.dash", //The Dashing Animation (Fast Pace)
            throwing = "animation.model.throw"; //Throw Animation for TNT Projectiles
    private static final RawAnimation
            idleAnim = RawAnimation.begin().thenLoop(idle),
            walkAnim = RawAnimation.begin().thenLoop(walk),
            runAnim = RawAnimation.begin().thenLoop(run),
            dashAnim = RawAnimation.begin().thenLoop(dash),
            throwingAnim = RawAnimation.begin().thenLoop(throwing);
    // All animations are made using part Molang and Bézier for Action type Animation

    private static final EntityDataAccessor<Integer> CURRENT_ACTION =
            SynchedEntityData.defineId(PrimalCreeper.class, EntityDataSerializers.INT);


    public PrimalCreeper(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Ocelot.class, 6.0F, (double)1.0F, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Cat.class, 6.0F, (double)1.0F, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, (double)1.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(1,new ThoughtSelector(this));
        this.goalSelector.addGoal(1,new ThrowTNTGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
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
    public void performRangedAttack(LivingEntity livingEntity, float v) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(mainController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_ACTION,0);
    }

    public int getCurrentAction() {
        return this.entityData.get(CURRENT_ACTION);
    }
    public void setCurrentAction(int actionID) {
        this.entityData.set(CURRENT_ACTION, actionID);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
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


    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.removePlayer(serverPlayer);
    }

    protected class ThoughtSelector extends Goal {

        private PrimalCreeper entity;

        public ThoughtSelector(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            if (!this.entity.isAlive()) return false;
            if (this.entity.getCurrentAction() != 0) return false;
            return true;
        }


    }
    protected class ThrowTNTGoal extends Goal {

        public static final int ID = 1;

        private int tickCount = 0;
        private PrimalCreeper entity;

        public ThrowTNTGoal(PrimalCreeper entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return this.entity.getCurrentAction() == ID;
        }

        @Override
        public void tick() {
            super.tick();
            this.tickCount++;
        }
    }

}
