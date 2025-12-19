package com.danielkkrafft.wilddungeons.entity.boss;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class CondemnedGuardianSegment extends PathfinderMob implements GeoEntity {
    private int headEntityId = -1;
    private int segmentIndex = 0;
    private int nextEntityId = -1;
    private int prevEntityId = -1;
    private int followEntityId = -1;

    private static final double POSITION_LERP_SPEED = 0.85D;
    private static final double SEGMENT_SPACING = 1.1D;

    private static final EntityDataAccessor<Integer> DATA_SEGMENT_INDEX = SynchedEntityData.defineId(CondemnedGuardianSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_NEXT_ENTITY_ID = SynchedEntityData.defineId(CondemnedGuardianSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PREV_ENTITY_ID = SynchedEntityData.defineId(CondemnedGuardianSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FOLLOW_ENTITY_ID = SynchedEntityData.defineId(CondemnedGuardianSegment.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_VULNERABLE = SynchedEntityData.defineId(CondemnedGuardianSegment.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CondemnedGuardianSegment(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
        this.noPhysics = true;
        //this.maxUpStep = 1.0f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SEGMENT_INDEX, 0);
        builder.define(DATA_NEXT_ENTITY_ID, -1);
        builder.define(DATA_PREV_ENTITY_ID, -1);
        builder.define(DATA_FOLLOW_ENTITY_ID, -1);
        builder.define(DATA_IS_VULNERABLE, false);
    }

    public void setHeadEntityId(int id) {
        this.headEntityId = id;
    }

    public void setSegmentIndex(int index) {
        this.segmentIndex = index;
        this.entityData.set(DATA_SEGMENT_INDEX, index);
    }

    public int getIndex() {
        return this.entityData.get(DATA_SEGMENT_INDEX);
    }

    public void setNextEntityId(int id) {
        this.nextEntityId = id;
        this.entityData.set(DATA_NEXT_ENTITY_ID, id);
    }

    public void setPrevEntityId(int id) {
        this.prevEntityId = id;
        this.entityData.set(DATA_PREV_ENTITY_ID, id);
    }

    public void setFollowEntityId(int id) {
        this.followEntityId = id;
        this.entityData.set(DATA_FOLLOW_ENTITY_ID, id);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.tickCount> 2) {
            if (this.nextEntityId == -1 && this.prevEntityId == -1) this.remove(RemovalReason.DISCARDED);
        }
        setNoGravity(isInWater());

        if (isInWater()) {
            setAirSupply(getMaxAirSupply());
        }

        if (!level().isClientSide) {
            updatePositionAndRotation();
        }

        updateVulnerability();
    }

    private void updatePositionAndRotation() {
        Entity followEntity = getFollowEntity();
        if (followEntity == null || !followEntity.isAlive()) {
            return;
        }

        Vec3 targetPos = calculateBezierPosition(followEntity);

        if (isInWater()) {
            targetPos = new Vec3(targetPos.x, followEntity.getY(), targetPos.z);
        }

        Vec3 currentPos = position();
        Vec3 direction = targetPos.subtract(currentPos);
        double distance = direction.length();

        if (distance > 0.1D) {
            direction = direction.normalize();
            Vec3 newPos = currentPos.add(direction.scale(Math.min(distance * POSITION_LERP_SPEED, 0.4D)));
            teleportTo(newPos.x, newPos.y, newPos.z);
        }
        Entity prevEntity = getPrevEntity();
        if (prevEntity != null && prevEntity.isAlive()) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, prevEntity.position().add(0,1.5,0));
        }
    }

    private Vec3 calculateBezierPosition(Entity followEntity) {
        Entity prevEntity = getPrevEntity();

        if (prevEntity == null || !prevEntity.isAlive() || prevEntity instanceof CondemnedGuardian) {
            Vec3 followDir = followEntity.getLookAngle().normalize();
            return followEntity.position().add(followDir.reverse().scale(SEGMENT_SPACING * this.segmentIndex));
        }
        Vec3 prevPos = prevEntity.position();
        Vec3 prevDir = new Vec3(
                Math.cos(Math.toRadians(prevEntity.getYRot() + 90)),
                0,
                Math.sin(Math.toRadians(prevEntity.getYRot() + 90))
        ).normalize();

        return prevPos.add(prevDir.reverse().scale(SEGMENT_SPACING));
    }

    @Nullable
    private Entity getFollowEntity() {
        int id = entityData.get(DATA_FOLLOW_ENTITY_ID);
        if (id == -1) return null;
        return level().getEntity(id);
    }

    @Nullable
    private Entity getPrevEntity() {
        int id = entityData.get(DATA_PREV_ENTITY_ID);
        if (id == -1) return null;
        return level().getEntity(id);
    }

    @Nullable
    private Entity getNextEntity() {
        int id = entityData.get(DATA_NEXT_ENTITY_ID);
        if (id == -1) return null;
        return level().getEntity(id);
    }

    private void updateVulnerability() {
        boolean isVulnerable = isTailSegment() || !isNextSegmentAlive();
        entityData.set(DATA_IS_VULNERABLE, isVulnerable);
    }

    private boolean isTailSegment() {
        return entityData.get(DATA_NEXT_ENTITY_ID) == -1;
    }

    private boolean isNextSegmentAlive() {
        int nextId = entityData.get(DATA_NEXT_ENTITY_ID);
        if (nextId == -1) return false;

        Entity nextEntity = level().getEntity(nextId);
        return nextEntity != null && nextEntity.isAlive();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof CondemnedGuardian) return super.hurt(source, amount);
        if (source.is(DamageTypes.GENERIC_KILL)) return super.hurt(source, amount);
        if (this.getIndex() % 3 != 0 || this.getIndex() > 10) return false;

        boolean result = super.hurt(source, amount);

        if (result && isDeadOrDying() && !level().isClientSide) {
            handleDeath(source);
        }

        return result;
    }

    private void handleDeath(DamageSource source) {
        if (this.headEntityId != -1) {
            Entity headEntity = level().getEntity(this.headEntityId);
            if (headEntity instanceof CondemnedGuardian head) {
                head.hurt(this.damageSources().mobAttack(this),110.5F);
                this.level().playLocalSound(this, SoundEvents.WITHER_HURT, SoundSource.HOSTILE,1,1);
                this.level().explode(this,this.getX(),this.getY(),this.getZ(),0.2f, Level.ExplosionInteraction.MOB);
                head.onSegmentDeath(this);
            }
        }
    }

    @Override
    public int getMaxAirSupply() {
        return 300;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (isInWater()) {
            setAirSupply(getMaxAirSupply());
        }
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "segment_controller", 5, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}