package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.registry.WDEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ThrownNautilusShield extends Entity {

    private static final EntityDataAccessor<Boolean> LOYAL =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOMING =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPIN =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> OWNER_ID =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> INITIAL_VEL =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<ItemStack> STACK =
            SynchedEntityData.defineId(ThrownNautilusShield.class, EntityDataSerializers.ITEM_STACK);

    private Player owner;
    private Vector3f initialVelocity = new Vector3f();
    public int spinTicks = 0;

    public ThrownNautilusShield(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownNautilusShield(Level level, boolean loyal, boolean homing, Player owner, ItemStack stack) {
        this(WDEntities.THROWN_NAUTILUS_SHIELD.get(), level);

        this.owner = owner;
        Vec3 pV = this.owner.getDeltaMovement();
        this.initialVelocity = new Vector3f(
                (float) (owner.getLookAngle().x + pV.x),
                (float) (owner.getLookAngle().y + pV.y),
                (float) (owner.getLookAngle().z + pV.z)
        ).mul(0.5f);

        this.entityData.set(STACK, stack);
        this.entityData.set(LOYAL, loyal);
        this.entityData.set(HOMING, homing);
        this.entityData.set(SPIN, false);
        this.entityData.set(OWNER_ID, owner.getId());
        this.entityData.set(INITIAL_VEL, initialVelocity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STACK, ItemStack.EMPTY);
        builder.define(LOYAL, false);
        builder.define(HOMING, false);
        builder.define(SPIN, false);
        builder.define(OWNER_ID, -1);
        builder.define(INITIAL_VEL, new Vector3f());
    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            if (owner == null) {
                Entity e = level().getEntity(this.entityData.get(OWNER_ID));
                if (e instanceof Player p) owner = p;
            }
            initialVelocity = this.entityData.get(INITIAL_VEL);
        }

        if (owner != null) {
            ItemStack stack = this.entityData.get(STACK);
            if (tickCount < 8) {
                setDeltaMovement(
                        new Vec3(initialVelocity.x(), initialVelocity.y(), initialVelocity.z())
                );
                List<LivingEntity> livingList = this.level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(1f));
                for (LivingEntity living : livingList) {
                    if (!living.equals(owner)) {
                        Vec3 dir = living.position().subtract(this.position()).normalize();
                        living.addDeltaMovement(dir.scale(0.2f));
                        if (living.hurt(owner.damageSources().mobAttack(living),2f)) {
                            ItemStack c = stack.copy();
                            c.setDamageValue(stack.getDamageValue() + 1);
                            this.entityData.set(STACK,c);
                        }
                    }
                }
            } else if (tickCount < 48) {
                setDeltaMovement(Vec3.ZERO);
                this.entityData.set(SPIN, true);
                List<LivingEntity> livingList = this.level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(1f));
                for (LivingEntity living : livingList) {
                    Vec3 dir = living.position().subtract(this.position()).normalize();
                    living.addDeltaMovement(dir.scale(0.2f));
                    if (living.hurt(owner.damageSources().mobAttack(living),2f)) {
                        ItemStack c = stack.copy();
                        c.setDamageValue(stack.getDamageValue() + 1);
                        this.entityData.set(STACK,c);
                    }
                }
                this.spinTicks++;
            } else {
                if (this.shouldSpin()) {
                    this.entityData.set(SPIN, false);
                    if (!this.entityData.get(STACK).isEmpty()) {
                        this.level().addFreshEntity(new ItemEntity(
                                this.level(), this.getX(), this.getY(), this.getZ(), this.entityData.get(STACK)
                        ));
                    }
                    this.remove(RemovalReason.DISCARDED);
                }
            }

            Vec3 mov = getDeltaMovement();
            this.setOldPosAndRot();
            this.setPos(getX() + mov.x, getY() + mov.y, getZ() + mov.z);
            this.hurtMarked = true;
        }

        super.tick();
    }

    public boolean shouldSpin() {
        return this.entityData.get(SPIN);
    }

    public ItemStack getStack() {
        return this.entityData.get(STACK);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.shouldSpin();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}


}