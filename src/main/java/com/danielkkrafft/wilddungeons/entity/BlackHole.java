package com.danielkkrafft.wilddungeons.entity;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.entity.BaseClasses.SelfGovernedEntity;
import com.danielkkrafft.wilddungeons.registry.WDDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BlackHole extends SelfGovernedEntity {

    // --- Size & Decay ---
    private float size = 1.0f;
    private static final float MIN_SIZE = 0.1f;
    private static final float MAX_SIZE = 2.5f;
    private static final float GROWTH_PER_CONSUME = 0.01f;
    private static final float SHRINK_RATE = 0.001f;
    private static final float BASE_DECAY_RATE = 0.001f;
    private static final float MAX_DECAY_MULTIPLIER = 8.0f;
    private static final int DECAY_GRACE_PERIOD_TICKS = 10;
    private int decayCooldownTicks = 0;

    // --- Pull & Damage ---
    private static final double RADIUS_PULL_SCALE = 4;
    private static final double RADIUS_EFFECT_SCALE = 2;
    private static final int MIN_DAMAGE = 1;
    private static final int MAX_DAMAGE = 10;
    private static final double MIN_PULL_STRENGTH = 1;
    private static final double MAX_PULL_STRENGTH = 5;
    private static final double PULL_BASE_MULTIPLIER = 3;
    private static final double PULL_FALLOFF_EXPONENT = 2;

    // --- Timing & Iteration ---
    private static final int EAT_COOLDOWN_BASE = 3;
    private static final int ENTITY_DAMAGE_COOLDOWN_TICKS = 10;
    private static final int BLOCK_CHECK_BATCH_SIZE = 750;
    private int eatCooldown = 0;
    private Iterator<BlockPos> blockIterator = null;
    private final Map<Integer, Integer> damageCooldowns = new HashMap<>();

    // --- Entity Data ---
    private static final EntityDataAccessor<Float> DATA_BLACK_HOLE_SIZE = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);

    public BlackHole(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (wasFired) {
            if (!level().isClientSide) {
                if (getSize() >= MAX_SIZE) {
                    handleDecay();
                } else {
                    handleMoving();
                }

                handleEating();

                if (getSize() <= MIN_SIZE) {
                    triggerCollapseEffect();
                    WildDungeons.getLogger().warn("BLACK HOLE DESTROYED!");
                    discard();
                    return;
                }
            }
        }
        super.tick();
    }

    private void handleMoving() {
        size = Mth.clamp(getSize() - SHRINK_RATE, MIN_SIZE, MAX_SIZE);
        setSize(size);

        if (getSize() >= MAX_SIZE) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        float baseSpeed = 0.25f;
        float minSpeed = 0.02f;
        float speed = Mth.clamp(baseSpeed - (size * 0.01f), minSpeed, baseSpeed);

        Vec3 motion = firedDirection.normalize().scale(speed);
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        lerpMotion(motion.x, motion.y, motion.z);
    }

    private void handleDecay() {
        if (decayCooldownTicks > 0) decayCooldownTicks--;

        float decayAmount = BASE_DECAY_RATE;

        // Only accelerate decay if size is *at or above* max
        if (getSize() >= MAX_SIZE) {
            float cooldownProgress = 1.0f - (decayCooldownTicks / (float) DECAY_GRACE_PERIOD_TICKS);
            float decayMultiplier = Mth.lerp(cooldownProgress, 1.0f, MAX_DECAY_MULTIPLIER);
            decayAmount *= decayMultiplier;
        }

        setSize(getSize() - decayAmount);
    }

    private void handleEating() {
        damageCooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
        if (--eatCooldown > 0) return;
        eatCooldown = EAT_COOLDOWN_BASE;

        Vec3 motion = getDeltaMovement();
        if (!motion.equals(Vec3.ZERO)) {
            Vec3 direction = motion.normalize();
            for (int i = 1; i <= 3; i++) {
                BlockPos stepPos = BlockPos.containing(position().add(direction.scale(i * 0.3)));
                BlockState state = level().getBlockState(stepPos);
                FluidState fluid = state.getFluidState();
                if (!level().isEmptyBlock(stepPos)) {
                    if (!fluid.isEmpty()) {
                        level().setBlockAndUpdate(stepPos, Fluids.EMPTY.defaultFluidState().createLegacyBlock());
                        level().addParticle(ParticleTypes.CLOUD, stepPos.getX() + 0.5, stepPos.getY() + 0.5, stepPos.getZ() + 0.5, 0, 0.1, 0);
                    } else if (state.getDestroySpeed(level(), stepPos) >= 0) {
                        level().destroyBlock(stepPos, false);
                        onEatThing();
                        flashAbsorbEffect(stepPos);
                    }
                }
            }
        }

        double pullRadius = getSize() * RADIUS_PULL_SCALE;
        double effectRadius = getSize() * RADIUS_EFFECT_SCALE;
        double pullDistSq = pullRadius * pullRadius;
        double effectDistSq = effectRadius * effectRadius;
        BlockPos center = blockPosition();
        Vec3 pullCenter = position().add(0, 0.75, 0);
        AABB zone = new AABB(center).inflate(pullRadius);

        List<Entity> entities = level().getEntities(this, zone);
        for (Entity target : entities) {
            if (target == this) continue;
            double distSq = target.position().distanceToSqr(pullCenter);
            double normDist = distSq / pullDistSq;

            if (distSq <= effectDistSq) {
                BlockPos flashPos = target.blockPosition().above();
                if (target instanceof LivingEntity living) {
                    int id = living.getId();
                    if (damageCooldowns.getOrDefault(id, 0) <= 0) {
                        float damage = Mth.lerp(1.0f - (float)(distSq / effectDistSq), MIN_DAMAGE, MAX_DAMAGE);
                        living.hurt(blackHoleDamage(level().damageSources()), damage);
                        onEatThing();
                        damageCooldowns.put(id, ENTITY_DAMAGE_COOLDOWN_TICKS);
                        flashAbsorbEffect(flashPos);
                    }
                } else {
                    target.discard();
                    onEatThing();
                    flashAbsorbEffect(flashPos);
                }
            } else {
                Vec3 pullDir = pullCenter.subtract(target.position()).normalize();
                double baseStrength = PULL_BASE_MULTIPLIER * (1.0 - Math.pow(normDist, PULL_FALLOFF_EXPONENT));
                double pullStrength = Mth.clamp(baseStrength, MIN_PULL_STRENGTH, MAX_PULL_STRENGTH) * (1.0 - normDist);

                Vec3 currentMotion = target.getDeltaMovement().scale(0.85);
                Vec3 pullVelocity = pullDir.scale(pullStrength);
                Vec3 newMotion = currentMotion.add(pullVelocity).scale(0.5);
                target.setDeltaMovement(newMotion);
            }
        }

        if (blockIterator == null || !blockIterator.hasNext()) {
            BlockPos min = center.offset(-(int) pullRadius, -(int) pullRadius, -(int) pullRadius);
            BlockPos max = center.offset((int) pullRadius, (int) pullRadius, (int) pullRadius);
            blockIterator = BlockPos.betweenClosedStream(min, max).iterator();
        }

        int processed = 0;
        while (blockIterator.hasNext() && processed++ < BLOCK_CHECK_BATCH_SIZE) {
            BlockPos pos = blockIterator.next();
            BlockState state = level().getBlockState(pos);
            FluidState fluid = state.getFluidState();
            if (level().isEmptyBlock(pos)) continue;

            double distSq = pos.distSqr(center);
            if (distSq > effectDistSq) continue;

            double normDist = distSq / effectDistSq;
            double chance = Mth.clamp(1.0 - Math.pow(normDist, 2.2), 0.05, 1.0);

            if (!fluid.isEmpty()) {
                level().setBlockAndUpdate(pos, Fluids.EMPTY.defaultFluidState().createLegacyBlock());
                level().addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
            } else if (state.getDestroySpeed(level(), pos) >= 0 && level().random.nextDouble() < chance) {
                level().destroyBlock(pos, false);
                onEatThing();
                flashAbsorbEffect(pos);
            }
        }
    }

    private void flashAbsorbEffect(BlockPos pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    pos.getY() + 0.5 + (serverLevel.random.nextDouble() * 0.3),
                    pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    1, // count
                    0, 0, 0, 0 // no velocity spread
            );
        }
    }

    private void triggerCollapseEffect() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    getX(), getY(), getZ(),
                    1,  // particle count
                    0, 0, 0,  // x/y/z offset (spread)
                    0        // speed
            );
        }
    }

    public static DamageSource blackHoleDamage(DamageSources sources) {
        return sources.source(WDDamageTypes.BLACKHOLE.getKey());
    }

    public void onEatThing() {
        setSize(getSize() + GROWTH_PER_CONSUME);
        decayCooldownTicks = DECAY_GRACE_PERIOD_TICKS;
    }

    public void setFiredDirectionAndSpeed(Vec3 newDirection, float newSpeed) {
        super.setFiredDirectionAndSpeed(newDirection, newSpeed);
        setDeltaMovement(firedDirection.scale(newSpeed * 0.2));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLACK_HOLE_SIZE, 1.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    public float getSize() {
        return this.entityData.get(DATA_BLACK_HOLE_SIZE);
    }

    public void setSize(float size) {
        this.entityData.set(DATA_BLACK_HOLE_SIZE, Mth.clamp(size, MIN_SIZE, 10f));
    }
}