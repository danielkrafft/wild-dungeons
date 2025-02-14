package com.danielkkrafft.wilddungeons.entity.boss;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
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

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import static com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity.AttackPhase.CIRCLE;
import static com.danielkkrafft.wilddungeons.entity.boss.NetherDragonEntity.AttackPhase.SWOOP;

public class NetherDragonEntity extends FlyingMob implements RangedAttackMob, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_20);
    private Vec3 moveTargetPoint = Vec3.ZERO;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private AttackPhase attackPhase = CIRCLE;

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
        this.anchorPoint = this.blockPosition().above(15);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1,new NetherDragonEntityAttackStrategyGoal());
        goalSelector.addGoal(2, new NetherDragonEntitySweepAttackGoal());
        goalSelector.addGoal(3, new NetherDragonEntityCircleAroundAnchorGoal());
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 30));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, li -> !(li instanceof NetherDragonEntity)));
        targetSelector.addGoal(1, new NetherDragonEntityAttackPlayerTargetGoal());
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
    public void performRangedAttack(LivingEntity livingEntity, float v) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder)
    {
        super.defineSynchedData(builder);
//        builder.define(TICKSINVULNERABLE,0);
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getAmbientSound() { return WDSoundEvents.BREEZE_GOLEM_AMBIENT.value(); }//todo

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return getAmbientSound();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() { return WDSoundEvents.BREEZE_GOLEM_DEATH.value(); }//todo

    @Override
    public @NotNull SoundSource getSoundSource() { return SoundSource.HOSTILE; }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) { bossEvent.addPlayer(serverPlayer); }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getCommandSenderWorld();
        bossEvent.setVisible(true);
        float hp = getHealth() / getMaxHealth();
        bossEvent.setProgress(hp);
        if (!level.isClientSide && !isDeadOrDying()) {
            //logic
        }
        WildDungeons.getLogger().info("Nether Dragon attack phase: {}", attackPhase);
        WildDungeons.getLogger().info("Nether Dragon Y: {}, desired Y {}", getY(), moveTargetPoint.y);
    }

    enum AttackPhase {
        CIRCLE,
        SWOOP;

        AttackPhase() {
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

            double d0 = NetherDragonEntity.this.moveTargetPoint.x - NetherDragonEntity.this.getX();
            double d1 = NetherDragonEntity.this.moveTargetPoint.y - NetherDragonEntity.this.getY();
            double d2 = NetherDragonEntity.this.moveTargetPoint.z - NetherDragonEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = (double)1.0F - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = NetherDragonEntity.this.getYRot();
                float f1 = (float) Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(NetherDragonEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                NetherDragonEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                NetherDragonEntity.this.yBodyRot = NetherDragonEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, NetherDragonEntity.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 3f, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 1f, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)180.0F / (double)(float)Math.PI));
                NetherDragonEntity.this.setXRot(f4);
                float f5 = NetherDragonEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = NetherDragonEntity.this.getDeltaMovement();
                NetherDragonEntity.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2)));
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
            this.setAnchorAboveTarget();
        }

        public void stop() {
            NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, NetherDragonEntity.this.anchorPoint).above(20 + NetherDragonEntity.this.random.nextInt(20));
        }

        public void tick() {
            if (NetherDragonEntity.this.attackPhase == CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    NetherDragonEntity.this.attackPhase = SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((100 + NetherDragonEntity.this.random.nextInt(5)) * 20);
                    NetherDragonEntity.this.playSound(WDSoundEvents.BREEZE_GOLEM_WALK.value(), 10.0F, 0.95F + NetherDragonEntity.this.random.nextFloat() * 0.1F);//todo
                }
            }
        }

        private void setAnchorAboveTarget() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity==null){
                NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.blockPosition().above(10 + NetherDragonEntity.this.random.nextInt(10));
                return;
            }
            NetherDragonEntity.this.anchorPoint = NetherDragonEntity.this.getTarget().blockPosition().above(10 + NetherDragonEntity.this.random.nextInt(10));
        }
    }

    abstract class NetherDragonEntityMoveTargetGoal extends Goal {
        public NetherDragonEntityMoveTargetGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return NetherDragonEntity.this.moveTargetPoint.distanceToSqr(NetherDragonEntity.this.getX(), NetherDragonEntity.this.getY(), NetherDragonEntity.this.getZ()) < (double)4.0F;
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
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (NetherDragonEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = NetherDragonEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
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

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            NetherDragonEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(NetherDragonEntity.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    class NetherDragonEntitySweepAttackGoal extends NetherDragonEntity.NetherDragonEntityMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        NetherDragonEntitySweepAttackGoal() {
        }

        public boolean canUse() {
            return NetherDragonEntity.this.getTarget() != null && NetherDragonEntity.this.attackPhase == SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player) {
                    Player player = (Player)livingentity;
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (NetherDragonEntity.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = NetherDragonEntity.this.tickCount + 20;
                        List<Cat> list = NetherDragonEntity.this.level().getEntitiesOfClass(Cat.class, NetherDragonEntity.this.getBoundingBox().inflate((double)16.0F), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void start() {
        }

        public void stop() {
            NetherDragonEntity.this.setTarget((LivingEntity)null);
            NetherDragonEntity.this.attackPhase = CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            if (livingentity != null) {
                NetherDragonEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY((double)0.5F), livingentity.getZ());
                if (NetherDragonEntity.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    NetherDragonEntity.this.doHurtTarget(livingentity);
                    NetherDragonEntity.this.attackPhase = CIRCLE;
                    if (!NetherDragonEntity.this.isSilent()) {
                        NetherDragonEntity.this.level().levelEvent(1039, NetherDragonEntity.this.blockPosition(), 0);
                    }
                } else if (NetherDragonEntity.this.horizontalCollision || NetherDragonEntity.this.hurtTime > 0) {
                    NetherDragonEntity.this.attackPhase = CIRCLE;
                }
            }

        }
    }

    class NetherDragonEntityAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range((double)64.0F);
        private int nextScanTick = reducedTickDelay(20);

        NetherDragonEntityAttackPlayerTargetGoal() {
        }

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = NetherDragonEntity.this.level().getNearbyPlayers(this.attackTargeting, NetherDragonEntity.this, NetherDragonEntity.this.getBoundingBox().inflate((double)16.0F, (double)64.0F, (double)16.0F));
                if (!list.isEmpty()) {
                    for(Player player : list) {
                        if (NetherDragonEntity.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            NetherDragonEntity.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = NetherDragonEntity.this.getTarget();
            return livingentity != null ? NetherDragonEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }
    }
}