package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.util.UtilityMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity.AttackPhase.*;

public class NetherDragonEntity extends WDBoss implements GeoEntity {
    private BlockPos anchorPoint, spawnPoint = BlockPos.ZERO;
    private static final EntityDataAccessor<Vector3f> MOVE_TARGET_POINT = SynchedEntityData.defineId(NetherDragonEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Integer> ATTACK_PHASE = SynchedEntityData.defineId(NetherDragonEntity.class, EntityDataSerializers.INT);
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

    public NetherDragonEntity(EntityType<? extends WDBoss> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);
        this.setMoveTargetPoint(new Vector3f(0,0,0));
        this.anchorPoint = BlockPos.ZERO;
        this.setAttackPhase(CIRCLE);
        this.xpReward = 200;
        this.summonTicks = 50;
        this.moveControl = new NetherDragonMoveControl(this);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setAnchorAboveTarget();
        this.spawnPoint = this.blockPosition();
        bossEvent.setVisible(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WDBoss.WDBossSummonGoal(this));
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
    protected boolean isImmuneToDamageType(DamageSource source) {
        return source.is(DamageTypes.FALL);
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return true;
    }

    protected void saveBossData(CompoundTag compound) {
        compound.putInt("AX", this.anchorPoint.getX());
        compound.putInt("AY", this.anchorPoint.getY());
        compound.putInt("AZ", this.anchorPoint.getZ());
        compound.putInt("AP", this.getAttackPhase().ordinal());
        compound.putDouble("MTX", this.getMoveTargetPoint().x());
        compound.putDouble("MTY", this.getMoveTargetPoint().y());
        compound.putDouble("MTZ", this.getMoveTargetPoint().z());
        compound.putInt("SX", this.spawnPoint.getX());
        compound.putInt("SY", this.spawnPoint.getY());
        compound.putInt("SZ", this.spawnPoint.getZ());
    }

    protected void loadBossData(CompoundTag compound) {
        if (compound.contains("AX")) {
            this.anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }
        if (compound.contains("AP")) {
            this.setAttackPhase(AttackPhase.values()[compound.getInt("AP")]);
        }
        if (compound.contains("MTX")) {
            this.setMoveTargetPoint(new Vector3f((float) compound.getDouble("MTX"), (float) compound.getDouble("MTY"), (float) compound.getDouble("MTZ")));
        }
        if (compound.contains("SX")) {
            this.spawnPoint = new BlockPos(compound.getInt("SX"), compound.getInt("SY"), compound.getInt("SZ"));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_PHASE, 0);
        builder.define(MOVE_TARGET_POINT, new Vector3f(0,0,0));
    }

    public void setAttackPhase(AttackPhase attackPhase) {
        this.entityData.set(ATTACK_PHASE, attackPhase.ordinal());
    }

    public AttackPhase getAttackPhase() {
        return AttackPhase.values()[this.entityData.get(ATTACK_PHASE)];
    }

    public void setMoveTargetPoint(Vector3f pos) {
        this.entityData.set(MOVE_TARGET_POINT, pos);
    }

    public Vector3f getMoveTargetPoint() {
        return this.entityData.get(MOVE_TARGET_POINT);
    }

    private static final BossSounds SOUNDS = new BossSounds(
            SoundEvents.ENDER_DRAGON_AMBIENT,
            SoundEvents.ENDER_DRAGON_AMBIENT,
            SoundEvents.ENDER_DRAGON_DEATH
    );

    @Override
    protected BossSounds bossSounds() {
        return SOUNDS;
    }

    @Override
    protected void spawnSummonParticles(Vec3 pos) {
        UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.EXPLOSION_EMITTER, true, 1, pos.x, pos.y, pos.z, 0, 0, 0, 0);
        UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.LAVA, true, 200, pos.x, pos.y, pos.z, 2, 2, 2, 0.06f);
        UtilityMethods.sendParticles((ServerLevel) NetherDragonEntity.this.level(), ParticleTypes.FLAME, true, 400, pos.x, pos.y, pos.z, 4, 4, 4, 0.08f);
    }

    @Override
    public void tick() {
        super.tick();
        updateBossBar();
    }

    public void StopAttackAnimations(){
        triggerAnim(ATTACKCONTROLLER,idle);
        AttackController.stop();
    }

    public enum AttackPhase {
        CIRCLE,
        SWOOP,
        FIREBALL
    }

    private void setAnchorAboveTarget() {
        LivingEntity livingentity = NetherDragonEntity.this.getTarget();
        BlockPos blockpos = livingentity != null ? livingentity.blockPosition() : NetherDragonEntity.this.blockPosition();
        this.anchorPoint = blockpos.above(2+ this.random.nextInt(10));
    }

    @Override
    protected MoveControl createMoveControlFor(Locomotion mode) {
        if (mode == Locomotion.AERIAL) return new NetherDragonMoveControl(this);
        return super.createMoveControlFor(mode);
    }

    @Override
    protected Locomotion defaultLocomotion() {
        return Locomotion.AERIAL;
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

        private int ticksTryingToGoUp = 0;
        public void tick() {
            checkWalls(NetherDragonEntity.this.getBoundingBox().inflate(0.1));
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

            if (NetherDragonEntity.this.random.nextInt(this.adjustedTickDelay(100)) == 0) {
                this.angle = NetherDragonEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (NetherDragonEntity.this.getMoveTargetPoint().y() > NetherDragonEntity.this.getY()){
                if (++ticksTryingToGoUp > 100){
                    this.height -= -4.0F;
                    ticksTryingToGoUp = 0;
                    selectNext();
                }
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

            AtomicReference<Vec3> vec3 = new AtomicReference<>(Vec3.atLowerCornerOf(NetherDragonEntity.this.anchorPoint).add(this.distance * Mth.cos(this.angle) * 3, this.height, this.distance * Mth.sin(this.angle) * 3));

            // Check if the target position is inside a solid block
            BlockPos targetPos = BlockPos.containing(vec3.get());
            if (!NetherDragonEntity.this.level().isEmptyBlock(targetPos)) {
                vec3.set(spawnPoint.getBottomCenter());
            }
            NetherDragonEntity.this.setMoveTargetPoint(vec3.get().toVector3f());
        }
    }

    private boolean checkWalls(AABB area) {//directly ripped from the EnderDragon
        int i = Mth.floor(area.minX);
        int j = Mth.floor(area.minY);
        int k = Mth.floor(area.minZ);
        int l = Mth.floor(area.maxX);
        int i1 = Mth.floor(area.maxY);
        int j1 = Mth.floor(area.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for(int k1 = i; k1 <= l; ++k1) {
            for(int l1 = j; l1 <= i1; ++l1) {
                for(int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() && !blockstate.is(BlockTags.DRAGON_TRANSPARENT)) {
                        if (CommonHooks.canEntityDestroy(this.level(), blockpos, this) && !blockstate.is(BlockTags.DRAGON_IMMUNE)) {
                            flag1 = this.level().removeBlock(blockpos, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPos blockpos1 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));
            this.level().levelEvent(2008, blockpos1, 0);
        }

        return flag;
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
}