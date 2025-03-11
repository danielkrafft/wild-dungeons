package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity.AttackPhase.*;

public class NetherDragonEntity extends FlyingMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);
    private BlockPos anchorPoint = BlockPos.ZERO;
    private static final EntityDataAccessor<Vector3f> MOVE_TARGET_POINT = SynchedEntityData.defineId(NetherDragonEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> ATTACK_PHASE = SynchedEntityData.defineId(NetherDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_INVULNERABLE = SynchedEntityData.defineId(NetherDragonEntity.class,EntityDataSerializers.INT);
    private static final int SUMMON_TICKS = 50;//5s
    private static final String
            FLIGHTCONTROLLER = "NetherDragonFlightController",
            ATTACKCONTROLLER = "NetherDragonAttackController";
    private static final String
            idle = "idle",
            fly = "fly",
            fireball = "idle",//todo should be replaced with an actual fireball animation that blends with the fly animation probably
            sweep = "dive",
            glide = "glide";
    private static final RawAnimation
            flyAnim = RawAnimation.begin().thenLoop(fly),
            fireballAnim = RawAnimation.begin().thenPlay(fireball),
            sweepAnim = RawAnimation.begin().thenLoop(sweep),
            glideAnim = RawAnimation.begin().thenLoop(glide),
            idleAnim = RawAnimation.begin().thenLoop(idle);
    //<editor-fold desc="Core Mob Properties">
    public NetherDragonEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.setMoveTargetPoint(new Vector3f(0,0,0));
        this.anchorPoint = BlockPos.ZERO;
        this.setAttackPhase(CIRCLE);
        this.xpReward = 200;
        this.moveControl = new NetherDragonMoveControl(this);
        setPersistenceRequired();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setAnchorAboveTarget();
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SummonGoal(this));
        //todo death goal like EnderDragon
        goalSelector.addGoal(1, new NetherDragonEntityAttackStrategyGoal());
        goalSelector.addGoal(2, new NetherDragonEntitySweepAttackGoal());
        goalSelector.addGoal(2, new NetherDragonEntityFireBallTargetGoal());
        goalSelector.addGoal(3, new NetherDragonEntityCircleAroundAnchorGoal());
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 30));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof NetherDragonEntity)));
        targetSelector.addGoal(1, new NetherDragonEntityTargetPlayerGoal());
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 225).
                add(Attributes.MOVEMENT_SPEED, 0.5).
                add(Attributes.FOLLOW_RANGE, 50).
                add(Attributes.ATTACK_DAMAGE, 10).
                add(Attributes.ATTACK_KNOCKBACK, 2).
                add(Attributes.KNOCKBACK_RESISTANCE, 0.4).
                add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 0.2).
                add(Attributes.FLYING_SPEED, 2).
                build();
    }

    private final AnimationController<NetherDragonEntity> FlightController = new AnimationController<>(this,FLIGHTCONTROLLER,5,this::FlightPredicate);
    private final AnimationController<NetherDragonEntity> AttackController = new AnimationController<>(this,ATTACKCONTROLLER,5,
            state->state.setAndContinue(idleAnim))
            .triggerableAnim(idle, idleAnim)
            .triggerableAnim(fireball, fireballAnim)
            .triggerableAnim(sweep, sweepAnim);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(FlightController);
        controllers.add(AttackController);
    }

    private <E extends GeoAnimatable> PlayState FlightPredicate(AnimationState<E> event) {
        NetherDragonEntity dragonEntity = (NetherDragonEntity) event.getAnimatable();
        switch (dragonEntity.getAttackPhase()) {
            case CIRCLE -> {
                if (dragonEntity.getMoveTargetPoint().y() > dragonEntity.getY()) {
                    event.setAnimation(flyAnim);
                } else {
                    event.setAnimation(glideAnim);
                }
                return PlayState.CONTINUE;
            }
            case SWOOP -> {
                event.setAnimation(glideAnim);
                return PlayState.CONTINUE;
            }
            case FIREBALL -> {
                event.setAnimation(flyAnim);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public boolean mayBeLeashed() {
        return false;
    }

    @Override
    public boolean canHaveALeashAttachedToIt() {
        return false;
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        compound.putInt("AX", this.anchorPoint.getX());
        compound.putInt("AY", this.anchorPoint.getY());
        compound.putInt("AZ", this.anchorPoint.getZ());
        compound.putInt("AP", this.getAttackPhase().ordinal());
        compound.putInt("TI", this.getTicksInvulnerable());
        compound.putDouble("MTX", this.getMoveTargetPoint().x());
        compound.putDouble("MTY", this.getMoveTargetPoint().y());
        compound.putDouble("MTZ", this.getMoveTargetPoint().z());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        if (compound.contains("AX")) {
            this.anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }
        if (compound.contains("AP")) {
            this.setAttackPhase(AttackPhase.values()[compound.getInt("AP")]);
        }
        if (compound.contains("TI")) {
            this.setTicksInvulnerable(compound.getInt("TI"));
        }
        if (compound.contains("MTX")) {
            this.setMoveTargetPoint(new Vector3f((float) compound.getDouble("MTX"), (float) compound.getDouble("MTY"), (float) compound.getDouble("MTZ")));
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation path = new FlyingPathNavigation(this, level);
        path.setCanFloat(true);
        path.setCanPassDoors(true);
        return path;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_PHASE, 0);
        builder.define(TICKS_INVULNERABLE,0);
        builder.define(MOVE_TARGET_POINT, new Vector3f(0,0,0));
    }

    public void setAttackPhase(AttackPhase attackPhase) {
        this.entityData.set(ATTACK_PHASE, attackPhase.ordinal());
    }

    public AttackPhase getAttackPhase() {
        return AttackPhase.values()[this.entityData.get(ATTACK_PHASE)];
    }

    public void setTicksInvulnerable(int ticks){
        this.entityData.set(TICKS_INVULNERABLE,ticks);
    }

    public int getTicksInvulnerable(){
        return this.entityData.get(TICKS_INVULNERABLE);
    }

    public void setMoveTargetPoint(Vector3f pos) {
        this.entityData.set(MOVE_TARGET_POINT, pos);
    }

    public Vector3f getMoveTargetPoint() {
        return this.entityData.get(MOVE_TARGET_POINT);
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;//todo
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return getAmbientSound();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_DEATH;//todo
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
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
    public void tick() {
        super.tick();
        Level level = getCommandSenderWorld();
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        if (!level.isClientSide && !isDeadOrDying()) {
            //logic
//            WildDungeons.getLogger().info("Current Target: {}", getTarget());
//            WildDungeons.getLogger().info("Nether Dragon attack phase: {}", attackPhase);
//            WildDungeons.getLogger().info("Nether Dragon Y: {}, desired Y {}", getY(), moveTargetPoint.y);
        }

    }
// </editor-fold>
    public void StopAttackAnimations(){
        triggerAnim(ATTACKCONTROLLER,idle);
        AttackController.stop();
    }

    //<editor-fold desc="Goals">
    public enum AttackPhase {
        CIRCLE,
        SWOOP,
        FIREBALL
    }

    private void setAnchorAboveTarget() {
        LivingEntity livingentity = NetherDragonEntity.this.getTarget();
        BlockPos blockpos = livingentity != null ? livingentity.blockPosition() : NetherDragonEntity.this.blockPosition();
        this.anchorPoint = blockpos.above(10 + this.random.nextInt(10));
    }

    public boolean isInvulnerable() {
        return getTicksInvulnerable() <= SUMMON_TICKS;
    }

    public class SummonGoal extends Goal {
        public SummonGoal(@NotNull Mob mob) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
        }

        @Override
        public void tick() {
            int ticks = NetherDragonEntity.this.getTicksInvulnerable();
            NetherDragonEntity.this.setTicksInvulnerable(NetherDragonEntity.this.getTicksInvulnerable() + 1);
            if (ticks % 10 == 0)
                NetherDragonEntity.this.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 2f, 2f);
        }

        @Override
        public void start() {
            NetherDragonEntity.this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 2f, 0.7f);
            NetherDragonEntity.this.setInvulnerable(true);
        }

        @Override
        public boolean canUse() {
            return NetherDragonEntity.this.isInvulnerable();
        }

        @Override
        public void stop() {
            //psuedo explosion
            Vec3 pos = NetherDragonEntity.this.position();
            List<LivingEntity> list = NetherDragonEntity.this.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(NetherDragonEntity.this.position(), 10, 10, 10), NetherDragonEntity.this::hasLineOfSight);
            for (LivingEntity li : list) {
                Vec3 kb = new Vec3(pos.x - li.position().x, pos.y - li.position().y, pos.z - li.position().z).
                        normalize().scale(2);
                li.knockback(1.5, kb.x, kb.z);
                li.setRemainingFireTicks(li.getRemainingFireTicks() + 100);
                li.hurt(new DamageSource(NetherDragonEntity.this.level().damageSources().generic().typeHolder()), 10);
            }
            NetherDragonEntity.this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 2f, 0.8f);
            UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
            UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.LAVA, true, 200, pos.x, pos.y, pos.z, 2, 2, 2, 0.06f);
            UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.FLAME, true, 400, pos.x, pos.y, pos.z, 4, 4, 4, 0.08f);
            NetherDragonEntity.this.setInvulnerable(false);
        }
    }


    class NetherDragonMoveControl extends MoveControl {
        private float speed = 0.1F;

        public NetherDragonMoveControl(Mob mob) {
            super(mob);
        }

        public void tick() {
            if (NetherDragonEntity.this.horizontalCollision) {
                NetherDragonEntity.this.setYRot(NetherDragonEntity.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }
            if (NetherDragonEntity.this.getAttackPhase() == FIREBALL) {
                //slow down the dragon when it is charging up a fireball
                this.speed = Mth.approach(this.speed, 0.1F, 0.5f);
            }

            double xDir = NetherDragonEntity.this.getMoveTargetPoint().x() - NetherDragonEntity.this.getX();
            double yDir = NetherDragonEntity.this.getMoveTargetPoint().y() - NetherDragonEntity.this.getY();
            double zDir = NetherDragonEntity.this.getMoveTargetPoint().z() - NetherDragonEntity.this.getZ();
            double distance = Math.sqrt(xDir * xDir + zDir * zDir);
            float oldRot = NetherDragonEntity.this.getYRot();
            if (Math.abs(distance) > (double) 1.0E-5F) {
                double horizontalAdjustmentFactor = (double) 1.0F - Math.abs(yDir * (double) 0.7F) / distance;
                xDir *= horizontalAdjustmentFactor;
                zDir *= horizontalAdjustmentFactor;
                distance = Math.sqrt(xDir * xDir + zDir * zDir);
                double distance3D = Math.sqrt(xDir * xDir + zDir * zDir + yDir * yDir);
                NetherDragonEntity.this.setYRot(Mth.approachDegrees(Mth.wrapDegrees(NetherDragonEntity.this.getYRot() + 90.0F), Mth.wrapDegrees((float) Mth.atan2(zDir, xDir) * (180F / (float) Math.PI)), 4.0F) - 90.0F);
                NetherDragonEntity.this.yBodyRot = NetherDragonEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(oldRot, NetherDragonEntity.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 4f, 0.005F * (2f / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 1f, 0.025F);
                }

                float pitchAngle = (float) (-(Mth.atan2(-yDir, distance) * (double) 180.0F / (double) (float) Math.PI));
                NetherDragonEntity.this.setXRot(pitchAngle);
                float yawAngle = NetherDragonEntity.this.getYRot() + 90.0F;
                double xMovement = (double) (this.speed * Mth.cos(yawAngle * ((float) Math.PI / 180F))) * Math.abs(xDir / distance3D);
                double yMovement = (double) (this.speed * Mth.sin(pitchAngle * ((float) Math.PI / 180F))) * Math.abs(yDir / distance3D);
                double zMovement = (double) (this.speed * Mth.sin(yawAngle * ((float) Math.PI / 180F))) * Math.abs(zDir / distance3D);
                Vec3 vec3 = NetherDragonEntity.this.getDeltaMovement();
                NetherDragonEntity.this.setDeltaMovement(vec3.add((new Vec3(xMovement, yMovement, zMovement)).subtract(vec3).scale(0.2)));
            }
        }
    }


    class NetherDragonEntityAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        NetherDragonEntityAttackStrategyGoal() {
        }

        public boolean canUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            return livingentity != null && NetherDragonEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT.range(100));
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            NetherDragonEntity.this.setAttackPhase(CIRCLE);
            NetherDragonEntity.this.setAnchorAboveTarget();
        }

        public void stop() {
            setAnchorAboveTarget();
        }

        public void tick() {
            if (NetherDragonEntity.this.getAttackPhase() == CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    this.nextSweepTick = this.adjustedTickDelay((8 + NetherDragonEntity.this.random.nextInt(3)) * 20);
                    NetherDragonEntity.this.setAttackPhase(NetherDragonEntity.this.random.nextBoolean() ? SWOOP : FIREBALL);
                    NetherDragonEntity.this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 10.0F, 0.95F + NetherDragonEntity.this.random.nextFloat() * 0.1F);//todo
                    switch (NetherDragonEntity.this.getAttackPhase()) {
                        case SWOOP:
                            NetherDragonEntity.this.setAnchorAboveTarget();
                            break;
                        case FIREBALL:
                            NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.blockPosition();
                            break;
                    }
                }
            }
        }
    }


    abstract class NetherDragonEntityMoveTargetGoal extends Goal {
        public NetherDragonEntityMoveTargetGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        protected boolean touchingTarget() {
            Vector3f target = NetherDragonEntity.this.getMoveTargetPoint();
            Vec3 vec3 = new Vec3(target.x(), target.y(), target.z());
            return vec3.distanceToSqr(NetherDragonEntity.this.getX(), NetherDragonEntity.this.getY(), NetherDragonEntity.this.getZ()) < (double) 12.0F;
        }
    }

    class NetherDragonEntityCircleAroundAnchorGoal extends NetherDragonEntityMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        NetherDragonEntityCircleAroundAnchorGoal() {
        }

        public boolean canUse() {
            return NetherDragonEntity.this.getTarget() == null || NetherDragonEntity.this.getAttackPhase() == CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + NetherDragonEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + NetherDragonEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = NetherDragonEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (NetherDragonEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + NetherDragonEntity.this.random.nextFloat() * 9.0F;
            }

            if (NetherDragonEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 50) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (NetherDragonEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = NetherDragonEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (NetherDragonEntity.this.getMoveTargetPoint().y() < NetherDragonEntity.this.getY() && !NetherDragonEntity.this.level().isEmptyBlock(NetherDragonEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (NetherDragonEntity.this.getMoveTargetPoint().y() > NetherDragonEntity.this.getY() && !NetherDragonEntity.this.level().isEmptyBlock(NetherDragonEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(NetherDragonEntity.this.anchorPoint)) {
                NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            NetherDragonEntity.this.setMoveTargetPoint(Vec3.atLowerCornerOf(NetherDragonEntity.this.anchorPoint).add(this.distance * Mth.cos(this.angle) * 3, this.height, this.distance * Mth.sin(this.angle) * 3).toVector3f());
        }
    }

    class NetherDragonEntitySweepAttackGoal extends NetherDragonEntityMoveTargetGoal {

        NetherDragonEntitySweepAttackGoal() {
        }

        public boolean canUse() {
            return NetherDragonEntity.this.getTarget() != null && NetherDragonEntity.this.getAttackPhase() == SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player) {
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                return this.canUse();
            }
        }

        public void start() {
        }

        public void stop() {
            NetherDragonEntity.this.setTarget(null);
            NetherDragonEntity.this.setAttackPhase(CIRCLE);
            StopAttackAnimations();
        }

        public void tick() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity != null) {
                NetherDragonEntity.this.setMoveTargetPoint(new Vec3(livingentity.getX(), livingentity.getY(0.5F), livingentity.getZ()).toVector3f());
                if (NetherDragonEntity.this.getBoundingBox().inflate(4).intersects(livingentity.getBoundingBox())) {
                    triggerAnim(ATTACKCONTROLLER,sweep);
                } else {
                    StopAttackAnimations();
                }
                if (NetherDragonEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                    NetherDragonEntity.this.doHurtTarget(livingentity);
                    NetherDragonEntity.this.setAttackPhase(CIRCLE);
                    setAnchorAboveTarget();
                    if (!NetherDragonEntity.this.isSilent()) {
                        NetherDragonEntity.this.level().levelEvent(1039, NetherDragonEntity.this.blockPosition(), 0);
                    }
                } else if (NetherDragonEntity.this.horizontalCollision || NetherDragonEntity.this.hurtTime > 0) {
                    NetherDragonEntity.this.setAttackPhase(CIRCLE);
                    StopAttackAnimations();
                    setAnchorAboveTarget();
                }
            }

        }
    }

    class NetherDragonEntityFireBallTargetGoal extends Goal {
        public int chargeTime;
        private int firedFireballs;
        private int initialFireballDelay;

        NetherDragonEntityFireBallTargetGoal() {
            this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return NetherDragonEntity.this.getTarget() != null && NetherDragonEntity.this.getAttackPhase() == FIREBALL;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player) {
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                return this.canUse();
            }
        }

        public void start() {
            firedFireballs = 0;
        }

        public void stop() {
            NetherDragonEntity.this.setTarget(null);
            NetherDragonEntity.this.setAttackPhase(CIRCLE);
            StopAttackAnimations();
        }

        public void tick() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity != null) {
                //face the target
                NetherDragonEntity.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                //get the direction to the target, then rotate the body to face that direction
                Vec3 vec3 = NetherDragonEntity.this.getViewVector(1.0F);
                double d0 = livingentity.getX() - (NetherDragonEntity.this.getX() + vec3.x * 4.0);
                double d1 = livingentity.getY(0.5) - (0.5 + NetherDragonEntity.this.getY(0.5));
                double d2 = livingentity.getZ() - (NetherDragonEntity.this.getZ() + vec3.z * 4.0);
                float rotation = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI) - 90.0F);
                //set the dragons rotation to face the target smoothly
                NetherDragonEntity.this.setYRot(Mth.approachDegrees(NetherDragonEntity.this.getYRot(), rotation, 10.0F));

                if (++initialFireballDelay < 60) return;
                if (initialFireballDelay == 60 && !NetherDragonEntity.this.isSilent()) {
                    //todo fireball charging noise
//                NetherDragonEntity.this.setCharging(true);//todo this could be used to make the dragon look like it is charging up a fireball instead of just floating there
                }

                Level level = NetherDragonEntity.this.level();
                this.chargeTime++;

                if (this.chargeTime == 10) {
                    Vec3 vec31 = new Vec3(d0, d1, d2);
                    if (!NetherDragonEntity.this.isSilent()) {
                        level.playSound(null, NetherDragonEntity.this.blockPosition(), SoundEvents.ENDER_DRAGON_SHOOT, NetherDragonEntity.this.getSoundSource(), 2.0F, (NetherDragonEntity.this.random.nextFloat() - NetherDragonEntity.this.random.nextFloat()) * 0.2F + 1.0F);
                    }

                    LargeFireball largefireball = new LargeFireball(level, NetherDragonEntity.this, vec31.normalize(), 1);
                    Vec3 headPos = new Vec3(NetherDragonEntity.this.getX() + vec3.x * 2.0, NetherDragonEntity.this.getY(0.5), largefireball.getZ() + vec3.z * 2.0)
                            .add(vec3.scale(2.5).add(firedFireballs == 0 ? Vec3.ZERO : vec3.cross(new Vec3(0, 1, 0)).normalize().scale(2.5 * (firedFireballs == 1 ? -1 : 1))));
                    largefireball.setPos(headPos);
                    level.addFreshEntity(largefireball);
                    triggerAnim(ATTACKCONTROLLER,fireball);
                    this.chargeTime = 0;
                    if (++this.firedFireballs == 3) {
                        NetherDragonEntity.this.setAttackPhase(CIRCLE);
                        StopAttackAnimations();
                        setAnchorAboveTarget();
                        this.firedFireballs = 0;
                        this.initialFireballDelay = 0;
                    }

                }
            } else {
                this.chargeTime = 0;
                this.firedFireballs = 0;
                this.initialFireballDelay = 0;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }


    class NetherDragonEntityTargetPlayerGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0F);
        private int nextScanTick = reducedTickDelay(20);

        NetherDragonEntityTargetPlayerGoal() {
        }

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = NetherDragonEntity.this.level().getNearbyPlayers(this.attackTargeting, NetherDragonEntity.this, NetherDragonEntity.this.getBoundingBox().inflate(16.0F, 64.0F, 16.0F));
                if (!list.isEmpty()) {
                    list.sort((p1, p2) -> NetherDragonEntity.this.random.nextInt(3) - 1);
                    for (Player player : list) {
                        if (NetherDragonEntity.this.canAttack(player, attackTargeting)) {
                            NetherDragonEntity.this.setTarget(player);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            return livingentity != null && NetherDragonEntity.this.canAttack(livingentity, attackTargeting);
        }
    }
    // </editor-fold>
}