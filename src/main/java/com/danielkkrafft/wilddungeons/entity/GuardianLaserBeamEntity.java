package com.danielkkrafft.wilddungeons.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GuardianLaserBeamEntity extends Entity implements Targeting, OwnableEntity {

    private static final EntityDataAccessor<Integer> OWNER_ID =
            SynchedEntityData.defineId(GuardianLaserBeamEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(GuardianLaserBeamEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ATTACK_TIME =
            SynchedEntityData.defineId(GuardianLaserBeamEntity.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity cachedOwner;
    @Nullable
    private LivingEntity cachedTarget;

    public GuardianLaserBeamEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
        builder.define(TARGET_ID, -1);
        builder.define(ATTACK_TIME, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    public int getAttackDuration() {
        return 80;
    }

    public float getClientSideAttackTime() {
        return this.entityData.get(ATTACK_TIME);
    }

    public float getAttackAnimationScale(float partialTick) {
        return (this.getClientSideAttackTime() + partialTick) / this.getAttackDuration();
    }


    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.entityData.set(ATTACK_TIME, this.entityData.get(ATTACK_TIME) + 1);
        }

        LivingEntity owner = getOwner();
        if (owner != null) {
            this.setOldPosAndRot();
            this.setPos(owner.getX(), owner.getEyeY() - 0.15, owner.getZ());
        }

        if (!this.level().isClientSide && getTarget() == null) {
            findTarget();
        }

        if (!this.level().isClientSide && this.tickCount >= 60) {
            if (owner != null && getTarget() != null) {
                getTarget().hurt(owner.damageSources().mobAttack(owner), 3.5f);
            }
            this.discard();
        }
    }

    private void findTarget() {
        double best = Double.MAX_VALUE;
        LivingEntity found = null;

        for (LivingEntity e : this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(8, 4, 8))) {

            if (e instanceof Enemy && e.isAlive()) {
                double d = e.distanceTo(this);
                if (d < best) {
                    best = d;
                    found = e;
                }
            }
        }

        setTarget(found);
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        if (cachedOwner == null) {
            int id = this.entityData.get(OWNER_ID);
            Entity e = id == -1 ? null : this.level().getEntity(id);
            if (e instanceof LivingEntity le) {
                cachedOwner = le;
            }
        }
        return cachedOwner;
    }

    public void setOwner(LivingEntity owner) {
        this.cachedOwner = owner;
        this.entityData.set(OWNER_ID, owner.getId());
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if (cachedTarget == null) {
            int id = this.entityData.get(TARGET_ID);
            Entity e = id == -1 ? null : this.level().getEntity(id);
            if (e instanceof LivingEntity le) {
                cachedTarget = le;
            }
        }
        return cachedTarget;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.cachedTarget = target;
        this.entityData.set(TARGET_ID, target != null ? target.getId() : -1);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        LivingEntity owner = getOwner();
        return owner != null ? owner.getUUID() : null;
    }
}