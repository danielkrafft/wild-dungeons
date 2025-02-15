package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

import static com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity.AttackPhase.*;

public class NetherDragonEntity extends FlyingMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_20);
    private Vec3 moveTargetPoint = Vec3.ZERO;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private AttackPhase attackPhase = CIRCLE;

    //<editor-fold desc="Core Mob Properties">
    public NetherDragonEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveTargetPoint = Vec3.ZERO;
        this.anchorPoint = BlockPos.ZERO;
        this.attackPhase = CIRCLE;
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
        goalSelector.addGoal(1, new NetherDragonEntityAttackStrategyGoal());
        goalSelector.addGoal(2, new NetherDragonEntitySweepAttackGoal());
        goalSelector.addGoal(3, new NetherDragonEntityCircleAroundAnchorGoal());
        goalSelector.addGoal(3, new NetherDragonEntityFireBallTargetGoal());
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
                add(Attributes.FLYING_SPEED, 1).
                build();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        //todo animation controllers
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
//        builder.define(TICKSINVULNERABLE,0);
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
//            WildDungeons.getLogger().info("Nether Dragon attack phase: {}", attackPhase);
//            WildDungeons.getLogger().info("Nether Dragon Y: {}, desired Y {}", getY(), moveTargetPoint.y);
        }

    }
// </editor-fold>

    //<editor-fold desc="Goals">
    enum AttackPhase {
        CIRCLE,
        SWOOP,
        FIREBALL
    }

    private void setAnchorAboveTarget() {
        LivingEntity livingentity = NetherDragonEntity.this.getTarget();
        BlockPos blockpos = livingentity != null ? livingentity.blockPosition() : NetherDragonEntity.this.blockPosition();
        this.anchorPoint = blockpos.above(15 + this.random.nextInt(10));
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
            if (NetherDragonEntity.this.attackPhase == FIREBALL) {
                //slow down the dragon when it is charging up a fireball
                this.speed = Mth.approach(this.speed, 0.1F, 0.5f);
            }

            double xDir = NetherDragonEntity.this.moveTargetPoint.x - NetherDragonEntity.this.getX();
            double yDir = NetherDragonEntity.this.moveTargetPoint.y - NetherDragonEntity.this.getY();
            double zDir = NetherDragonEntity.this.moveTargetPoint.z - NetherDragonEntity.this.getZ();
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
                    this.speed = Mth.approach(this.speed, 2f, 0.005F * (2f / this.speed));
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
            NetherDragonEntity.this.attackPhase = CIRCLE;
            NetherDragonEntity.this.setAnchorAboveTarget();
        }

        public void stop() {
            setAnchorAboveTarget();
        }

        public void tick() {
            if (NetherDragonEntity.this.attackPhase == CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    this.nextSweepTick = this.adjustedTickDelay((8 + NetherDragonEntity.this.random.nextInt(3)) * 20);
                    NetherDragonEntity.this.attackPhase = NetherDragonEntity.this.random.nextBoolean() ? SWOOP : FIREBALL;
                    NetherDragonEntity.this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 10.0F, 0.95F + NetherDragonEntity.this.random.nextFloat() * 0.1F);//todo
                    switch (NetherDragonEntity.this.attackPhase) {
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
            return NetherDragonEntity.this.moveTargetPoint.distanceToSqr(NetherDragonEntity.this.getX(), NetherDragonEntity.this.getY(), NetherDragonEntity.this.getZ()) < (double) 4.0F;
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
            return NetherDragonEntity.this.getTarget() == null || NetherDragonEntity.this.attackPhase == CIRCLE;
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

            if (NetherDragonEntity.this.moveTargetPoint.y < NetherDragonEntity.this.getY() && !NetherDragonEntity.this.level().isEmptyBlock(NetherDragonEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (NetherDragonEntity.this.moveTargetPoint.y > NetherDragonEntity.this.getY() && !NetherDragonEntity.this.level().isEmptyBlock(NetherDragonEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(NetherDragonEntity.this.anchorPoint)) {
                NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float) Math.PI / 180F);
            NetherDragonEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(NetherDragonEntity.this.anchorPoint).add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    class NetherDragonEntitySweepAttackGoal extends NetherDragonEntityMoveTargetGoal {

        NetherDragonEntitySweepAttackGoal() {
        }

        public boolean canUse() {
            return NetherDragonEntity.this.getTarget() != null && NetherDragonEntity.this.attackPhase == SWOOP;
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
            NetherDragonEntity.this.attackPhase = CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity != null) {
                NetherDragonEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5F), livingentity.getZ());
                if (NetherDragonEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                    NetherDragonEntity.this.doHurtTarget(livingentity);
                    NetherDragonEntity.this.attackPhase = CIRCLE;
                    setAnchorAboveTarget();
                    if (!NetherDragonEntity.this.isSilent()) {
                        NetherDragonEntity.this.level().levelEvent(1039, NetherDragonEntity.this.blockPosition(), 0);
                    }
                } else if (NetherDragonEntity.this.horizontalCollision || NetherDragonEntity.this.hurtTime > 0) {
                    NetherDragonEntity.this.attackPhase = CIRCLE;
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
            return NetherDragonEntity.this.getTarget() != null && NetherDragonEntity.this.attackPhase == FIREBALL;
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
            NetherDragonEntity.this.attackPhase = CIRCLE;
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
                    //todo set the dragons state to charging to make it look like it is charging up in the animations
//                NetherDragonEntity.this.setCharging(true);

                }

                Level level = NetherDragonEntity.this.level();
                this.chargeTime++;

                if (this.chargeTime == 10) {
                    Vec3 vec31 = new Vec3(d0, d1, d2);
                    if (!NetherDragonEntity.this.isSilent()) {
                        level.playSound(null, NetherDragonEntity.this.blockPosition(), SoundEvents.ENDER_DRAGON_SHOOT, NetherDragonEntity.this.getSoundSource(), 2.0F, (NetherDragonEntity.this.random.nextFloat() - NetherDragonEntity.this.random.nextFloat()) * 0.2F + 1.0F);
                    }

                    LargeFireball largefireball = new LargeFireball(level, NetherDragonEntity.this, vec31.normalize(), 1);
                    largefireball.setPos(NetherDragonEntity.this.getX() + vec3.x * 4.0, NetherDragonEntity.this.getY(0.5) + 0.5, largefireball.getZ() + vec3.z * 4.0);
                    level.addFreshEntity(largefireball);
                    this.chargeTime = 0;
                    if (++this.firedFireballs == 3) {
                        NetherDragonEntity.this.attackPhase = CIRCLE;
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