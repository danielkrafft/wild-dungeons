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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

import java.util.*;

public class BlackHole extends SelfGovernedEntity {

    private float size = 1.0f;
    private static final float MIN_SIZE = 0.1f;
    private static final float MAX_SIZE = 4f;
    private static final float GROWTH_PER_CONSUME = 0.01f;
    private static final float SHRINK_RATE = 0.001f;
    private static final float BASE_DECAY_RATE = 0.001f;
    private static final float MAX_DECAY_MULTIPLIER = 100f;
    private static final int DECAY_GRACE_PERIOD_TICKS = 10;
    private int decayCooldownTicks = 0;

    private static final double RADIUS_OUTER_SCALE = 2.0;
    private static final double RADIUS_INNER_SCALE = 0.75;
    private static final int MIN_DAMAGE = 1;
    private static final int MAX_DAMAGE = 10;
    private static final double MIN_PULL_STRENGTH = 1;
    private static final double MAX_PULL_STRENGTH = 5;
    private static final double PULL_BASE_MULTIPLIER = 3;
    private static final double PULL_FALLOFF_EXPONENT = 2;

    private static final int ENTITY_DAMAGE_COOLDOWN_TICKS = 10;
    private static final int MIN_BLOCKS_DESTROYED = 1;
    private static final int MAX_BLOCKS_DESTROYED = 500;
    private static final int BLOCK_DESTROY_SPREAD_TICKS = 3;
    private Queue<BlockPos> pendingDestruction = new ArrayDeque<>();
    private int destructionSpreadTickCounter = 0;

    private final Map<Integer, Integer> damageCooldowns = new HashMap<>();

    private static final EntityDataAccessor<Float> DATA_BLACK_HOLE_SIZE = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);
    private boolean hasReachedMaxSize = false;

    public BlackHole(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (wasFired) {
            if (!level().isClientSide) {
                if (getSize() >= MAX_SIZE) {
                    hasReachedMaxSize = true;
                    handleDecay();
                } else {
                    handleMoving();
                }
                handleEating();
                handleFusion();

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
        if (hasReachedMaxSize) {
            size = Mth.clamp(getSize() - SHRINK_RATE, MIN_SIZE, MAX_SIZE);
            setSize(size);
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        size = Mth.clamp(getSize() - SHRINK_RATE, MIN_SIZE, MAX_SIZE);
        setSize(size);

        float baseSpeed = 0.25f;
        float minSpeed = 0.02f;
        float speed = Mth.clamp(baseSpeed - (size * 0.01f), minSpeed, baseSpeed);

        Vec3 motion = firedDirection.normalize().scale(speed);

        // Lerp toward the largest black hole nearby (if still moving)
        BlackHole biggestNearby = null;
        float biggestSize = -1f;
        for (Entity entity : level().getEntities(this, new AABB(blockPosition()).inflate(getSize() * RADIUS_OUTER_SCALE * 2))) {
            if (entity instanceof BlackHole other && other != this && other.getSize() > biggestSize) {
                biggestSize = other.getSize();
                biggestNearby = other;
            }
        }

        if (biggestNearby != null && !getDeltaMovement().equals(Vec3.ZERO)) {
            Vec3 toTarget = biggestNearby.position().subtract(position()).normalize();
            Vec3 blended = motion.normalize().lerp(toTarget, 0.5).normalize().scale(motion.length());
            motion = blended;
        }

        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        lerpMotion(motion.x, motion.y, motion.z);
    }

    private void handleFusion() {
        List<Entity> nearby = level().getEntities(this, new AABB(blockPosition()).inflate(getSize() * RADIUS_INNER_SCALE));
        for (Entity entity : nearby) {
            if (entity instanceof BlackHole other && other != this) {
                if (this.getSize() > other.getSize()) {
                    float drain = Math.min(0.01f, other.getSize());
                    other.setSize(other.getSize() - drain);
                    this.setSize(this.getSize() + drain);

                    if (other.getSize() <= MIN_SIZE) {
                        other.triggerCollapseEffect();
                        other.discard();
                        WildDungeons.getLogger().info("Black hole fused and vanished.");
                    }
                }
            }
        }
    }

    private void handleDecay() {
        if (decayCooldownTicks > 0) decayCooldownTicks--;

        float decayAmount = BASE_DECAY_RATE;

        if (getSize() >= MAX_SIZE) {
            float cooldownProgress = 1.0f - (decayCooldownTicks / (float) DECAY_GRACE_PERIOD_TICKS);
            float decayMultiplier = Mth.lerp(cooldownProgress, 1.0f, MAX_DECAY_MULTIPLIER);
            decayAmount *= decayMultiplier;
        }

        setSize(getSize() - decayAmount);
    }

    private void handleEating() {
        damageCooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));

        double outerRadius = getSize() * RADIUS_OUTER_SCALE;
        double innerRadius = getSize() * RADIUS_INNER_SCALE;
        double outerDistSq = outerRadius * outerRadius;
        double innerDistSq = innerRadius * innerRadius;
        BlockPos center = blockPosition();
        Vec3 pullCenter = position().add(0, 0.75, 0);
        AABB zone = new AABB(center).inflate(outerRadius);

        List<Entity> entities = level().getEntities(this, zone);
        for (Entity target : entities) {
            if (target == this) continue;
            if (target instanceof BlackHole otherHole && otherHole.getSize() > 0 && otherHole != this) {
                Vec3 pullDir = position().add(0, 0.75, 0).subtract(otherHole.position().add(0, 0.75, 0)).normalize();
                double distSq = otherHole.position().distanceToSqr(position().add(0, 0.75, 0));
                double sizeFactor = (getSize() - MIN_SIZE) / (MAX_SIZE - MIN_SIZE);
                double normDist = distSq / (outerRadius * outerRadius);
                double clampedNormDist = Mth.clamp(normDist, 0.0, 1.0);
                double baseStrength = Math.max(0, sizeFactor * PULL_BASE_MULTIPLIER * (1.0 - Math.pow(clampedNormDist, PULL_FALLOFF_EXPONENT)));
                double pullStrength = Mth.clamp(baseStrength, MIN_PULL_STRENGTH, MAX_PULL_STRENGTH) * (1.0 - clampedNormDist);

                Vec3 currentMotion = otherHole.getDeltaMovement().scale(0.85);
                Vec3 pullVelocity = pullDir.scale(pullStrength);
                Vec3 newMotion = currentMotion.add(pullVelocity).scale(0.5);
                otherHole.setDeltaMovement(newMotion);
            }
            double distSq = target.position().distanceToSqr(pullCenter);
            double normDist = distSq / outerDistSq;

            if (distSq <= innerDistSq) {
                BlockPos flashPos = target.blockPosition().above();
                if (target instanceof BlackHole otherHole) {
                    if (otherHole.getSize() < getSize()) {
                        float siphonAmount = 0.01f; // Amount to siphon per tick
                        float newOtherSize = Math.max(MIN_SIZE, otherHole.getSize() - siphonAmount);
                        float siphoned = otherHole.getSize() - newOtherSize;
                        otherHole.setSize(newOtherSize);
                        setSize(getSize() + siphoned);
                        decayCooldownTicks = DECAY_GRACE_PERIOD_TICKS;
                        //flashAbsorbEffect(flashPos);
                    }
                } else if (target instanceof LivingEntity living) {
                } else if (target instanceof LivingEntity living) {
                    int id = living.getId();
                    if (damageCooldowns.getOrDefault(id, 0) <= 0) {
                        float damage = Mth.lerp((getSize() - MIN_SIZE) / (MAX_SIZE - MIN_SIZE), MIN_DAMAGE, MAX_DAMAGE);
                        living.hurt(blackHoleDamage(level().damageSources()), damage);
                        onEatThing();
                        damageCooldowns.put(id, ENTITY_DAMAGE_COOLDOWN_TICKS);
                        //flashAbsorbEffect(flashPos);
                    }
                } else {
                    target.discard();
                    onEatThing();
                }
            } else {
                Vec3 pullDir = pullCenter.subtract(target.position()).normalize();
                double sizeFactor = (getSize() - MIN_SIZE) / (MAX_SIZE - MIN_SIZE);
                double clampedNormDist = Mth.clamp(normDist, 0.0, 1.0);
                double baseStrength = Math.max(0, sizeFactor * PULL_BASE_MULTIPLIER * (1.0 - Math.pow(clampedNormDist, PULL_FALLOFF_EXPONENT)));
                double pullStrength = Mth.clamp(baseStrength, MIN_PULL_STRENGTH, MAX_PULL_STRENGTH) * (1.0 - normDist);

                Vec3 currentMotion = target.getDeltaMovement().scale(0.85);
                Vec3 pullVelocity = pullDir.scale(pullStrength);
                Vec3 newMotion = currentMotion.add(pullVelocity).scale(0.5);
                target.setDeltaMovement(newMotion);
            }
        }

        if (pendingDestruction.isEmpty()) {
            int blocksToSample = (int) Mth.lerp((getSize() - MIN_SIZE) / (MAX_SIZE - MIN_SIZE), MIN_BLOCKS_DESTROYED, MAX_BLOCKS_DESTROYED);
            for (int i = 0; i < blocksToSample; i++) {
                int dx = Mth.floor((random.nextDouble() - 0.5) * 2 * outerRadius);
                int dy = Mth.floor((random.nextDouble() - 0.5) * 2 * outerRadius);
                int dz = Mth.floor((random.nextDouble() - 0.5) * 2 * outerRadius);
                BlockPos pos = center.offset(dx, dy, dz);
                pendingDestruction.add(pos);
            }
            destructionSpreadTickCounter = BLOCK_DESTROY_SPREAD_TICKS;
        }

        int blocksThisTick = (int)Math.ceil((double)pendingDestruction.size() / destructionSpreadTickCounter);
        for (int i = 0; i < blocksThisTick && !pendingDestruction.isEmpty(); i++) {
            BlockPos pos = pendingDestruction.poll();
            if (pos == null || level().isEmptyBlock(pos)) continue;

            BlockState state = level().getBlockState(pos);
            FluidState fluid = state.getFluidState();
            double distSq = pos.distSqr(center);
            double normDist = distSq / outerDistSq;
            double chance = Mth.clamp(1.0 - Math.pow(normDist, 2.2), 0.05, 1.0);

            if (distSq <= innerDistSq || (random.nextDouble() < chance && state.getDestroySpeed(level(), pos) >= 0)) {
                if (!fluid.isEmpty()) {
                    level().setBlockAndUpdate(pos, Fluids.EMPTY.defaultFluidState().createLegacyBlock());
                    level().addParticle(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
                } else {
                    level().levelEvent(2001, pos, Block.getId(state));
                    level().destroyBlock(pos, false);
                    onEatThing();
                }
            }
        }

        if (destructionSpreadTickCounter > 0) destructionSpreadTickCounter--;
    }

    private void flashAbsorbEffect(BlockPos pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    pos.getY() + 0.5 + (serverLevel.random.nextDouble() * 0.3),
                    pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.3,
                    1,
                    0, 0, 0, 0
            );
        }
    }

    private void triggerCollapseEffect() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLASH,
                    getX(), getY(), getZ(),
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static DamageSource blackHoleDamage(DamageSources sources) {
        return sources.source(WDDamageTypes.BLACKHOLE.getKey());
    }

    public void onEatThing() {
        float newSize = getSize() + GROWTH_PER_CONSUME;
        setSize(newSize);
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
        this.entityData.set(DATA_BLACK_HOLE_SIZE, Mth.clamp(size, MIN_SIZE, MAX_SIZE + 1.0f));
    }
}
