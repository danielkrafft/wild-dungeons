package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.enchantment.WDEnchantments;
import com.danielkkrafft.wilddungeons.registry.WDEntities;
import com.danielkkrafft.wilddungeons.registry.WDParticleTypes;
import com.danielkkrafft.wilddungeons.registry.WDSoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Comparator;
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
    private static final double PUSH_OUT = 0.15;
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
        ).mul(1f);

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

    private void handleBounce(Vec3 oldPos, Vec3 newPos) {
        ClipContext ctx = new ClipContext(
                oldPos,
                newPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        );

        BlockHitResult hit = this.level().clip(ctx); // clip context
        if (hit.getType() != HitResult.Type.BLOCK) return;

        Direction side = hit.getDirection();
        Vec3 vel = this.getDeltaMovement();

        ItemStack stack = this.getStack();
        boolean hasHoming = stack.getEnchantmentLevel(WildDungeons.getEnchantment(WDEnchantments.HOMING)) > 0;


        //Thingy for the detection of the hit surface so it reflect the right position
        if (!hasHoming) {
            switch (side) {
                case UP, DOWN -> vel = new Vec3(vel.x, -vel.y, vel.z);
                case EAST, WEST -> vel = new Vec3(-vel.x, vel.y, vel.z);
                case NORTH, SOUTH -> vel = new Vec3(vel.x, vel.y, -vel.z);
            }
            vel = vel.scale(1f);
        }else {
            LivingEntity target = findNearestTarget(20);

            if (target != null) {
                Vec3 dir = target.position().add(0,0.7,0).subtract(this.position()).normalize();
                vel = dir.scale(1f);
            } else {
                vel = this.getDeltaMovement().scale(-0.75);
            }

        }
        this.setPos(hit.getLocation().add(Vec3.atLowerCornerOf(side.getNormal()).scale(PUSH_OUT)));
        this.setDeltaMovement(vel);
        this.hurtMarked = true;

        Vector3f newInit = new Vector3f((float) vel.x, (float) vel.y, (float) vel.z);
        this.initialVelocity = newInit;
        this.entityData.set(INITIAL_VEL, newInit);

        float pitch = 0.8f + (this.random.nextFloat() * 0.5f);

        this.level().playSound(null,
                hit.getLocation().x, hit.getLocation().y, hit.getLocation().z,
                WDSoundEvents.NAUTILUS_SHIELD_HIT, SoundSource.PLAYERS, 1.0f,
                pitch
        );


        Vec3 incoming = newPos.subtract(oldPos).normalize();
        Vec3 outgoing = vel.normalize();


        double tBase = incoming.dot(outgoing); // -1 to 1 range

        for (int i = 0; i < 15; i++) {

            double t = this.random.nextDouble() * 0.6 + 0.2;
            Vec3 dir = incoming.scale(1.0 - t).add(outgoing.scale(t)).normalize();

            dir = dir.add(
                    (this.random.nextDouble() - 0.5) * 0.3,
                    (this.random.nextDouble() - 0.5) * 0.3,
                    (this.random.nextDouble() - 0.5) * 0.3
            ).normalize();

            double speed = 0.2 + this.random.nextDouble() * 0.2;

            Vec3 sparkVel = dir.scale(speed);

            double posX = hit.getLocation().x + (this.random.nextDouble() - 0.5) * 0.2;
            double posY = hit.getLocation().y + (this.random.nextDouble() - 0.5) * 0.2;
            double posZ = hit.getLocation().z + (this.random.nextDouble() - 0.5) * 0.2;

            this.level().addParticle(
                    WDParticleTypes.SPARK_PARTICLE.get(),
                    posX, posY, posZ,
                    sparkVel.x, sparkVel.y, sparkVel.z
            );
        }
    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            if (this.owner == null) {
                Entity e = level().getEntity(this.entityData.get(OWNER_ID));
                if (e instanceof Player p) this.owner = p;
            }
            this.initialVelocity = this.entityData.get(INITIAL_VEL);
        }

        if (this.owner != null) {
            ItemStack stack = this.entityData.get(STACK);

            int range = stack.getEnchantmentLevel(WildDungeons.getEnchantment(WDEnchantments.RANGE)) * 10;

            int timeA = 8 + range;
            int timeB = 58 + range;



            if (stack.getEnchantmentLevel(WildDungeons.getEnchantment(Enchantments.LOYALTY)) > 0) {
                if (this.tickCount < timeA) {
                    this.setDeltaMovement(new Vec3(
                            this.initialVelocity.x(),
                            this.initialVelocity.y(),
                            this.initialVelocity.z()));
                    List<LivingEntity> livingList = level().getEntitiesOfClass(
                            LivingEntity.class, this.getBoundingBox().inflate(1f)
                    );
                    for (LivingEntity living : livingList) {
                        if (!living.equals(this.owner)) {
                            Vec3 dir = living.position().subtract(this.position()).normalize();
                            living.addDeltaMovement(dir.scale(0.2f));
                            if (living.hurt(this.owner.damageSources().mobAttack(living), 2f)) {
                                if (!this.owner.isCreative()) {
                                    ItemStack c = stack.copy();
                                    c.setDamageValue(stack.getDamageValue() + 1);
                                    this.entityData.set(STACK, c);
                                }
                            }
                        }
                    }
                } else if (this.tickCount < timeB) {
                    setDeltaMovement(Vec3.ZERO);
                    this.entityData.set(SPIN, true);

                    List<LivingEntity> livingList = level().getEntitiesOfClass(
                            LivingEntity.class, this.getBoundingBox().inflate(1f)
                    );
                    for (LivingEntity living : livingList) {
                        if (!living.getUUID().equals(this.owner.getUUID())) {
                            Vec3 dir = living.position().subtract(this.position()).normalize();
                            living.addDeltaMovement(dir.scale(0.2f));
                        if (living.hurt(this.owner.damageSources().mobAttack(living), 2f)) {
                            if (!this.owner.isCreative()) {
                                ItemStack c = stack.copy();
                                c.setDamageValue(stack.getDamageValue() + 1);
                                this.entityData.set(STACK, c);
                            }
                        }
                        }
                    }
                    this.spinTicks++;
                } else {
                    if (this.entityData.get(SPIN)) this.entityData.set(SPIN, false);
                    this.doLoyaltyReturn();
                    List<LivingEntity> livingList = level().getEntitiesOfClass(
                            LivingEntity.class, this.getBoundingBox().inflate(1f)
                    );
                    for (LivingEntity living : livingList) {
                        if (!living.getUUID().equals(this.owner.getUUID())) {
                            Vec3 dir = living.position().subtract(this.position()).normalize();
                            living.addDeltaMovement(dir.scale(0.2f));
                            if (living.hurt(this.owner.damageSources().mobAttack(living), 2f)) {
                                if (!this.owner.isCreative()) {
                                    ItemStack c = stack.copy();
                                    c.setDamageValue(stack.getDamageValue() + 1);
                                    this.entityData.set(STACK, c);
                                }
                            }
                        }
                    }
                }
            }



            else {
                if (this.tickCount < timeA) {
                    this.setDeltaMovement(new Vec3(
                            this.initialVelocity.x(),
                            this.initialVelocity.y(),
                            this.initialVelocity.z()
                    ));

                    List<LivingEntity> livingList = level().getEntitiesOfClass(
                            LivingEntity.class, this.getBoundingBox().inflate(1f)
                    );
                    for (LivingEntity living : livingList) {
                        if (!living.getUUID().equals(this.owner.getUUID())) {
                            Vec3 dir = living.position().subtract(this.position()).normalize();
                            living.addDeltaMovement(dir.scale(0.2f));
                            if (living.hurt(this.owner.damageSources().mobAttack(living), 2f)) {
                                if (!this.owner.isCreative()) {
                                    ItemStack c = stack.copy();
                                    c.setDamageValue(stack.getDamageValue() + 1);
                                    this.entityData.set(STACK, c);
                                }
                            }
                        }
                    }
                }

                else if (this.tickCount < timeB) {
                    setDeltaMovement(Vec3.ZERO);
                    this.entityData.set(SPIN, true);

                    List<LivingEntity> livingList = level().getEntitiesOfClass(
                            LivingEntity.class, this.getBoundingBox().inflate(1f)
                    );
                    for (LivingEntity living : livingList) {
                        if (!living.getUUID().equals(this.owner.getUUID())) {
                            Vec3 dir = living.position().subtract(this.position()).normalize();
                            living.addDeltaMovement(dir.scale(0.2f));
                        if (living.hurt(this.owner.damageSources().mobAttack(living), 2f)) {
                            if (!this.owner.isCreative()) {
                                ItemStack c = stack.copy();
                                c.setDamageValue(stack.getDamageValue() + 1);
                                this.entityData.set(STACK, c);
                            }
                        }
                        }
                    }
                    this.spinTicks++;
                }

                else {
                    if (this.shouldSpin()) {
                        this.entityData.set(SPIN, false);
                        if (!stack.isEmpty()) {
                            level().addFreshEntity(new ItemEntity(
                                    level(), getX(), getY(), getZ(), stack
                            ));
                        }
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
            }
            if (this.shouldSpin()) {
                List<LivingEntity> livingList = level().getEntitiesOfClass(
                        LivingEntity.class, this.getBoundingBox().inflate(0.2, 1.5, 0.2)
                );
                for (LivingEntity living : livingList) {
                    if (living instanceof Player player) {
                        Vec3 vec3 = player.getDeltaMovement();
                        if (vec3.y < (double) -0.58F) {
                            //System.out.println(vec3.y + " Y ");
                            player.addDeltaMovement(vec3.multiply(0, -2, 0));
                        }
                        if (Math.abs(vec3.x) > (double) 0.58F) {
                            //System.out.println(vec3.x + " X ");
                            player.addDeltaMovement(new Vec3(0,Math.abs(vec3.x) * 0.5,0));
                        } else if (Math.abs(vec3.z) > (double) 0.58F) {
                            //System.out.println(vec3.z + " Z ");
                            player.addDeltaMovement(new Vec3(0,Math.abs(vec3.z) * 0.5,0));
                        }
                    }
                }
            }

            Vec3 oldPos = this.position();
            Vec3 mov = getDeltaMovement();
            if (!this.shouldSpin()) {
                for (int i = 0; i < 2; i++) {
                    double posX = this.getX() + (this.random.nextDouble() - 0.5) * 0.2;
                    double posY = this.getY() + (this.random.nextDouble() - 0.5) * 0.2;
                    double posZ = this.getZ() + (this.random.nextDouble() - 0.5) * 0.2;
                    double velocityX = (this.random.nextDouble() - 0.5) * 0.1;
                    double velocityY = (this.random.nextDouble() - 0.5) * 0.1;
                    double velocityZ = (this.random.nextDouble() - 0.5) * 0.1;
                    this.level().addParticle(WDParticleTypes.SPARK_PARTICLE.get(),
                            posX,
                            posY,
                            posZ,
                            velocityX,
                            velocityY,
                            velocityZ);
                }
            }
            this.setOldPosAndRot();
            Vec3 newPos = this.position().add(mov);
            if (this.tickCount < timeB) handleBounce(oldPos, newPos);
            mov = this.getDeltaMovement();
            this.setPos(
                    getX() + mov.x,
                    getY() + mov.y,
                    getZ() + mov.z
            );

        }

        super.tick();
    }

    private void doLoyaltyReturn() {
        if (this.owner == null || !this.owner.isAlive()) return;
        Vec3 toOwner = this.owner.position().add(0, 1.0, 0).subtract(this.position());
        double dist = toOwner.length();
        if (dist < 1.5) {
            ItemStack stack = this.entityData.get(STACK);
            if (!stack.isEmpty()) {
                if (!this.owner.addItem(stack)) {
                    this.level().addFreshEntity(new ItemEntity(level(), this.owner.getX(), this.owner.getY(), this.owner.getZ(), stack));
                }
            }
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        Vec3 dir = toOwner.normalize();
        Vec3 newVel = dir.scale(0.65);
        this.setDeltaMovement(newVel);
        Vector3f newInit = new Vector3f((float)newVel.x, (float)newVel.y, (float)newVel.z);
        this.entityData.set(INITIAL_VEL, newInit);
        this.initialVelocity = newInit;
    }


    private LivingEntity findNearestTarget(double radius) {

        return this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(radius),
                        e -> e != this.getOwner() && e.isAlive()
                )
                .stream()
                .filter(this::hasLineOfSightTo)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .orElse(null);
    }

    private boolean hasLineOfSightTo(Entity target) {
        Vec3 eye = this.position().add(0, this.getBbHeight() * 0.5, 0);
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);

        BlockHitResult hit = this.level().clip(new ClipContext(
                eye,
                targetPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        return hit.getType() == HitResult.Type.MISS || hit.getBlockPos() == null;
    }

    public boolean shouldSpin() {
        return this.entityData.get(SPIN);
    }

    public ItemStack getStack() {
        return this.entityData.get(STACK);
    }

    public boolean isLoyal() {
        return this.entityData.get(LOYAL);
    }

    public Player getOwner() {
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}